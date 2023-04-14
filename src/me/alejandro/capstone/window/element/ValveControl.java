package me.alejandro.capstone.window.element;

import me.alejandro.capstone.input.MouseAction;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Point2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ValveControl implements Drawable {

    public static final int VALVE_MAX_VALUE = 5600;

    private final Canvas canvas;
    private final List<Button> buttons;
    private final Button valveCoarseUp, valveCoarseDown, valveFineUp, valveFineDown;

    private final double posX, posY;

    private int valveValue;

    public ValveControl(Canvas canvas, double posX, double posY) {

        this.posX = posX;
        this.posY = posY;

        this.canvas = canvas;
        this.buttons = new ArrayList<>();

        this.valveCoarseUp = new ButtonUp(this.canvas);
        this.valveCoarseUp.setPosition(posX, posY + 0.08);
        this.buttons.add(valveCoarseUp);

        this.valveFineUp = new ButtonUp(this.canvas);
        this.valveFineUp.setPosition(posX, posY + 0.03);
        this.buttons.add(valveFineUp);

        this.valveCoarseDown = new ButtonDown(this.canvas);
        this.valveCoarseDown.setPosition(posX, posY - 0.03);
        this.buttons.add(valveCoarseDown);

        this.valveFineDown = new ButtonDown(this.canvas);
        this.valveFineDown.setPosition(posX, posY - 0.08);
        this.buttons.add(valveFineDown);

    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        int pixelPosX = g.cartesianToImgX(this.posX);
        int pixelPosY = g.cartesianToImgY(this.posY);

        g.setColor(Color.CYAN);
        g.getGraphics().drawString("Water Valve", pixelPosX + 35,  pixelPosY - 5);
        g.getGraphics().drawString("Position: " + Math.round(this.valveValue * 100D / VALVE_MAX_VALUE) + "%", pixelPosX + 35, pixelPosY + 10);

        for(Button button : buttons) {
            button.draw(g, partialTick);
        }

    }

    public void testClickEvent(Point2D pos, MouseAction mAction) {
        for(Button button : buttons) {
            button.testClickEvent(pos, mAction);
        }
    }
}
