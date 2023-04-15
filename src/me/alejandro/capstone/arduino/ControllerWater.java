package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerWater extends Controller {

    public ControllerWater(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        super(arduino, window, listener);
    }

    public void coarseUp() {
        arduino.serialWrite("2");
    }

    public void fineUp() {
        arduino.serialWrite("4");
    }

    public void coarseDown() {
        arduino.serialWrite("1");
    }

    public void fineDown() {
        arduino.serialWrite("3");
    }

    public void resetPosition() {
        arduino.serialWrite("5");
    }

    @Override
    public void onMessageReceive(String msg) {
        try {
            window.valvePanel.valveValue = Integer.parseInt(msg);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
