package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerThrottle extends Controller {

    public ControllerThrottle(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        super(arduino, window, listener);
    }

    @Override
    public void onMessageReceive(String msg) {

    }
}
