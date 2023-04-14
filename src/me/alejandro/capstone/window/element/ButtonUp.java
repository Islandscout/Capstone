package me.alejandro.capstone.window.element;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ButtonUp extends Button {

    public ButtonUp(Canvas canvas) {
        super("", canvas);

        this.texture = textureUp_Shared;
        this.texturePressed = textureUp_PressedShared;
        this.textureLatched = textureUp_LatchedShared;

        this.setPosition(0, 0);
    }
}
