package me.alejandro.capstone.window.element;

import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.MathPlus;
import me.alejandro.capstone.util.Matrix4D;
import me.alejandro.capstone.util.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Plot implements Drawable {

    private final int HORIZONTAL_LINES = 6;
    private final int VERTICAL_LINES = 10;
    private final double[] horizLinePos = new double[HORIZONTAL_LINES];
    private final double[] vertLinePos = new double[VERTICAL_LINES];

    private BufferedImage borderTex;
    private BufferedImage bg;

    private List<Point> data; //assume these are non-negative

    private Color gridColor = new Color(25, 81, 87);

    public double posX, posY;

    private Matrix4D plotTransformation; //used to draw lines and points

    public Plot() {

        for(int i = 0; i < HORIZONTAL_LINES; i++) {
            horizLinePos[i] = (i + 1) / (double) HORIZONTAL_LINES;
        }
        for(int i = 0; i < VERTICAL_LINES; i++) {
            vertLinePos[i] = (i + 1) / (double) VERTICAL_LINES;
        }

        this.data = new ArrayList<>();

        //debug
        /*for(int i = 0; i < 60; i++) {
            Point p = new Point(Math.cos(i) + 1.1, Math.sin(i) + 1.1);
            this.data.add(p);
        }*/

        try {
            this.borderTex = ImageIO.read(new File("res/plot_border.png"));
            this.bg = ImageIO.read(new File("res/plot_bg.png"));
        } catch (IOException e) {
            System.out.println("Could not load plot textures! Are they missing in the JAR?");
            e.printStackTrace();
        }

    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        //compute plot bounds
        double boundX = 1.1 * MathPlus.getMax(this.data, MathPlus.Axis.X);
        double boundY = 1.1 * MathPlus.getMax(this.data, MathPlus.Axis.Y);

        g.drawImage(this.bg, posX, posY); //draw background
        this.drawLines(g, boundX, boundY);
        this.drawData(g, boundX, boundY);
        g.drawImage(this.borderTex, posX, posY); //draw border

        g.setColor(Color.WHITE);
        g.getGraphics().drawString("Torque (ft-lbs)", 210, 60);
        g.getGraphics().drawString("Power (hp)", 620, 60);
        g.getGraphics().drawString("RPM", 430, 310);

    }

    private void drawLines(GraphicsWrapper g, double boundX, double boundY) {

        g.setColor(this.gridColor);

        double plotWidth = this.bg.getWidth() / (double) g.getWidth();
        double plotHeight = (g.imgToCartesianY(0) - g.imgToCartesianY(this.bg.getHeight())) / 2;

        //draw horizontal lines
        {
            double xMin = (-plotWidth) + this.posX;
            double xMax = (plotWidth) + this.posX;

            for (int i = 0; i < horizLinePos.length; i++) {
                double pos = horizLinePos[i];

                double value = boundY * (i + 1) / HORIZONTAL_LINES;
                int tickValue = (int) value;

                double y = (tickValue / value) * pos - 0.5;
                y *= 2 * plotHeight;
                y += this.posY;

                g.drawLine(xMin, y, xMax, y);

                g.getGraphics().drawString("" + tickValue, g.cartesianToImgX(xMin) - 40, g.cartesianToImgY(y));
            }
        }

        //draw vertical lines
        {
            double yMin = (-plotHeight) + this.posY;
            double yMax = (plotHeight) + this.posY;

            for (int i = 0; i < vertLinePos.length; i++) {
                double pos = vertLinePos[i];

                double value = boundX * (i + 1) / VERTICAL_LINES;
                int tickValue = (int) value;

                double x = (tickValue / value) * pos - 0.5;
                x *= 2 * plotWidth;
                x += this.posX;

                g.drawLine(x, yMin, x, yMax);

                g.getGraphics().drawString("" + tickValue, g.cartesianToImgX(x), g.cartesianToImgY(yMin) + 11);
            }
        }
    }

    public void drawData(GraphicsWrapper g, double boundX, double boundY) {
        g.setColor(Color.cyan);
        for(int i = 0; i < this.data.size(); i ++) {
            double x = this.data.get(i).x / boundX;
            double y = this.data.get(i).y / boundY;

            double scaleXTransform = getScaleXTransform(g);
            double scaleYTransform = getScaleYTransform(g);

            x *= scaleXTransform;
            y *= scaleYTransform;

            x += this.posX - scaleXTransform / 2;
            y += this.posY - scaleYTransform / 2;

            g.drawPoint(x, y);
        }
    }

    //Used for mapping plot space onto screen space
    private double getScaleXTransform(GraphicsWrapper g) {
        return 2 * this.bg.getWidth() / (double) g.getWidth();
    }

    //Used for mapping plot space onto screen space
    private double getScaleYTransform(GraphicsWrapper g) {
        return (g.imgToCartesianY(0) - g.imgToCartesianY(this.bg.getHeight()));
    }

    public void addPoint(Point point) {
        this.data.add(point);
    }
}
