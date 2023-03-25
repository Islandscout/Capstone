package me.alejandro.capstone.util;

public class BoundingBox {

    private final Point2D min, max;

    public BoundingBox(Point2D min, Point2D max) {
        this.min = min;
        this.max = max;
    }

    public boolean containsPoint(Point2D p) {
        return p.x < max.x && p.x > min.x &&
                p.y < max.y && p.y > min.y;
    }

    public Point2D getMin() {
        return min;
    }

    public Point2D getMax() {
        return max;
    }

    public void translate(double x, double y) {
        this.min.add(x, y);
        this.max.add(x, y);
    }

    public void scale(double factor) {
        this.min.multiply(factor);
        this.max.multiply(factor);
    }
}
