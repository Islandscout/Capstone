package me.alejandro.capstone.window.element;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ButtonDown extends Button {

    public ButtonDown(Canvas canvas) {
        super("", canvas);

        this.texture = textureDown_Shared;
        this.texturePressed = textureDown_PressedShared;
        this.textureLatched = textureDown_LatchedShared;

        this.setPosition(0, 0);
    }
}
