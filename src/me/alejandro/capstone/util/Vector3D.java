package me.alejandro.capstone.util;

public class Vector3D {

    public double x, y, z;

    public Vector3D() {
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double magnitudeSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public Vector3D normalize() {
        double magnitude = this.magnitude();
        this.x /= magnitude;
        this.y /= magnitude;
        this.z /= magnitude;
        return this;
    }

    public double dot(Vector3D o) {
        return this.x * o.x + this.y * o.y + this.z * o.z;
    }
}
