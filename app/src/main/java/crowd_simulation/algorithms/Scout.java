package crowd_simulation.algorithms;

import java.util.List;

import crowd_simulation.Agent;
import processing.core.PVector;

public class Scout implements CrowdAlgorithm {
    private float timeHorizon = 2.0f;
    private float neighborDistance = 50f;

    @Override
    public void updateAgents(List<Agent> agents, double deltaTime) {
        for (Agent agent : agents) {
            if (!agent.hasReachedGoal()) {
                PVector preferredVelocity = getPreferredVelocity(agent);
                PVector newVelocity = computeNewVelocity(agent, agents, preferredVelocity);
                agent.setVelocity(newVelocity);
            } else {
                agent.setVelocity(new PVector(0, 0));
            }
        }
    }

    private PVector getPreferredVelocity(Agent agent) {
        PVector goal = PVector.sub(agent.getGoal(), agent.getPosition());
        float distance = goal.mag();

        if (distance > 0) {
            goal.normalize();
            goal.mult(Math.min(agent.getMaxSpeed(), distance));
        }

        return goal;
    }

    private PVector computeNewVelocity(Agent agent, List<Agent> agents, PVector preferredVelocity) {
        PVector newVelocity = preferredVelocity.copy();

        for (Agent other : agents) {
            if (other != agent) {
                float distance = PVector.dist(agent.getPosition(), other.getPosition());

                if (distance > 0 && distance < neighborDistance) {
                    PVector avoidVelocity = computeAvoidVelocity(agent, other);
                    if (avoidVelocity.mag() > 0) {
                        newVelocity = avoidVelocity;
                        break;
                    }
                }
            }
        }

        newVelocity.limit(agent.getMaxSpeed());
        return newVelocity;
    }

    private PVector computeAvoidVelocity(Agent agent, Agent other) {
        PVector relativePosition = PVector.sub(other.getPosition(), agent.getPosition());
        PVector relativeVelocity = PVector.sub(other.getVelocity(), agent.getVelocity());

        float distanceSquared = relativePosition.magSq();
        float combinedRadius = agent.getRadius() + other.getRadius();
        float combinedRadiusSquared = combinedRadius * combinedRadius;

        float dotProduct = PVector.dot(relativeVelocity, relativePosition);

        if (dotProduct > 0) {
            return new PVector();
        }

        float discriminant = dotProduct * dotProduct - relativeVelocity.magSq() * (distanceSquared - combinedRadiusSquared);

        if (discriminant < 0) {
            return new PVector();
        }

        float timeToCollide = -(dotProduct + (float)Math.sqrt(discriminant)) / relativeVelocity.magSq();

        if (timeToCollide > timeHorizon || timeToCollide < 0) {
            return new PVector();
        }

        PVector futureRelativePosition = PVector.add(relativePosition, PVector.mult(relativeVelocity, timeToCollide));
        futureRelativePosition.normalize();
        futureRelativePosition.mult(agent.getMaxSpeed());

        return futureRelativePosition;
    }

    @Override
    public String toString() {
        return "Scout RVO Algorithm (Predictive Approach)";
    }

    public void setTimeHorizon(float timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    public void setNeighborDistance(float neighborDistance) {
        this.neighborDistance = neighborDistance;
    }
}