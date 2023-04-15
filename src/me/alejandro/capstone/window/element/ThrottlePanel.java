package me.alejandro.capstone.window.element;

import me.alejandro.capstone.arduino.ControllerThrottle;
import me.alejandro.capstone.arduino.ControllerWater;
import me.alejandro.capstone.input.MouseAction;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThrottlePanel implements Drawable {

    public static final int THROTTLE_MAX_VALUE = 5600;

    private ControllerThrottle controller;
    private final Canvas canvas;
    private final List<Button> buttons;
    private final Button rpm2, rpm4, rpm6, rpm8, rpm10, rpmReset;

    private final double posX, posY;

    private int throttlePos;

    public ThrottlePanel(Canvas canvas, double posX, double posY) {

        this.posX = posX;
        this.posY = posY;

        this.canvas = canvas;
        this.buttons = new ArrayList<>();

        this.rpmReset = new ButtonSmall("IDLE", this.canvas);
        this.rpmReset.setPosition(posX - 0.2, posY + 0.04);
        this.rpmReset.setExecution(new Runnable() {
            @Override
            public void run() {
                controller.rpm0();
            }
        });
        this.buttons.add(this.rpmReset);

        this.rpm2 = new ButtonSmall("2k", this.canvas);
        this.rpm2.setPosition(posX - 0.1, posY + 0.04);
        this.rpm2.setExecution(new Runnable() {
            @Override
            public void run() {
                controller.rpm2();
            }
        });
        this.buttons.add(this.rpm2);

        this.rpm4 = new ButtonSmall("4k", this.canvas);
        this.rpm4.setPosition(posX, posY + 0.04);
        this.rpm4.setExecution(new Runnable() {
            @Override
            public void run() {
                controller.rpm4();
            }
        });
        this.buttons.add(this.rpm4);

        this.rpm6 = new ButtonSmall("6k", this.canvas);
        this.rpm6.setPosition(posX - 0.2, posY - 0.04);
        this.rpm6.setExecution(new Runnable() {
            @Override
            public void run() {
                controller.rpm6();
            }
        });
        this.buttons.add(this.rpm6);

        this.rpm8 = new ButtonSmall("8k", this.canvas);
        this.rpm8.setPosition(posX - 0.1, posY - 0.04);
        this.rpm8.setExecution(new Runnable() {
            @Override
            public void run() {
                controller.rpm8();
            }
        });
        this.buttons.add(this.rpm8);

        this.rpm10 = new ButtonSmall("10k", this.canvas);
        this.rpm10.setPosition(posX, posY - 0.04);
        this.rpm10.setExecution(new Runnable() {
            @Override
            public void run() {
                controller.rpm10();
            }
        });
        this.buttons.add(this.rpm10);
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        int pixelPosX = g.cartesianToImgX(this.posX);
        int pixelPosY = g.cartesianToImgY(this.posY);

        g.setColor(Color.ORANGE);
        g.getGraphics().drawString("RPM Selector", pixelPosX - 70,  pixelPosY + 40);
        //g.getGraphics().drawString("Position: " + Math.round(this.throttlePos * 100D / THROTTLE_MAX_VALUE) + "%", pixelPosX - 70, pixelPosY + 55);

        for(Button button : buttons) {
            button.draw(g, partialTick);
        }

    }

    public void testClickEvent(Point2D pos, MouseAction mAction) {
        for(Button button : buttons) {
            button.testClickEvent(pos, mAction);
        }
    }

    public ControllerThrottle getController() {
        return controller;
    }

    public void setController(ControllerThrottle controller) {
        this.controller = controller;
    }
}
