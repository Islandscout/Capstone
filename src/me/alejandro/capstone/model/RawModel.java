package me.alejandro.capstone.model;

import me.alejandro.capstone.util.Matrix4D;
import me.alejandro.capstone.util.Vector3D;

import java.awt.*;

/**
 * Represents a 3D model
 */
public class RawModel {

    private Vector3D[] vertices;
    private int[] indices;
    private Vector3D[] normals;

    private Color color;
    private boolean shaded;

    private Matrix4D transformation;

    public RawModel(Vector3D[] vertices, int[] indices, Vector3D[] normals) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;

        this.color = Color.magenta;

    }

    public void draw(Graphics g) {
        //TODO copy geometry and apply transformation

        for(int i = 0; i < indices.length; i += 3) {
            Vector3D a = vertices[indices[i]];
            Vector3D b = vertices[indices[i + 1]];
            Vector3D c = vertices[indices[i + 2]];



        }

        //TODO sort triangles (hidden surface determination)

        //TODO loop through each triangle, calculate lighting, set color
        // call Graphics#fillPolygon(xPoints, yPoints, 3);
    }
}
