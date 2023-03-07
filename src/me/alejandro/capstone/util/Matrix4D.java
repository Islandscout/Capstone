package me.alejandro.capstone.util;

import javax.xml.bind.SchemaOutputResolver;

public class Matrix4D implements Cloneable {

    double[][] entries;
    private double[][] transformation; //to be used as a local variable. Avoids making a ton of garbage each frame.
    boolean transposed;

    public Matrix4D() {
        this.entries = new double[4][4];
        this.transformation = new double[4][4];
    }

    public Matrix4D(Vector3D a, Vector3D b, Vector3D c) {
        this.entries = new double[4][4];
        this.transformation = new double[4][4];

        this.entries[0][0] = a.x;
        this.entries[1][0] = a.y;
        this.entries[2][0] = a.z;

        this.entries[0][1] = b.x;
        this.entries[1][1] = b.y;
        this.entries[2][1] = b.z;

        this.entries[0][2] = c.x;
        this.entries[1][2] = c.y;
        this.entries[2][2] = c.z;

    }

    public double getAt(int row, int column) {
        return transposed ? this.entries[column][row] : this.entries[row][column];
    }

    public void setAt(int row, int column, double value) {
        if (transposed) {
            this.entries[column][row] = value;
        } else {
            this.entries[row][column] = value;
        }
    }

    public int getRows() {
        return transposed ? entries[0].length : entries.length;
    }

    public int getColumns() {
        return transposed ? entries.length : entries[0].length;
    }

    public Matrix4D transpose() {
        this.transposed = !this.transposed;
        return this;
    }

    public Matrix4D add(Matrix4D addend) {
        return null;
    }

    public Matrix4D subtract(Matrix4D subtrahend) {
        return null;
    }

    public Matrix4D scale(double factor) {
        for(int r = 0; r < 4; r++) {
            for(int c = 0; c < 4; c++) {

                this.entries[r][c] *= factor;

            }
        }
        return this;
    }

    public Matrix4D translate(Vector3D displacement) {
        transformation[0][0] = 1;
        transformation[0][1] = 0;
        transformation[0][2] = 0;
        transformation[0][3] = displacement.x;
        transformation[1][0] = 0;
        transformation[1][1] = 1;
        transformation[1][2] = 0;
        transformation[1][3] = displacement.y;
        transformation[2][0] = 0;
        transformation[2][1] = 0;
        transformation[2][2] = 1;
        transformation[2][3] = displacement.z;
        transformation[3][0] = 0;
        transformation[3][1] = 0;
        transformation[3][2] = 0;
        transformation[3][3] = 1;

        return this.transform(transformation);
    }

    //a left-multiply by the parameter
    public Matrix4D transform(Matrix4D matrix) {
        return this.transform(matrix.entries);
    }

    private Matrix4D transform(double[][] other) {

        double[][] product = new double[4][4]; //i guess theres no easy way around doing new

        for(int r = 0; r < 4; r++) {
            for(int c = 0; c < 4; c++) {

                double sum = 0;
                for(int i = 0; i < 4; i++) {
                    sum += other[r][i] * this.entries[i][c];
                }

                product[r][c] = sum;

            }
        }

        this.entries = product;
        return this;
    }

    public Matrix4D augment(Matrix4D matrix) throws IllegalArgumentException {
        return null;
    }

    public Matrix4D getInverse() throws IllegalArgumentException {
        return null;
    }

    public double getDeterminant() throws IllegalArgumentException {
        return 0;
    }

    public Matrix4D getAdjugate() throws IllegalArgumentException {
        return null;
    }

    public Matrix4D getRREF() {
        return null;
    }

    public Matrix4D getNullSpace() {
        return null;
    }

    public Matrix4D getRowSpace() {
        return null;
    }

    public Matrix4D getLeftNullSpace() {
        return null;
    }

    public double[] getEigenvalues() throws IllegalArgumentException {
        return null;
    }

    public Matrix4D[] getEigenspaces() throws IllegalArgumentException {
        return null;
    }


    public static Matrix4D makeIdentity() {
        Matrix4D matrix = new Matrix4D();
        for (int i = 0; i < 4; i++) {
            matrix.entries[i][i] = 1;
        }

        return matrix;
    }

    //axis must be normal!
    //ang is in radians
    public Matrix4D rotate(Vector3D axis, double ang) {

        double ux = axis.x;
        double uy = axis.y;
        double uz = axis.z;
        double uxux = ux * ux;
        double uxuy = ux * uy;
        double uxuz = ux * uz;
        double uyuy = uy * uy;
        double uyuz = uy * uz;
        double uzuz = uz * uz;

        double cos = Math.cos(ang);
        double sin = Math.sin(ang);

        transformation[0][0] = cos + uxux * (1 - cos);
        transformation[0][1] = uxuy * (1 - cos) - uz * sin;
        transformation[0][2] = uxuz * (1 - cos) + uy * sin;
        transformation[0][3] = 0;
        transformation[1][0] = uxuy * (1 - cos) + uz * sin;
        transformation[1][1] = cos + uyuy * (1 - cos);
        transformation[1][2] = uyuz * (1 - cos) - ux * sin;
        transformation[1][3] = 0;
        transformation[2][0] = uxuz * (1 - cos) - uy * sin;
        transformation[2][1] = uyuz * (1 - cos) + ux * sin;
        transformation[2][2] = cos + uzuz * (1 - cos);
        transformation[2][3] = 0;
        transformation[3][0] = 0;
        transformation[3][1] = 0;
        transformation[3][2] = 0;
        transformation[3][3] = 0;

        return this.transform(transformation);

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int rows = this.getRows();
        int columns = this.getColumns();

        for (int i = 0; i < rows; i++) {
            sb.append("[");
            for (int j = 0; j < columns; j++) {
                sb.append(getAt(i, j));
                if (j + 1 < columns) {
                    sb.append(" ");
                }
            }
            sb.append("]\n");
        }

        return sb.toString();
    }
}
