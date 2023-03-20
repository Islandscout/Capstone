package me.alejandro.capstone.window.element;

import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Button implements Drawable {

    //TODO min max bounds
    private boolean pressed;

    private BufferedImage texture;
    private BufferedImage texturePressed;
    public double posX, posY;
    private String text;
    private Color textColor;

    public Button(String text) {

        this.text = text;
        this.textColor = Color.BLACK;

        try {
            this.texture = ImageIO.read(new File("res/button.png"));
            this.texturePressed = ImageIO.read(new File("res/button_pressed.png"));
        } catch (IOException e) {
            System.out.println("Could not load button textures! Is it missing in the JAR?");
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {
        g.drawImage(this.texture, this.posX, this.posY);
        g.setColor(this.textColor);
        g.getGraphics().drawString(text, g.cartesianToImgX(this.posX - 0.1), g.cartesianToImgY(this.posY - 0.015));
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
}
