package me.alejandro.capstone.window.element;

import me.alejandro.capstone.model.PolygonalModel;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Vector3D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tachometer implements Drawable {

    private static BufferedImage texture;
    private PolygonalModel dialModel;
    private int rpm;

    public double posX, posY;

    static {
        try {
            texture = ImageIO.read(new File("res/tach.png"));
        } catch (IOException e) {
            System.out.println("Could not load tachometer texture! Is it missing in the JAR?");
            e.printStackTrace();
        }
    }

    public Tachometer() {

        //defining vertices counter-clockwise
        Vector3D[] vertices = {
                new Vector3D(-0.02, -0.02, 0),
                new Vector3D(0.02, -0.02, 0),
                new Vector3D(0.01, 0.7, 0),
                new Vector3D(-0.01, 0.7, 0),
        };

        int[] indices = {0, 1, 2, 2, 3, 0};

        //not used, yet
        Vector3D[] normals = {new Vector3D(0, 0, 1)};

        this.dialModel = new PolygonalModel(vertices, indices, normals);
        this.dialModel.setColor(Color.RED);
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {
        g.drawImage(texture, posX, posY);
        this.dialModel.draw(g, partialTick);
        g.setColor(Color.WHITE);
        g.getGraphics().drawString("RPM: " + this.rpm, 90, 330);
    }

    public PolygonalModel getModel() {
        return dialModel;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }
}
