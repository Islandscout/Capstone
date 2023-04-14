package me.alejandro.capstone.window;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.input.MouseAction;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Vector3D;
import me.alejandro.capstone.window.element.*;
import me.alejandro.capstone.window.element.Button;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class WindowDashboard extends Window {

    private Arduino arduino;
    private ArduinoListener listener;

    //Window elements go here
    private final Tachometer tach;
    private final Plot plot;
    private final TorqueMeter torqueMeter;
    private final ValveControl valveControl;
    private final ThrottleControl throttleControl;

    private final List<Button> buttons;
    private final Button plotTorqueButton;
    private final Button plotPowerButton;
    private final Button recordButton;
    private final Button clearPlotButton;
    private final Button csvButton;

    private volatile double torque, rpm;

    private boolean inputC, inputR;

    public WindowDashboard() {
        super("dashboard", 680, 420);

        this.tach = new Tachometer();
        this.tach.posX = -0.65;

        this.plot = new Plot();
        this.plot.posX = 0.33;
        this.plot.posY = 0.1;

        this.torqueMeter = new TorqueMeter(7);
        this.torqueMeter.transform(0.1, -0.6, -0.5);

        this.valveControl = new ValveControl(this.canvas, 0.5, -0.51);
        this.throttleControl = new ThrottleControl(this.canvas, 0, -0.51);

        this.buttons = new ArrayList<>();

        this.plotPowerButton = new ButtonLarge("Plot Power", this.canvas);
        this.plotPowerButton.setPosition(0.1, 0.5);
        this.buttons.add(this.plotPowerButton);

        this.plotTorqueButton = new ButtonLarge("Plot Torque", this.canvas);
        this.plotTorqueButton.setPosition(0.45, 0.5);
        this.buttons.add(this.plotTorqueButton);

        this.recordButton = new ButtonLarge("REC [R]", this.canvas);
        this.recordButton.setTextColor(Color.RED);
        this.recordButton.setPosition(-0.1, -0.35);
        this.recordButton.mode = Button.ButtonMode.TOGGLE;
        this.buttons.add(this.recordButton);

        this.clearPlotButton = new ButtonLarge("Clear Plot [C]", this.canvas);
        this.clearPlotButton.setPosition(0.2, -0.35);
        this.clearPlotButton.setExecution(new Runnable() {
            @Override
            public void run() {
                plot.getData().clear();
            }
        });
        this.buttons.add(this.clearPlotButton);

        this.csvButton = new ButtonLarge("Save to CSV", this.canvas);
        this.csvButton.setPosition(0.7, -0.35);
        this.csvButton.setExecution(new Runnable() {
            @Override
            public void run() {
                exportCSV();
            }
        });
        this.buttons.add(this.csvButton);

        BufferedImage bg = null;
        try {
            bg = ImageIO.read(new File("res/bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setBackground(bg);
        //TODO use a "check engine light" as app icon
        this.setTitle("Engine Dynamometer Interface");
    }

    public void initArduino() {

        System.out.println("Detecting serial devices...");

        SerialPort[] ports = SerialPort.getCommPorts();

        /* //TODO Allow user to choose a port

        String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

        JFrame frame = new JFrame("ComboBoxDemo2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Legit skidded from Oracle docs
        JComboBox petList = new JComboBox(petStrings);
        petList.setSelectedIndex(4);
        petList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String petName = (String)cb.getSelectedItem();
                cb.setSelectedItem(cb.getSelectedIndex());
            }
        });
         */

        for(SerialPort port : ports) {
            System.out.println("NAME: " + port.getSystemPortName() + ", BAUD RATE: " + port.getBaudRate());
            //this.arduino = new Arduino(port.getSystemPortName(), port.getBaudRate());
            //this.arduino.openConnection();
        }
        System.out.println(ports.length + " serial devices detected");
        System.out.println();

        if(ports.length == 0) {
            System.out.println("No ports found, try again?");
        } else {
            //Set up event listener for Arduino. This is just like Bukkit.
            // Of course we need to sync these threads up.
            this.listener = new ArduinoListener();
            this.arduino.getSerialPort().addDataListener(this.listener);
        }
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        tach.draw(g, partialTick);
        plot.draw(g, partialTick);
        torqueMeter.draw(g, partialTick);
        valveControl.draw(g, partialTick);
        throttleControl.draw(g, partialTick);

        for(Button button : buttons) {
            button.draw(g, partialTick);
        }

        //g.setColor(Color.WHITE);
        //g.getGraphics().drawString("DAQ Arduino connected on port: ", 0, 15);
        //g.getGraphics().drawString("Controller Arduino connected on port: ", 0, 30);

    }

    double frame;

    @Override
    public void tick() {

        if(mouseDown || wasMouseDown) {
            MouseAction mAction = mouseDown ? MouseAction.BUTTON_HOLD : MouseAction.BUTTON_RELEASE;

            for(Button button : buttons) {
                button.testClickEvent(getMousePos(), mAction);
            }

            this.valveControl.testClickEvent(getMousePos(), mAction);
            this.throttleControl.testClickEvent(getMousePos(), mAction);

        }

        if(getKey(KeyEvent.VK_C)) {
            if(!this.inputC) {
                clearPlotButton.fireShortcut();
                this.inputC = true;
            }
        } else {
            this.inputC = false;
        }

        if(getKey(KeyEvent.VK_R)) {
            if(!this.inputR) {
                recordButton.fireShortcut();
                this.inputR = true;
            }
        } else {
            this.inputR = false;
        }

        this.plot.hold = !this.recordButton.engaged;

        frame += 10;
        double rpm = frame;

        tach.setRpm((int)rpm);
        tach.getModel().getTransformation()
                .setIdentity()
                .scale(0.32)
                .rotate(new Vector3D(0, 0, 1), rpm)
                .translate(new Vector3D(tach.posX, 0, 0));


        plot.addPoint(frame, Math.sqrt(frame / 10), 1);
        torqueMeter.setValue(torque / 25);

    }

    @Override
    protected void onClose() {
        if(this.arduino != null && this.arduino.getSerialPort() != null) {
            this.arduino.getSerialPort().removeDataListener();
            this.arduino.closeConnection();
        }
    }

    private void updateTorque(double value) {
        this.torque = value;
    }

    private void exportCSV() {
        Thread saveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JFrame parentFrame = new JFrame();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Export as CSV format");

                int userSelection = fileChooser.showSaveDialog(parentFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();

                    try {
                        FileWriter fw = new FileWriter(fileToSave, false);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw);

                        StringJoiner rpmValues = new StringJoiner(",");
                        StringJoiner torqueValues = new StringJoiner(",");
                        StringJoiner powerValues = new StringJoiner(",");
                        for(Vector3D p : plot.getData()) {
                            rpmValues.add("" + p.x);
                            torqueValues.add("" + p.y);
                            powerValues.add("" + p.z);
                        }
                        out.println(rpmValues);
                        out.println(torqueValues);
                        out.println(powerValues);

                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                parentFrame.dispose();
            }
        });
        saveThread.start();
    }

    class ArduinoListener implements SerialPortPacketListener {

        private StringBuilder lineSB;
        private boolean syncd;

        ArduinoListener() {
            this.lineSB = new StringBuilder();
        }

        @Override
        public int getPacketSize() {
            return 1; //one-byte packets should be good for our application
        }

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public void serialEvent(SerialPortEvent e) {
            //TODO we grab the RPM and torque here.
            // Look for end-of-line character (\n) to mark the end of the message
            //TODO hmm, maybe we want to clear the buffer before reading in our first message?
            //Don't use Arduino#serialRead(). It's not sync'd.

            for(byte element : e.getReceivedData()) {

                boolean endl = (char) element == 10;

                if(syncd) {

                    if(endl) {
                        try {
                            updateTorque(Double.parseDouble(this.lineSB.toString()));
                        } catch (NumberFormatException exception) {
                            exception.printStackTrace();
                        }
                        this.lineSB.setLength(0); //clear (avoids a "new")
                    } else {
                        this.lineSB.append((char)element);
                    }

                } else if(endl) {
                    this.syncd = true;
                }
            }
        }
    }
}
