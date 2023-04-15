package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerRPM extends Controller {

    public ControllerRPM(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        super(arduino, window, listener);
    }

    @Override
    public void onMessageReceive(String msg) {
        try {
            window.updateRPM(Double.parseDouble(msg));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
    }
}
