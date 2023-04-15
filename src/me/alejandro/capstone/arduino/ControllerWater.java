package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;

public class ControllerWater extends Controller {

    public ControllerWater(Arduino arduino, Window window) {
        super(arduino, window);
    }

    @Override
    public void onMessageReceive(String msg) {

    }
}
