package crowd_simulation;

import processing.core.PApplet;
import processing.core.PVector;

public class Agent {
    private final PVector position;
    private PVector velocity;
    private final PVector goal;
    private final float radius = 12.5f;
    private final float maxSpeed = 2.0f;
    private final PApplet canvas;
    private int color;
    
    // Benchmark fields
    private float distanceTraveled = 0;
    private int collisions = 0;
    private boolean reachedGoal = false;
    private float timeToGoal = 0;

    public Agent(PApplet canvas, float x, float y, float goalX, float goalY) {
        this.canvas = canvas;
        this.position = new PVector(x, y);
        this.velocity = new PVector(0, 0);
        this.goal = new PVector(goalX, goalY);
        this.color = canvas.color(100, 150, 255);
    }

    public void update(float deltaTime) {
        PVector oldPos = position.copy();
        position.add(PVector.mult(velocity, deltaTime));
        
        // Calculate distance traveled for benchmarking
        distanceTraveled += PVector.dist(oldPos, position);
        
        // Check if reached goal
        if (!reachedGoal && PVector.dist(position, goal) < radius) {
            reachedGoal = true;
        }
        
        if (!reachedGoal) {
            timeToGoal += deltaTime;
        }
    }

    public void draw() {
        canvas.pushStyle();
        canvas.fill(color);
        canvas.stroke(0);
        canvas.circle(position.x, position.y, radius * 2);
        
        // Draw goal
        canvas.fill(255, 0, 0, 100);
        canvas.noStroke();
        canvas.circle(goal.x, goal.y, 10);
        
        // Draw direction line
        canvas.stroke(0, 150);
        PVector dir = PVector.mult(velocity, 20);
        canvas.line(position.x, position.y, position.x + dir.x, position.y + dir.y);
        canvas.popStyle();
    }

    // Getters and setters
    public PVector getPosition() { return position.copy(); }
    public PVector getVelocity() { return velocity.copy(); }
    public PVector getGoal() { return goal.copy(); }
    public float getRadius() { return radius; }
    public float getMaxSpeed() { return maxSpeed; }
    public void setVelocity(PVector velocity) { this.velocity = velocity.copy(); }
    public void setColor(int color) { this.color = color; }
    
    // Benchmarking getters
    public float getDistanceTraveled() { return distanceTraveled; }
    public int getCollisions() { return collisions; }
    public boolean hasReachedGoal() { return reachedGoal; }
    public float getTimeToGoal() { return timeToGoal; }
    public void incrementCollisions() { collisions++; }
}