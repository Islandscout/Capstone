package me.alejandro.capstone.window;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.render.GraphicsWrapper;

import javax.swing.*;
import java.awt.*;

public class WindowDashboard extends Window {

    //TODO the graphic models go here
    private Arduino arduino;
    private ArduinoListener listener;

    public WindowDashboard() {
        super("dashboard", 600, 400);
        this.setBackground(Color.BLACK);

        SerialPort[] ports = SerialPort.getCommPorts();

        //TODO Allow user to choose a port
        for(SerialPort port : ports) {
            this.arduino = new Arduino(port.getSystemPortName(), port.getBaudRate());
            this.arduino.openConnection();
            break;

        }

        //debug
        System.out.println("success? " + this.arduino.getSerialPort().isOpen());

        //Set up event listener for Arduino. This is just like Bukkit.
        // Of course we need to sync these threads up.
        this.listener = new ArduinoListener();
        this.arduino.getSerialPort().addDataListener(this.listener);

    }

    int frame;

    @Override
    public void render(GraphicsWrapper g, double partialTick) {

        g.setColor(Color.WHITE);

        double[] xPoints = {
                -0.2,
                Math.cos(0.01 * frame++),
                0.0
        };

        double[] yPoints = {
                0.0,
                Math.sin(0.01 * frame++),
                0.2
        };

        g.fillTriangle(xPoints, yPoints);
    }

    @Override
    public void tick() {

    }

    @Override
    protected void onClose() {
        this.arduino.getSerialPort().removeDataListener();
        this.arduino.closeConnection();
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
