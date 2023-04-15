package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerTorque extends Controller {

    public ControllerTorque(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        super(arduino, window, listener);
    }

    @Override
    public void onMessageReceive(String msg) {
        try {
            System.out.println(msg);
            window.updateTorque(Double.parseDouble(msg));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
    }
}
