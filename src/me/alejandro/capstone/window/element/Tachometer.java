package me.alejandro.capstone.window.element;

import me.alejandro.capstone.model.PolygonalModel;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Vector3D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tachometer implements Drawable {

    private BufferedImage texture;
    private PolygonalModel dialModel;

    public double posX, posY;

    public Tachometer() {

        try {
            this.texture = ImageIO.read(new File("res/tach.png"));
        } catch (IOException e) {
            System.out.println("Could not load tachometer texture! Is it missing in the JAR?");
            e.printStackTrace();
        }

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
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {
        g.drawImage(this.texture, posX, posY);
        this.dialModel.draw(g, partialTick);
    }

    public PolygonalModel getModel() {
        return dialModel;
    }
}
