package me.alejandro.capstone.window;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.input.MouseAction;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Point2D;
import me.alejandro.capstone.util.Vector3D;
import me.alejandro.capstone.window.element.Button;
import me.alejandro.capstone.window.element.Meter;
import me.alejandro.capstone.window.element.Plot;
import me.alejandro.capstone.window.element.Tachometer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.StringJoiner;

public class WindowDashboard extends Window {

    private Arduino arduino;
    private ArduinoListener listener;

    //Window elements go here
    private Tachometer tach;
    private Plot plot;
    private Meter torqueMeter;
    private Button plotTorqueButton; //TODO
    private Button plotPowerButton; //TODO
    private Button recordButton; //TODO
    private Button clearPlotButton; //TODO clear plot
    private Button csvButton; //TODO Save to CSV

    private volatile double torque, rpm;

    private boolean inputC, inputR;

    private boolean recording;

    public WindowDashboard() {
        super("dashboard", 680, 420);

        this.tach = new Tachometer();
        this.tach.posX = -0.65;

        this.plot = new Plot();
        this.plot.posX = 0.33;
        this.plot.posY = 0.1;

        this.torqueMeter = new Meter(7, "Torque");
        this.torqueMeter.transform(0.1, -0.6, -0.5);

        this.plotPowerButton = new Button("Plot Power", this.canvas);
        this.plotPowerButton.setPosition(0.1, 0.5);

        this.plotTorqueButton = new Button("Plot Torque", this.canvas);
        this.plotTorqueButton.setPosition(0.45, 0.5);

        this.recordButton = new Button("REC [R]", this.canvas);
        this.recordButton.setTextColor(Color.RED);
        this.recordButton.setPosition(-0.1, -0.35);
        this.recordButton.setExecution(new Runnable() {
            @Override
            public void run() {
                recordButton.pressed = !recordButton.pressed;
            }
        });

        this.clearPlotButton = new Button("Clear Plot [C]", this.canvas);
        this.clearPlotButton.setPosition(0.2, -0.35);
        this.clearPlotButton.setExecution(new Runnable() {
            @Override
            public void run() {
                plot.getData().clear();
            }
        });

        this.csvButton = new Button("Save to CSV", this.canvas);
        this.csvButton.setPosition(0.7, -0.35);
        this.csvButton.setExecution(new Runnable() {
            @Override
            public void run() {
                exportCSV();
            }
        });

        BufferedImage bg = null;
        try {
            bg = ImageIO.read(new File("res/bg.png"));
        } catch (IOException e) {
            System.out.println("Could not load background texture! Is it missing in the JAR?");
            e.printStackTrace();
        }

        this.setBackground(bg);
        //TODO use a "check engine light" as app icon
        this.setTitle("Engine Dynamometer Interface");
    }

    public void initArduino() {
        SerialPort[] ports = SerialPort.getCommPorts();

        //TODO Allow user to choose a port
        for(SerialPort port : ports) {
            this.arduino = new Arduino(port.getSystemPortName(), port.getBaudRate());
            this.arduino.openConnection();
            break;

        }

        if(ports.length == 0) {
            System.out.println("No ports found, try again?");
        }

        //debug
        System.out.println("success? " + this.arduino.getSerialPort().isOpen());

        //Set up event listener for Arduino. This is just like Bukkit.
        // Of course we need to sync these threads up.
        this.listener = new ArduinoListener();
        this.arduino.getSerialPort().addDataListener(this.listener);
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        tach.draw(g, partialTick);
        plot.draw(g, partialTick);
        torqueMeter.draw(g, partialTick);

        plotPowerButton.draw(g, partialTick);
        plotTorqueButton.draw(g, partialTick);
        recordButton.draw(g, partialTick);
        clearPlotButton.draw(g, partialTick);
        csvButton.draw(g, partialTick);

    }

    double frame;

    @Override
    public void tick() {

        //TODO
        // honestly, I would put these buttons in a list and loop through them on mouse click
        this.plotTorqueButton.testClickEvent(getMousePos(), MouseAction.BUTTON_CLICK);

        if(getKey(KeyEvent.VK_C)) {
            if(!this.inputC) {
                clearPlotButton.execute();
                this.inputC = true;
            }
        } else {
            this.inputC = false;
        }

        if(getKey(KeyEvent.VK_R)) {
            if(!this.inputR) {
                recordButton.execute();
                this.inputR = true;
            }
        } else {
            this.inputR = false;
        }

        this.plot.hold = !this.recordButton.pressed;


        frame++;
        double rpm = frame;


        tach.setRpm((int)rpm);
        tach.getModel().getTransformation()
                .setIdentity()
                .scale(0.32)
                .rotate(new Vector3D(0, 0, 1), rpm)
                .translate(new Vector3D(tach.posX, 0, 0));

        plot.addPoint(new Point2D(rpm, torque));
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
                        for(Point2D p : plot.getData()) {
                            rpmValues.add("" + p.x);
                            torqueValues.add("" + p.y);
                            powerValues.add("" + p.y); //TODO sigh... you forgot power
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
