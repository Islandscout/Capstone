package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;
import me.alejandro.capstone.window.WindowDashboard;

public abstract class Controller {

    protected Arduino arduino;
    protected ArduinoListener listener; //I dont see any use for this field but i dont care im done with this mess
    protected WindowDashboard window;

    public Controller(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        this.arduino = arduino;
        this.listener = listener;
        this.window = window;
    }

    protected abstract void onMessageReceive(String msg);

    protected void sendMessage(String msg) {
        arduino.serialWrite(msg);
    }

    public Arduino getArduino() {
        return arduino;
    }
}
