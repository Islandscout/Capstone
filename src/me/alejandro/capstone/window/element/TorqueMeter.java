package me.alejandro.capstone.window.element;

import me.alejandro.capstone.model.PolygonalModel;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.BoundingBox;
import me.alejandro.capstone.util.Point2D;

import java.awt.*;

public class TorqueMeter implements Drawable {

    private BoundingBox bounds;
    private Color background, foreground;
    private double value;
    private static final double MAX_TORQUE = 35;

    public TorqueMeter(double lengthRatio) {
        this.bounds = new BoundingBox(new Point2D(-lengthRatio/2, -0.5), new Point2D(lengthRatio/2, 0.5));
        this.background = new Color(0, 0, 0);
        this.foreground = Color.CYAN;
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        //draw bg
        g.setColor(this.background);
        g.fillRect(this.bounds.getMin().x, this.bounds.getMin().y, this.bounds.getMax().x, this.bounds.getMax().y);

        //draw foreground
        g.setColor(this.foreground);
        double relativeValue = Math.min(Math.max(0, value / MAX_TORQUE), 1);
        double xMax = this.bounds.getMin().x + (this.bounds.getMax().x - this.bounds.getMin().x) * relativeValue;
        g.fillRect(this.bounds.getMin().x, this.bounds.getMin().y, xMax, this.bounds.getMax().y);

        double displayValue = Math.max(0, (int)(value * 10) / 10D);

        //draw title
        g.getGraphics().drawString("Torque: " + displayValue + " ft-lbs", g.cartesianToImgX(this.bounds.getMin().x), g.cartesianToImgY(this.bounds.getMax().y) - 4);

    }

    public void transform(double scale, double dx, double dy) {
        this.bounds.scale(scale);
        this.bounds.translate(dx, dy);
    }

    public void setValue(double value) {
        this.value = value;
    }
}
