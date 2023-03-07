package me.alejandro.capstone.window;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Vector3D;
import me.alejandro.capstone.window.element.Tachometer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WindowDashboard extends Window {

    //TODO the graphic models go here
    private Arduino arduino;
    private ArduinoListener listener;

    //Window elements go here
    private Tachometer tach;

    public WindowDashboard() {
        super("dashboard", 600, 400);

        this.tach = new Tachometer();
        this.tach.posX = -0.6;

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

        g.setColor(Color.RED);
    }

    double frame;

    @Override
    public void tick() {

        tach.getModel().getTransformation()
                .setIdentity()
                .scale(0.4)
                .rotate(new Vector3D(0, 0, 1), frame -= 0.1)
                .translate(new Vector3D(-0.6, 0, 0));

    }

    @Override
    protected void onClose() {
        if(this.arduino != null && this.arduino.getSerialPort() != null) {
            this.arduino.getSerialPort().removeDataListener();
            this.arduino.closeConnection();
        }
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
