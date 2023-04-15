package me.alejandro.capstone.window;

import com.fazecast.jSerialComm.SerialPort;
import me.alejandro.capstone.arduino.*;
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

    //Window elements go here
    private final Tachometer tach;
    private final Plot plot;
    private final TorqueMeter torqueMeter;
    public ValvePanel valvePanel;
    public ThrottlePanel throttlePanel;

    public ControllerRPM controllerRPM;
    public ControllerTorque controllerTorque;

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

        this.valvePanel = new ValvePanel(this.canvas, 0.5, -0.51);
        this.throttlePanel = new ThrottlePanel(this.canvas, 0, -0.51);

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

        for(SerialPort port : ports) {

            System.out.println("NAME: " + port.getSystemPortName() + ", BAUD RATE: " + port.getBaudRate());

            ArduinoListener listener = new ArduinoListener(port.getSystemPortName(), this);
            listener.getArduino().openConnection();
            listener.getArduino().getSerialPort().addDataListener(listener);

        }
        System.out.println(ports.length + " serial device(s) detected");
        System.out.println();

        if(ports.length == 0) {
            System.out.println("No ports found, try again?");
        }
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        tach.draw(g, partialTick);
        plot.draw(g, partialTick);
        torqueMeter.draw(g, partialTick);
        valvePanel.draw(g, partialTick);
        throttlePanel.draw(g, partialTick);

        for(Button button : buttons) {
            button.draw(g, partialTick);
        }

    }

    double frame;

    @Override
    public void tick() {

        if(mouseDown || wasMouseDown) {
            MouseAction mAction = mouseDown ? MouseAction.BUTTON_HOLD : MouseAction.BUTTON_RELEASE;

            for(Button button : buttons) {
                button.testClickEvent(getMousePos(), mAction);
            }

            this.valvePanel.testClickEvent(getMousePos(), mAction);
            this.throttlePanel.testClickEvent(getMousePos(), mAction);

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
        /*if(this.arduino != null && this.arduino.getSerialPort() != null) {
            this.arduino.getSerialPort().removeDataListener();
            this.arduino.closeConnection();
        }*/
    }

    public void updateTorque(double value) {
        this.torque = value;
    }

    public void updateRPM(double value) {
        this.rpm = value;
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
}
