package me.alejandro.capstone.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input implements KeyListener {

    private static boolean[] currentKeys = new boolean[256];

    public static boolean getKey(int keyCode) {
        return currentKeys[keyCode];
    }

    public void keyPressed(KeyEvent e) {
        currentKeys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        currentKeys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) {

    }
}
