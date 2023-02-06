package me.alejandro.capstone.window;

import me.alejandro.capstone.render.GraphicsWrapper;

import javax.swing.*;
import java.awt.*;

public class WindowDashboard extends Window {

    public WindowDashboard() {
        super("dashboard", 600, 400);
        this.setBackground(Color.BLACK);
    }

    int frame;

    @Override
    public void render(GraphicsWrapper g) {

        g.setColor(Color.WHITE);

        double[] xPoints = {
                -0.2,
                0.0,
                0.0
        };

        double[] yPoints = {
                0.0,
                Math.sin(0.01 * frame++),
                0.2
        };

        g.fillTriangle(xPoints, yPoints);
    }

}
