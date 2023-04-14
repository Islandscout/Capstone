package me.alejandro.capstone.window.element;

import java.awt.*;

public class ButtonLarge extends Button {

    public ButtonLarge(String text, Canvas canvas) {
        super(text, canvas);

        this.texture = textureShared;
        this.texturePressed = texturePressedShared;
        this.textureLatched = textureLatchedShared;

        this.textOffsetX = -0.1;

        this.setPosition(0, 0);
    }
}
