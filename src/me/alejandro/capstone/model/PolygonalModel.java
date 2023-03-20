package me.alejandro.capstone.model;

import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Matrix4D;
import me.alejandro.capstone.util.Vector3D;

import java.awt.*;

/**
 * Represents a 3D model
 */
public class PolygonalModel implements Drawable {

    private final Vector3D[] vertices;
    private final int[] indices;
    private final Vector3D[] normals;

    private Color color;
    private boolean shaded;

    private Matrix4D transformation;

    public PolygonalModel(Vector3D[] vertices, int[] indices, Vector3D[] normals) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.transformation = Matrix4D.makeIdentity();

        this.color = Color.magenta;

    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        g.setColor(this.color);

        //Loop through every triangle
        for(int i = 0; i < indices.length; i += 3) {

            //copy
            Vector3D a = vertices[indices[i]];
            Vector3D b = vertices[indices[i + 1]];
            Vector3D c = vertices[indices[i + 2]];

            //transform
            Matrix4D triangle = new Matrix4D(a, b, c);
            triangle.transform(transformation);

            double[] xPoints = {
                    triangle.getAt(0, 0),
                    triangle.getAt(0, 1),
                    triangle.getAt(0, 2)
            };

            double[] yPoints = {
                    triangle.getAt(1, 0),
                    triangle.getAt(1, 1),
                    triangle.getAt(1, 2)
            };

            g.fillTriangle(xPoints, yPoints);

        }
    }

    public Matrix4D getTransformation() {
        return transformation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
