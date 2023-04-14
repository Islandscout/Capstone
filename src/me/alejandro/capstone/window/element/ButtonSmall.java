package me.alejandro.capstone.window.element;

import java.awt.*;

public class ButtonSmall extends Button {

    public ButtonSmall(String text, Canvas canvas) {
        super(text, canvas);

        this.texture = textureSmall_Shared;
        this.texturePressed = textureSmall_PressedShared;
        this.textureLatched = textureSmall_LatchedShared;

        this.textOffsetX = -0.03;

        this.setPosition(0, 0);
    }
}
