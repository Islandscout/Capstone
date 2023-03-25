package me.alejandro.capstone.window.element;

import me.alejandro.capstone.model.PolygonalModel;
import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.BoundingBox;
import me.alejandro.capstone.util.Point2D;

import java.awt.*;

public class Meter implements Drawable {

    private BoundingBox bounds;
    private Color background, foreground;
    private double value;
    private String name;

    public Meter(double lengthRatio, String name) {
        this.bounds = new BoundingBox(new Point2D(-lengthRatio/2, -0.5), new Point2D(lengthRatio/2, 0.5));
        this.background = new Color(0, 0, 0);
        this.foreground = Color.CYAN;
        this.name = name;
    }

    @Override
    public void draw(GraphicsWrapper g, double partialTick) {

        //draw bg
        g.setColor(this.background);
        g.fillRect(this.bounds.getMin().x, this.bounds.getMin().y, this.bounds.getMax().x, this.bounds.getMax().y);

        //draw foreground
        g.setColor(this.foreground);
        double xMax = this.bounds.getMin().x + (this.bounds.getMax().x - this.bounds.getMin().x) * this.value;
        g.fillRect(this.bounds.getMin().x, this.bounds.getMin().y, xMax, this.bounds.getMax().y);

        //draw title
        g.getGraphics().drawString(this.name, g.cartesianToImgX(this.bounds.getMin().x), g.cartesianToImgY(this.bounds.getMax().y) - 4);

    }

    public void transform(double scale, double dx, double dy) {
        this.bounds.scale(scale);
        this.bounds.translate(dx, dy);
    }

    public void setValue(double value) {
        this.value = Math.max(Math.min(value, 1), 0);
    }
}
