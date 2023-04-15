package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;

public class ControllerThrottle extends Controller {

    public ControllerThrottle(Arduino arduino, Window window) {
        super(arduino, window);
    }

    @Override
    public void onMessageReceive(String msg) {

    }
}
