package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;

public abstract class Controller {

    protected Arduino arduino;
    protected Window window;

    public Controller(Arduino arduino, Window window) {
        this.arduino = arduino;
        this.window = window;
    }

    protected abstract void onMessageReceive(String msg);

    //TODO write to arduino
}
