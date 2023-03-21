package me.alejandro.capstone.util;

import java.util.List;

public class MathPlus {

    public enum Axis {
        X,
        Y,
        Z
    }

    public static double getMax(List<Point2D> data, Axis axis) {
        if(data.size() == 0) {
            return 0;
        }

        double result = Integer.MIN_VALUE;

        for(Point2D point : data) {
            if(axis == Axis.X && point.x > result) {
                result = point.x;
            } else if(axis == Axis.Y && point.y > result) {
                result = point.y;
            }
        }

        return result;
    }

    public static double getMin(List<Point2D> data, Axis axis) {
        if(data.size() == 0) {
            return 0;
        }

        double result = Integer.MAX_VALUE;

        for(Point2D point : data) {
            if(axis == Axis.X && point.x < result) {
                result = point.x;
            } else if(axis == Axis.Y && point.y < result) {
                result = point.y;
            }
        }

        return result;
    }


}
