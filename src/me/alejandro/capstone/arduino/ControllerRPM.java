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
            //This should be the number of microseconds elapsed since the last magnet detection
            long microElapsed = Long.parseLong(msg);

            //This should fire 12 times per revolution
            //double minPerRev = /*12*/ 4 * microElapsed * 60E-6; //i think this is right
            double minPerRev = 4 * microElapsed * (1E-6 / 60); //i think this is wrong
            window.updateRPM(33 / minPerRev);
            //System.out.println("PULSE! That took " + microElapsed / 1E6 + " sec, so " + 4 * microElapsed / 1E6 + " sec per rev");

        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
    }
}
