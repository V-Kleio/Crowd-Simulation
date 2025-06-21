package crowd_simulation;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {
    private final List<BenchmarkResult> results;
    private long startTime;
    private boolean isRunning;
    private String currentAlgorithm;
    
    public Benchmark() {
        this.results = new ArrayList<>();
        this.isRunning = false;
    }
    
    public void startBenchmark(String algorithmName) {
        this.currentAlgorithm = algorithmName;
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
    }
    
    public void endBenchmark(List<Agent> agents) {
        if (!isRunning) return;
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        BenchmarkResult result = new BenchmarkResult(currentAlgorithm, agents, duration);
        results.add(result);
        
        isRunning = false;
    }
    
    public List<BenchmarkResult> getResults() {
        return new ArrayList<>(results);
    }
    
    public void clearResults() {
        results.clear();
    }
    
    public String getComparisonReport() {
        if (results.isEmpty()) {
            return "No benchmark results available";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("Benchmark Comparison Report\n");
        report.append("===========================\n\n");
        
        for (BenchmarkResult result : results) {
            report.append(String.format("Algorithm: %s\n", result.getAlgorithmName()));
            report.append(String.format("Simulation Time: %.2f seconds\n", result.getSimulationTime() / 1000.0));
            report.append(String.format("Average Distance Traveled: %.2f\n", result.getAverageDistanceTraveled()));
            report.append(String.format("Total Collisions: %d\n", result.getTotalCollisions()));
            report.append(String.format("Agents Reached Goal: %d/%d (%.1f%%)\n", 
                result.getAgentsReachedGoal(), result.getTotalAgents(), result.getGoalReachPercentage()));
            report.append(String.format("Average Time to Goal: %.2f seconds\n", result.getAverageTimeToGoal()));
            report.append("\n");
        }
        
        return report.toString();
    }
    
    public static class BenchmarkResult {
        private final String algorithmName;
        private final long simulationTime;
        private float averageDistanceTraveled;
        private int totalCollisions;
        private int agentsReachedGoal;
        private final int totalAgents;
        private float averageTimeToGoal;
        
        public BenchmarkResult(String algorithmName, List<Agent> agents, long simulationTime) {
            this.algorithmName = algorithmName;
            this.simulationTime = simulationTime;
            this.totalAgents = agents.size();
            
            calculateMetrics(agents);
        }
        
        private void calculateMetrics(List<Agent> agents) {
            float totalDistance = 0;
            totalCollisions = 0;
            agentsReachedGoal = 0;
            float totalTimeToGoal = 0;
            int agentsWithGoalTime = 0;
            
            for (Agent agent : agents) {
                totalDistance += agent.getDistanceTraveled();
                totalCollisions += agent.getCollisions();
                
                if (agent.hasReachedGoal()) {
                    agentsReachedGoal++;
                    totalTimeToGoal += agent.getTimeToGoal();
                    agentsWithGoalTime++;
                }
            }
            
            averageDistanceTraveled = totalDistance / totalAgents;
            averageTimeToGoal = agentsWithGoalTime > 0 ? totalTimeToGoal / agentsWithGoalTime : 0;
        }
        
        // Getters
        public String getAlgorithmName() { return algorithmName; }
        public long getSimulationTime() { return simulationTime; }
        public float getAverageDistanceTraveled() { return averageDistanceTraveled; }
        public int getTotalCollisions() { return totalCollisions; }
        public int getAgentsReachedGoal() { return agentsReachedGoal; }
        public int getTotalAgents() { return totalAgents; }
        public float getGoalReachPercentage() { return (float)agentsReachedGoal / totalAgents * 100; }
        public float getAverageTimeToGoal() { return averageTimeToGoal; }
    }
}