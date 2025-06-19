package crowd_simulation;

import processing.core.PApplet;

class Agent {
    private double x;
    private double y;
    private final float collisionSize = 25f;
    private int speed;
    private double direction;

    private final PApplet canvas;

    public Agent(PApplet canvas, double x, double y, int speed, double direction) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.canvas = canvas;
        this.direction = direction;
    }

    public void draw() {
        canvas.circle((float) x, (float) y, collisionSize);
    }

    public void move() {
        x += speed * Math.cos(direction);
        y += speed * Math.sin(direction);
    }
}