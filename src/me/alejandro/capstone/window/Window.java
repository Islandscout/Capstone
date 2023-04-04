package me.alejandro.capstone.window;

import me.alejandro.capstone.render.Drawable;
import me.alejandro.capstone.render.GraphicsWrapper;
import me.alejandro.capstone.util.Point2D;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public abstract class Window implements Drawable, KeyListener, MouseListener {

    private Frame frame;
    protected Canvas canvas;

    private Color fallbackBg;
    private BufferedImage bg;

    public final int width, height;
    private final double aspect;
    private final int fpsCap = 60;
    private final int tps = 20;
    private final long minDrawTime;

    private boolean closeRequested;
    protected boolean mouseDown, wasMouseDown;

    private String name;

    private static boolean[] currentKeys = new boolean[256];
    private Point2D mousePos;

    public Window(String name, int width, int height) {
        this.name = name;

        this.width = width;
        this.height = height;
        this.aspect = (float) height / width;

        this.fallbackBg = Color.BLACK; //default

        this.mousePos = new Point2D(0, 0);

        this.frame = new Frame();
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(new Dimension(width, height));

        this.frame.add(canvas);

        this.frame.pack();
        this.frame.setName(name);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeRequested = true;
            }
        });
        this.frame.setVisible(true);
        this.canvas.addKeyListener(this);
        this.canvas.addMouseListener(this);

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

                    //usually in video games we poll inputs here

                    //ticking
                    long sinceLastTick = Math.min(System.nanoTime() - lastTickStartTime, 1000000000); //cap max catchup time to 1 second
                    if(sinceLastTick > tickIntervalTime) {

                        int ticks = (int)(sinceLastTick / tickIntervalTime);
                        int remainderTime = (int)(sinceLastTick % tickIntervalTime);

                        for(int i = 0; i < ticks; i++) {

                            if(i + 1 == ticks) {
                                lastTickStartTime = System.nanoTime() - remainderTime;
                            }

                            Point mouseScreenPos = MouseInfo.getPointerInfo().getLocation();
                            mousePos.x = g.imgToCartesianX(mouseScreenPos.x - canvas.getLocationOnScreen().x);
                            mousePos.y = g.imgToCartesianY(mouseScreenPos.y - canvas.getLocationOnScreen().y);

                            tick();

                            wasMouseDown = mouseDown;
                        }

                        sinceLastTick = remainderTime;
                    }

                    double partialTick = (double)sinceLastTick / tickIntervalTime;

                    drawBackground(g);
                    draw(g, partialTick);

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

    protected abstract void tick();

    protected void setTitle(String title) {
        this.frame.setTitle(title);
    }

    public void setFallbackBg(Color fallbackBg) {
        this.fallbackBg = fallbackBg;
    }

    public void setBackground(BufferedImage img) {
        this.bg = img;
    }

    private void drawBackground(GraphicsWrapper g) {
        if(this.bg == null) { //fallback to solid color
            g.setColor(fallbackBg);
            g.fillScreen();
        }
        else {
           g.drawImage(bg);
        }
    }

    protected abstract void onClose();

    public static boolean getKey(int keyCode) {
        return currentKeys[keyCode];
    }

    public Point2D getMousePos() {
        return this.mousePos;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        currentKeys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKeys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
