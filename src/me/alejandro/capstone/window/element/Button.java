package me.alejandro.capstone.window.element;

import me.alejandro.capstone.input.MouseAction;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.BoundingBox;
import me.alejandro.capstone.util.Point2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Button implements Drawable {

    public ButtonMode mode = ButtonMode.CLICK;
    public ButtonState state = ButtonState.UP;
    public boolean engaged;
    private Runnable execution;

    protected static BufferedImage textureShared;
    protected static BufferedImage texturePressedShared;
    protected static BufferedImage textureLatchedShared;

    protected static BufferedImage textureSmall_Shared;
    protected static BufferedImage textureSmall_PressedShared;
    protected static BufferedImage textureSmall_LatchedShared;

    protected static BufferedImage textureUp_Shared;
    protected static BufferedImage textureUp_PressedShared;
    protected static BufferedImage textureUp_LatchedShared;

    protected static BufferedImage textureDown_Shared;
    protected static BufferedImage textureDown_PressedShared;
    protected static BufferedImage textureDown_LatchedShared;


    protected BufferedImage texture, texturePressed, textureLatched;

    private double posX, posY;
    private String text;
    protected double textOffsetX;
    private Color textColor;
    private BoundingBox boundingBox;
    private Canvas canvas;

    static {
        try {
            textureShared = ImageIO.read(new File("res/button.png"));
            texturePressedShared = ImageIO.read(new File("res/button_pressed.png"));
            textureLatchedShared = ImageIO.read(new File("res/button_latched.png"));

            textureSmall_Shared = ImageIO.read(new File("res/button1.png"));
            textureSmall_PressedShared = ImageIO.read(new File("res/button1_pressed.png"));
            textureSmall_LatchedShared = ImageIO.read(new File("res/button1_latched.png"));

            textureSmall_Shared = ImageIO.read(new File("res/button1.png"));
            textureSmall_PressedShared = ImageIO.read(new File("res/button1_pressed.png"));
            textureSmall_LatchedShared = ImageIO.read(new File("res/button1_latched.png"));

            textureUp_Shared = ImageIO.read(new File("res/buttonUp.png"));
            textureUp_PressedShared = textureUp_LatchedShared = ImageIO.read(new File("res/buttonUp_pressed.png"));

            textureDown_Shared = ImageIO.read(new File("res/buttonDown.png"));
            textureDown_PressedShared = textureDown_LatchedShared = ImageIO.read(new File("res/buttonDown_pressed.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Button(String text, Canvas canvas) {

        this.text = text;
        this.textColor = Color.BLACK;
        this.canvas = canvas;

        //this.setPosition(0, 0);
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {
        if(this.state == ButtonState.GRABBED) {
            g.drawImage(this.texturePressed, this.posX, this.posY);
        } else if (this.state == ButtonState.LATCHED) {
            g.drawImage(this.textureLatched, this.posX, this.posY);
        } else {
            g.drawImage(this.texture, this.posX, this.posY);
        }
        g.setColor(this.textColor);
        g.getGraphics().drawString(text, g.cartesianToImgX(this.posX + textOffsetX), g.cartesianToImgY(this.posY - 0.015));
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setExecution(Runnable runnable) {
        this.execution = runnable;
    }

    public void execute() {
        if(this.execution != null) {
            this.execution.run();
        }
    }

    public void fireShortcut() {
        Point2D firePos = new Point2D(posX, posY);
        testClickEvent(firePos, MouseAction.BUTTON_HOLD);
        testClickEvent(firePos, MouseAction.BUTTON_RELEASE);
    }

    public void testClickEvent(Point2D mousePos, MouseAction action) {

        boolean inside = this.boundingBox.containsPoint(mousePos);

        if(this.mode == ButtonMode.CLICK) {

            if(this.state == ButtonState.GRABBED) {
                if(action == MouseAction.BUTTON_RELEASE) {

                    if(inside) {
                        try {
                            this.execute();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(canvas, "An internal exception occurred while attempting to execute your action.");
                            e.printStackTrace();
                        }
                    }

                    this.state = ButtonState.UP;
                }
            } else if(this.state == ButtonState.UP && inside) {
                if(action == MouseAction.BUTTON_HOLD) {
                    this.state = ButtonState.GRABBED;
                }
            }


        } else if(this.mode == ButtonMode.TOGGLE) {

            if(this.state == ButtonState.GRABBED) {
                if(action == MouseAction.BUTTON_RELEASE) {

                    if(inside) {
                        if(this.engaged) {
                            engaged = false;
                            this.state = ButtonState.UP;
                        } else {
                            engaged = true;
                            this.state = ButtonState.LATCHED;
                        }

                    } else {
                        this.state = ButtonState.UP;
                    }


                }
            } else if(this.state == ButtonState.LATCHED) {
                if(action == MouseAction.BUTTON_HOLD) {

                    if(inside) {
                        this.state = ButtonState.GRABBED;
                    }
                }
            } else if(inside && action == MouseAction.BUTTON_HOLD) {
                this.state = ButtonState.GRABBED;
            }

        }
    }

    public void setPosition(double x, double y) {
        this.posX = x;
        this.posY = y;
        this.boundingBox = this.computeHitboxes();
    }

    private BoundingBox computeHitboxes() {
        double width = 2 * this.texture.getWidth() / (double) canvas.getWidth();
        double height = (imgToCartesianY(0) - imgToCartesianY(this.texture.getHeight()));

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

    public enum ButtonMode {
        CLICK,
        TOGGLE
    }

    public enum ButtonState {
        UP,
        GRABBED,
        LATCHED
    }
}
