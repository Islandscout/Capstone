package me.alejandro.capstone.window.element;

import me.alejandro.capstone.input.MouseAction;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.BoundingBox;
import me.alejandro.capstone.util.Point2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Button implements Drawable {

    public boolean pressed;
    private Runnable execution;

    private BufferedImage texture;
    private BufferedImage texturePressed;
    private double posX, posY;
    private String text;
    private Color textColor;
    private BoundingBox boundingBox;
    private Canvas canvas;

    public Button(String text, Canvas canvas) {

        this.text = text;
        this.textColor = Color.BLACK;
        this.canvas = canvas;

        try {
            this.texture = ImageIO.read(new File("res/button.png"));
            this.texturePressed = ImageIO.read(new File("res/button_pressed.png"));
        } catch (IOException e) {
            System.out.println("Could not load button textures! Is it missing in the JAR?");
            e.printStackTrace();
        }

        this.setPosition(0, 0);
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {
        if(this.pressed) {
            g.drawImage(this.texturePressed, this.posX, this.posY);
        } else {
            g.drawImage(this.texture, this.posX, this.posY);
        }
        g.setColor(this.textColor);
        g.getGraphics().drawString(text, g.cartesianToImgX(this.posX - 0.1), g.cartesianToImgY(this.posY - 0.015));
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setExecution(Runnable runnable) {
        this.execution = runnable;
    }

    public void execute() {
        this.execution.run();
    }

    public void testClickEvent(Point2D mousePos, MouseAction action) {
        if(this.boundingBox.containsPoint(mousePos)) {
            this.pressed = true;
        } else {
            this.pressed = false;
        }
    }

    public void setPosition(double x, double y) {
        this.posX = x;
        this.posY = y;
        this.boundingBox = this.computeHitboxes();
    }

    private BoundingBox computeHitboxes() {
        double width = 2 * texture.getWidth() / (double) canvas.getWidth();
        double height = (imgToCartesianY(0) - imgToCartesianY(texture.getHeight()));

        Point2D max = new Point2D(width / 2, height / 2);
        Point2D min = new Point2D(-max.x, -max.y);

        min.x += posX;
        max.x += posX;
        min.y += posY;
        max.y += posY;

        return new BoundingBox(min, max);
    }

    private double imgToCartesianY(double y) {
        return ((double)canvas.getHeight() / canvas.getWidth()) * -(2 * y / canvas.getHeight() - 1);
    }
}
