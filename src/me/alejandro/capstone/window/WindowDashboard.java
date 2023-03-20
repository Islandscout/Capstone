package me.alejandro.capstone.window;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Point;
import me.alejandro.capstone.util.Vector3D;
import me.alejandro.capstone.window.element.Button;
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
    private Button plotTorqueButton; //TODO
    private Button plotPowerButton; //TODO
    private Button recordButton; //TODO
    private Button clearPlotButton; //TODO clear plot
    private Button csvButton; //TODO Save to CSV

    private boolean recording;

    public WindowDashboard() {
        super("dashboard", 680, 420);

        this.tach = new Tachometer();
        this.tach.posX = -0.65;

        this.plot = new Plot();
        this.plot.posX = 0.33;
        this.plot.posY = 0.1;

        this.plotPowerButton = new Button("Plot Power");
        this.plotPowerButton.posX = 0.1;
        this.plotPowerButton.posY = 0.5;
        this.plotTorqueButton = new Button("Plot Torque");
        this.plotTorqueButton.posX = 0.45;
        this.plotTorqueButton.posY = 0.5;
        this.recordButton = new Button("REC [R]");
        this.recordButton.setTextColor(Color.RED);
        this.recordButton.posX = -0.1;
        this.recordButton.posY = -0.35;

        this.clearPlotButton = new Button("Clear Plot [C]");
        this.clearPlotButton.posX = 0.2;
        this.clearPlotButton.posY = -0.35;
        this.clearPlotButton.setExecution(new Runnable() {
            @Override
            public void run() {
                plot.getData().clear();
            }
        });


        this.csvButton = new Button("Save to CSV");
        this.csvButton.posX = 0.7;
        this.csvButton.posY = -0.35;
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

        plotPowerButton.draw(g, partialTick);
        plotTorqueButton.draw(g, partialTick);
        recordButton.draw(g, partialTick);
        clearPlotButton.draw(g, partialTick);
        csvButton.draw(g, partialTick);

    }

    double frame;

    @Override
    public void tick() {

        if(getKey(KeyEvent.VK_C) && !clearPlotButton.pressed) {
            clearPlotButton.execute();
        }

        frame += 0.03;
        double rpm = 20 * (Math.sin(0.3 * Math.sin(frame) * frame) + 1.2);
        double torque = 200 * (Math.cos(frame) + 1.2);


        tach.setRpm((int)rpm);
        tach.getModel().getTransformation()
                .setIdentity()
                .scale(0.32)
                .rotate(new Vector3D(0, 0, 1), rpm)
                .translate(new Vector3D(tach.posX, 0, 0));

        plot.addPoint(new Point(rpm, torque));

    }

    @Override
    protected void onClose() {
        if(this.arduino != null && this.arduino.getSerialPort() != null) {
            this.arduino.getSerialPort().removeDataListener();
            this.arduino.closeConnection();
        }
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
                        for(Point p : plot.getData()) {
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

            StringBuilder sb = new StringBuilder();
            for(byte element : e.getReceivedData()) {
                if((char) element == 10) {
                    sb.append("\\n");
                }
                else {
                    sb.append((char) element);
                }
            }
            System.out.println("data: " + sb);
        }
    }
}
