package me.alejandro.capstone.window.element;

import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.MathPlus;
import me.alejandro.capstone.util.Matrix4D;
import me.alejandro.capstone.util.Vector3D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Plot implements Drawable {

    private final int HORIZONTAL_LINES = 6;
    private final int VERTICAL_LINES = 10;
    private final double[] horizLinePos = new double[HORIZONTAL_LINES];
    private final double[] vertLinePos = new double[VERTICAL_LINES];

    public boolean hold;

    private static BufferedImage borderTex;
    private static BufferedImage bg;

    private List<Vector3D> data; //assume these are non-negative. For future, a Tuple might be more appropriate


    private Color torqueGridColor = new Color(25, 81, 87);
    private Color powerGridColor = new Color(143, 31, 31);


    public double posX, posY;

    private Matrix4D plotTransformation; //used to draw lines and points

    static {
        try {
            borderTex = ImageIO.read(new File("res/plot_border.png"));
            bg = ImageIO.read(new File("res/plot_bg.png"));
        } catch (IOException e) {
            System.out.println("Could not load plot textures! Are they missing in the JAR?");
            e.printStackTrace();
        }
    }

    public Plot() {

        for(int i = 0; i < HORIZONTAL_LINES; i++) {
            horizLinePos[i] = (i + 1) / (double) HORIZONTAL_LINES;
        }
        for(int i = 0; i < VERTICAL_LINES; i++) {
            vertLinePos[i] = (i + 1) / (double) VERTICAL_LINES;
        }

        this.data = new CopyOnWriteArrayList<>();

    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        //compute plot bounds
        double boundX = 1.1 * MathPlus.getMax(this.data, MathPlus.Axis.X);
        double boundTorqueY = 1.1 * MathPlus.getMax(this.data, MathPlus.Axis.Y);
        double boundPowerY = 1.1 * MathPlus.getMax(this.data, MathPlus.Axis.Z);
        double boundY = Math.max(boundTorqueY, boundPowerY);

        g.drawImage(bg, posX, posY); //draw background
        this.drawLines(g, boundX, boundY);
        this.drawData(g, boundX, boundY);
        g.drawImage(borderTex, posX, posY); //draw border

        g.setColor(Color.WHITE);
        g.getGraphics().drawString("Torque (ft-lbs)", 210, 50);
        g.getGraphics().drawString("Power (hp)", 620, 50);
        g.getGraphics().drawString("RPM", 430, 310);

    }

    private void drawLines(GraphicsWrapper g, double boundX, double boundY) {

        g.setColor(this.torqueGridColor);

        double plotWidth = bg.getWidth() / (double) g.getWidth();
        double plotHeight = (g.imgToCartesianY(0) - g.imgToCartesianY(bg.getHeight())) / 2;

        //draw horizontal lines
        {
            double xMin = (-plotWidth) + this.posX;
            double xMax = (plotWidth) + this.posX;

            //torque minor grid lines
            for (int i = 0; i < horizLinePos.length; i++) {
                double pos = horizLinePos[i];

                double value = boundY * (i + 1) / HORIZONTAL_LINES;
                int tickValue = (int) value;

                double y = (tickValue / value) * pos - 0.5;
                y *= 2 * plotHeight;
                y += this.posY;

                g.drawLine(xMin, y, xMax, y);

                g.getGraphics().drawString("" + tickValue, g.cartesianToImgX(xMin) - 27, g.cartesianToImgY(y));
            }

            //power minor grid lines
            for (int i = 0; i < horizLinePos.length; i++) {
                double pos = horizLinePos[i];

                double value = boundY * (i + 1) / HORIZONTAL_LINES;
                int tickValue = (int) value;

                double y = (tickValue / value) * pos - 0.5;
                y *= 2 * plotHeight;
                y += this.posY;

                g.setColor(this.powerGridColor);
                g.drawLine(xMin, y, xMax, y);

                g.getGraphics().drawString("" + tickValue, g.cartesianToImgX(xMin) + 355, g.cartesianToImgY(y));
            }
        }

        g.setColor(this.torqueGridColor);

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

        //draw torque

        for(int i = 0; i < this.data.size(); i ++) {
            double x = this.data.get(i).x / boundX;
            double torqueY = this.data.get(i).y / boundY;
            double powerY = this.data.get(i).z / boundY;

            double scaleXTransform = getScaleXTransform(g);
            double scaleYTransform = getScaleYTransform(g);

            x *= scaleXTransform;
            torqueY *= scaleYTransform;
            powerY *= scaleYTransform;

            x += this.posX - scaleXTransform / 2;
            torqueY += this.posY - scaleYTransform / 2;
            powerY += this.posY - scaleYTransform / 2;

            g.setColor(Color.CYAN);
            g.drawPoint(x, torqueY);
            g.setColor(Color.RED);
            g.drawPoint(x, powerY);
        }

    }

    //Used for mapping plot space onto screen space
    private double getScaleXTransform(GraphicsWrapper g) {
        return 2 * bg.getWidth() / (double) g.getWidth();
    }

    //Used for mapping plot space onto screen space
    private double getScaleYTransform(GraphicsWrapper g) {
        return (g.imgToCartesianY(0) - g.imgToCartesianY(bg.getHeight()));
    }

    public void addPoint(double x, double y1, double y2) {
        if(!this.hold) {
            this.data.add(new Vector3D(x, y1, y2));
        }
    }

    public List<Vector3D> getData() {
        return data;
    }
}
