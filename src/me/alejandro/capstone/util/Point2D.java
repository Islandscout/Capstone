package me.alejandro.capstone.util;

/**
 * 2D representation of a coordinate pair
 */
public class Point2D {

    public double x, y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Point2D multiply(double factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    public Point2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }
}
