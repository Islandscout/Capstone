package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerThrottle extends Controller {

    public ControllerThrottle(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        super(arduino, window, listener);
    }

    public void rpm0() {
        arduino.serialWrite("6");
    }

    public void rpm2() {
        arduino.serialWrite("1");
    }

    public void rpm4() {
        arduino.serialWrite("2");
    }

    public void rpm6() {
        arduino.serialWrite("3");
    }

    public void rpm8() {
        arduino.serialWrite("4");
    }

    public void rpm10() {
        arduino.serialWrite("5");
    }

    @Override
    public void onMessageReceive(String msg) {

    }
}
