package me.alejandro.capstone.arduino;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.window.Window;

public class ArduinoListener implements SerialPortPacketListener {

    private Arduino arduino;
    private Controller controller;
    private boolean syncd;
    private final StringBuilder lineSB;

    public ArduinoListener(String portName, Window window) {

        this.arduino = new Arduino(portName, 9600);

        //dummy controller
        this.controller = new Controller(arduino, window) {
            @Override
            public void onMessageReceive(String msg) {
                if(msg.charAt(0) == 'W') {
                    System.out.println("Detected water valve arduino");
                    controller = new ControllerWater(arduino, window);
                } else if(msg.charAt(0) == 'R') {
                    System.out.println("Detected RPM sensor arduino");
                    controller = new ControllerRPM(arduino, window);
                } else if(msg.charAt(0) == 'F') {
                    System.out.println("Detected torque sensor arduino");
                    controller = new ControllerTorque(arduino, window);
                } else if(msg.charAt(0) == 'T') {
                    System.out.println("Detected throttle position arduino");
                    controller = new ControllerThrottle(arduino, window);
                }
            }
        };

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
    public void serialEvent(SerialPortEvent event) {

        //Don't use Arduino#serialRead(). It's not sync'd.

        for(byte element : event.getReceivedData()) {

            boolean endl = (char) element == 10;

            if(syncd) {

                if(endl) {

                    this.controller.onMessageReceive(this.lineSB.toString());
                    this.lineSB.setLength(0); //clear (avoids a "new")

                } else {
                    this.lineSB.append((char)element);
                }

            } else if(endl) {
                this.syncd = true;
            }
        }
    }

    public Arduino getArduino() {
        return arduino;
    }
}
