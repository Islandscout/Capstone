package me.alejandro.capstone.window;

import arduino.Arduino;
import me.alejandro.capstone.Main;
import me.alejandro.capstone.input.Input;
import me.alejandro.capstone.render.GraphicsWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.VolatileImage;

public abstract class Window {

    private Frame frame;
    private Canvas canvas;

    private Color background;

    public final int width, height;
    private final double aspect;
    private final int fpsCap = 60;
    private final int tps = 20;
    private final long minDrawTime;

    private boolean closeRequested;

    private String name;

    public Window(String name, int width, int height) {
        this.name = name;

        this.width = width;
        this.height = height;
        this.aspect = (float) height / width;

        this.background = Color.BLACK; //default

        this.frame = new Frame();
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(new Dimension(width, height));

        this.frame.add(canvas);

        this.frame.pack();
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeRequested = true;
            }
        });
        this.frame.setVisible(true);
        this.canvas.addKeyListener(new Input());

        minDrawTime = fpsCap > 0 ? (long) (1000000000D / fpsCap) : 0;

    }

    public void startLoop() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                GraphicsWrapper g = new GraphicsWrapper(canvas);

                long start, lastTickStartTime;
                start = lastTickStartTime = System.nanoTime();

                long tickIntervalTime = 1000000000 / tps;

                while(!closeRequested) {

                    g.validate();

                    //TODO poll inputs

                    //ticking
                    long sinceLastTick = Math.min(System.nanoTime() - lastTickStartTime, 1000000000); //cap max catchup time to 1 second
                    if(sinceLastTick > tickIntervalTime) {

                        int ticks = (int)(sinceLastTick / tickIntervalTime);
                        int remainderTime = (int)(sinceLastTick % tickIntervalTime);

                        for(int i = 0; i < ticks; i++) {

                            if(i + 1 == ticks) {
                                lastTickStartTime = System.nanoTime() - remainderTime;
                            }

                            tick();
                        }

                        sinceLastTick = remainderTime;
                    }

                    double partialTick = (double)sinceLastTick / tickIntervalTime;

                    drawBackground(g);
                    render(g, partialTick);

                    g.flush();

                    //Frame rate regulation
                    long drawTime = System.nanoTime() - start;
                    if(drawTime < minDrawTime) {
                        long sleep = minDrawTime - (drawTime);
                        try {
                            Thread.sleep(sleep / 1000000, (int) (sleep % 1000000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    start = System.nanoTime();
                    Toolkit.getDefaultToolkit().sync(); //required for linux. the GPU screws itself over causing jitters.
                }

                //cleanup
                frame.dispose();
                onClose();
            }
        });
        thread.setName("Render thread (" + name + ")");
        thread.start();
    }

    protected abstract void render(GraphicsWrapper g, double partialTick);

    protected abstract void tick();

    public void setBackground(Color background) {
        this.background = background;
    }

    private void drawBackground(GraphicsWrapper g) {
        g.setColor(background);
        g.fillScreen();
    }

    protected abstract void onClose();

    protected double imgToCartesianX(double x) {
        return 2 * x / width - 1;
    }

    protected double imgToCartesianY(double y) {
        return aspect * -(2 * y / height - 1);
    }

    protected int cartesianToImgX(double x) {
        return (int) (width * (x + 1)) / 2;
    }

    protected int cartesianToImgY(double y) {
        return (int) (-height * (y - aspect) / (2 * aspect));
    }
}
