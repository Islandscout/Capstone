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

    public ButtonMode mode = ButtonMode.CLICK;
    public ButtonState state = ButtonState.UP;
    public boolean engaged;
    private Runnable execution;

    private BufferedImage texture;
    private BufferedImage texturePressed;
    private BufferedImage textureLatched;
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
            this.textureLatched = ImageIO.read(new File("res/button_pressed1.png"));
        } catch (IOException e) {
            System.out.println("Could not load button textures! Is it missing in the JAR?");
            e.printStackTrace();
        }

        this.setPosition(0, 0);
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
        g.getGraphics().drawString(text, g.cartesianToImgX(this.posX - 0.1), g.cartesianToImgY(this.posY - 0.015));
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
                        this.execute();
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
