package me.alejandro.capstone.window;

import javax.swing.*;
import java.awt.*;

public class WindowDashboard extends Window {

    public WindowDashboard() {
        super("dashboard", 600, 400);
        this.setBackground(Color.BLACK);
    }

    int frame;

    @Override
    public void render(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        g.setColor(Color.WHITE);

        int[] xPoints = {
                cartesianToImgX(-0.2),
                cartesianToImgX(0.0),
                cartesianToImgX(0.0)};

        int[] yPoints = {
                cartesianToImgY(0),
                cartesianToImgY(0),
                cartesianToImgY(0.2)};

        g2D.fillPolygon(xPoints, yPoints, 3);
    }

}
