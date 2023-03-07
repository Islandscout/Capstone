package me.alejandro.capstone.window.element;

import me.alejandro.capstone.model.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tachometer implements Drawable {

    private BufferedImage texture;

    public double posX, posY;

    public Tachometer() {

        try {
            this.texture = ImageIO.read(new File("res/tach.png"));
        } catch (IOException e) {
            System.out.println("Could not load tachometer texture! Is it missing in the JAR?");
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GraphicsWrapper g) {
        g.drawImage(this.texture, posX, posY);
    }
}
