package me.alejandro.capstone.arduino;

import arduino.Arduino;
import me.alejandro.capstone.window.Window;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerTorque extends Controller {

    public ControllerTorque(Arduino arduino, Window window) {
        super(arduino, window);
    }

    @Override
    public void onMessageReceive(String msg) {
        try {
            System.out.println(msg);
            //dashboard.updateTorque(Double.parseDouble(msg));
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
    }
}
