package me.alejandro.capstone.arduino;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import me.alejandro.capstone.window.WindowDashboard;

public class ArduinoListener implements SerialPortPacketListener {

    //It's a miracle how I got this design to work
    //No time to improve this

    private Arduino arduino;
    private Controller controller;
    private boolean syncd;
    private final StringBuilder lineSB;

    public ArduinoListener(String portName, WindowDashboard window) {

        this.arduino = new Arduino(portName, 9600);

        ArduinoListener selfListener = this;

        //dummy controller
        this.controller = new Controller(arduino, window, this) {
            @Override
            public void onMessageReceive(String msg) {
                if(msg.charAt(0) == 'W') {
                    System.out.println("Detected water valve arduino");
                    window.valvePanel.setController(new ControllerWater(arduino, window, selfListener));
                    controller = window.valvePanel.getController();
                } else if(msg.charAt(0) == 'R') {
                    System.out.println("Detected RPM sensor arduino");
                    window.controllerRPM = new ControllerRPM(arduino, window, selfListener);
                    controller = window.controllerRPM;
                } else if(msg.charAt(0) == 'F') {
                    System.out.println("Detected torque sensor arduino");
                    window.controllerTorque = new ControllerTorque(arduino, window, selfListener);
                    controller = window.controllerTorque;
                } else if(msg.charAt(0) == 'T') {
                    System.out.println("Detected throttle position arduino");
                    window.throttlePanel.setController(new ControllerThrottle(arduino, window, selfListener));
                    controller = window.throttlePanel.getController();
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

                } else if(element != 13) { // I don't know what this character is but I don't like it
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
