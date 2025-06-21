package crowd_simulation.algorithms;

import java.util.List;

import crowd_simulation.Agent;
import processing.core.PVector;

public class Ghost implements CrowdAlgorithm {
    @Override
    public void updateAgents(List<Agent> agents, double deltaTime) {
        for (Agent agent : agents) {
            if (!agent.hasReachedGoal()) {
                PVector goal = PVector.sub(agent.getGoal(), agent.getPosition());
                goal.normalize();
                goal.mult(agent.getMaxSpeed());
                agent.setVelocity(goal);
            } else {
                agent.setVelocity(new PVector(0, 0));
            }
        }

        checkCollisions(agents);
    }

    private void checkCollisions(List<Agent> agents) {
        for (int i = 0; i < agents.size(); i++) {
            for (int j = i + 1; j < agents.size(); j++) {
                Agent firstAgent = agents.get(i);
                Agent secondAgent = agents.get(j);

                float distance = PVector.dist(firstAgent.getPosition(), secondAgent.getPosition());
                if (distance < firstAgent.getRadius() + secondAgent.getRadius()) {
                    firstAgent.incrementCollisions();
                    secondAgent.incrementCollisions();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Ghost Algorithm (Naive Approach)";
    }
}