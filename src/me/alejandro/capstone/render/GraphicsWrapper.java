package me.alejandro.capstone.render;

import java.awt.*;
import java.awt.image.VolatileImage;

public class GraphicsWrapper {

    private Graphics graphics;
    private final Canvas canvas;
    private final GraphicsConfiguration gc;
    private VolatileImage vImage;
    private double aspect;
    private int width, height;

    public GraphicsWrapper(Canvas canvas) {
        this.canvas = canvas;
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        this.gc = canvas.getGraphicsConfiguration();
        this.vImage = gc.createCompatibleVolatileImage(this.width, this.height);
        this.graphics = this.vImage.getGraphics();
        this.aspect = (double) this.height / this.width;
    }

    public void setColor(Color color) {
        this.graphics.setColor(color);
    }

    public void fillScreen() {

        this.graphics.fillRect(0, 0, this.width, this.height);
    }

    public void flush() {
        Graphics old = this.graphics;
        this.graphics = canvas.getGraphics();
        this.graphics.drawImage(this.vImage, 0, 0, width, height, null);
        this.graphics.dispose();
        this.graphics = old;
    }

    public void validate() {
        if(vImage.validate(this.gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
            vImage = this.gc.createCompatibleVolatileImage(width, height);
            System.out.println("this should not happen");
        }
    }

    public void fillTriangle(double[] xPoints, double[] yPoints) {
        int[] xPointsImg = new int[] {
                cartesianToImgX(xPoints[0]),
                cartesianToImgX(xPoints[1]),
                cartesianToImgX(xPoints[2])
        };
        int[] yPointsImg = new int[] {
                cartesianToImgY(yPoints[0]),
                cartesianToImgY(yPoints[1]),
                cartesianToImgY(yPoints[2])
        };

        this.graphics.fillPolygon(xPointsImg, yPointsImg, 3);
    }

    public Graphics getGraphics() {
        return graphics;
    }

    private double imgToCartesianX(double x) {
        return 2 * x / width - 1;
    }

    private double imgToCartesianY(double y) {
        return aspect * -(2 * y / height - 1);
    }

    private int cartesianToImgX(double x) {
        return (int) (width * (x + 1)) / 2;
    }

    private int cartesianToImgY(double y) {
        return (int) (-height * (y - aspect) / (2 * aspect));
    }
}
