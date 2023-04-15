package me.alejandro.capstone.arduino;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPortEvent;
import me.alejandro.capstone.window.Window;
import me.alejandro.capstone.window.WindowDashboard;

public class ControllerRPM extends Controller {

    public ControllerRPM(Arduino arduino, WindowDashboard window, ArduinoListener listener) {
        super(arduino, window, listener);
    }

    @Override
    public void onMessageReceive(String msg) {

    }
}
