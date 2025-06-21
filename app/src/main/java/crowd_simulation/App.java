package crowd_simulation;

import java.util.ArrayList;
import java.util.List;

import crowd_simulation.algorithms.Boid;
import crowd_simulation.algorithms.CrowdAlgorithm;
import crowd_simulation.algorithms.Ghost;
import crowd_simulation.algorithms.Scout;
import processing.core.PApplet;

public class App extends PApplet {
    private List<Agent> agents;
    private CrowdAlgorithm currentAlgorithm;
    private Benchmark benchmark;
    
    // Algorithm instances
    private Ghost ghost;
    private Boid boid;
    private Scout scout;
    
    // Simulation parameters
    private final int agentCount = 20;
    private boolean isRunning = false;
    private boolean showBenchmark = false;
    private float lastTime;
    
    // GUI state
    private int currentAlgorithmIndex = 0;

    @Override
    public void settings() {
        size(1200, 800);
    }

    @Override
    public void setup() {
        background(255);
        
        // Initialize algorithms
        ghost = new Ghost();
        boid = new Boid();
        scout = new Scout();
        currentAlgorithm = ghost;
        
        // Initialize benchmark system
        benchmark = new Benchmark();
        
        // Initialize simulation
        resetSimulation();
        lastTime = millis();
    }

    @Override
    public void draw() {
        background(255);
        
        float currentTime = millis();
        float deltaTime = (currentTime - lastTime) / 1000.0f; // Convert to seconds
        lastTime = currentTime;
        
        if (isRunning) {
            // Update agents using current algorithm
            currentAlgorithm.updateAgents(agents, deltaTime);
            
            // Update agent positions
            for (Agent agent : agents) {
                agent.update(deltaTime);
            }
        }
        
        // Draw agents
        for (Agent agent : agents) {
            agent.draw();
        }
        
        // Draw GUI
        drawGUI();
        
        // Check if simulation should end (all agents reached goal or timeout)
        if (isRunning && checkSimulationEnd()) {
            stopSimulation();
        }
    }
    
    private void drawGUI() {
        // Draw control panel
        fill(240);
        stroke(0);
        rect(width - 300, 0, 300, height);
        
        fill(0);
        textSize(16);
        text("Crowd Simulation Control", width - 290, 25);
        
        // Algorithm selection
        text("Algorithm: " + currentAlgorithm.toString(), width - 290, 60);
        
        // Control buttons
        text("Controls:", width - 290, 100);
        text("SPACE - Start/Stop", width - 290, 120);
        text("R - Reset", width - 290, 140);
        text("A - Next Algorithm", width - 290, 160);
        text("B - Toggle Benchmark", width - 290, 180);
        text("C - Clear Benchmark", width - 290, 200);
        
        // Simulation info
        text("Agents: " + agentCount, width - 290, 240);
        text("Status: " + (isRunning ? "Running" : "Stopped"), width - 290, 260);
        
        // Agent statistics
        int reachedGoal = 0;
        int totalCollisions = 0;
        for (Agent agent : agents) {
            if (agent.hasReachedGoal()) reachedGoal++;
            totalCollisions += agent.getCollisions();
        }
        
        text("Reached Goal: " + reachedGoal + "/" + agentCount, width - 290, 300);
        text("Total Collisions: " + totalCollisions, width - 290, 320);
        
        // Benchmark results
        if (showBenchmark) {
            text("Benchmark Results:", width - 290, 360);
            String[] lines = benchmark.getComparisonReport().split("\n");
            int y = 380;
            for (String line : lines) {
                if (y > height - 20) break;
                text(line, width - 290, y);
                y += 15;
            }
        }
        
        // Parameter adjustment for reactive algorithm
        if (currentAlgorithm instanceof Boid) {
            text("Boids Parameters:", width - 290, 450);
            text("1-5: Adjust weights", width - 290, 470);
            text("Q/W: Separation radius", width - 290, 490);
        }
    }
    
    private boolean checkSimulationEnd() {
        // End if all agents reached goal
        for (Agent agent : agents) {
            if (!agent.hasReachedGoal()) {
                return false;
            }
        }
        return true;
    }
    
    private void resetSimulation() {
        agents = new ArrayList<>();
        
        // Create agents in a circle formation with goals on opposite side
        float centerX = (width - 300) / 2; // Account for GUI panel
        float centerY = height / 2;
        float radius = 150;
        
        for (int i = 0; i < agentCount; i++) {
            float angle = (float)(i * (Math.PI * 2 / agentCount));
            float startX = centerX + radius * cos(angle);
            float startY = centerY + radius * sin(angle);
            
            // Goal is on opposite side
            float goalX = centerX - radius * cos(angle);
            float goalY = centerY - radius * sin(angle);
            
            Agent agent = new Agent(this, startX, startY, goalX, goalY);
            
            // Set different colors for different algorithms
            if (currentAlgorithm instanceof Ghost) {
                agent.setColor(color(255, 100, 100));
            } else if (currentAlgorithm instanceof Ghost) {
                agent.setColor(color(100, 255, 100));
            } else {
                agent.setColor(color(100, 100, 255));
            }
            
            agents.add(agent);
        }
        
        isRunning = false;
    }
    
    private void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            benchmark.startBenchmark(currentAlgorithm.toString());
        }
    }
    
    private void stopSimulation() {
        if (isRunning) {
            isRunning = false;
            benchmark.endBenchmark(agents);
        }
    }
    
    @Override
    public void keyPressed() {
        switch (key) {
            case ' ' -> {
                // Space - Start/Stop
                if (isRunning) {
                    stopSimulation();
                } else {
                    startSimulation();
                }
            }
                
            case 'r', 'R' -> {
                // Reset
                stopSimulation();
                resetSimulation();
            }
            case 'a', 'A' -> {
                // Next algorithm
                stopSimulation();
                currentAlgorithmIndex = (currentAlgorithmIndex + 1) % 3;
                switch (currentAlgorithmIndex) {
                    case 0 -> currentAlgorithm = ghost;
                    case 1 -> currentAlgorithm = boid;
                    case 2 -> currentAlgorithm = scout;
                }
                resetSimulation();
            }
                
            case 'b', 'B' -> // Toggle benchmark display
                showBenchmark = !showBenchmark;
            case 'c', 'C' -> // Clear benchmark
                benchmark.clearResults();
                
            case '1' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setSeparationWeight(1.0f);
            }
            case '2' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setSeparationWeight(2.0f);
            }
                
            case '3' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setSeparationWeight(3.0f);
            }
            case '4' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setAlignmentWeight(2.0f);
            }
            case '5' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setCohesionWeight(2.0f);
            }
            case 'q', 'Q' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setSeparationRadius(30f);
            }
            case 'w', 'W' -> {
                if (currentAlgorithm instanceof Boid boid1) boid1.setSeparationRadius(70f);
            }
        }
        // Parameter adjustment for reactive algorithm
            }

    public static void main(String[] args) {
        PApplet.main("crowd_simulation.App");
    }
}