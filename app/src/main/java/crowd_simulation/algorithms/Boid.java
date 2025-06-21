package crowd_simulation.algorithms;

import java.util.List;

import crowd_simulation.Agent;
import processing.core.PVector;

public class Boid implements CrowdAlgorithm {

    private float separationRadius = 50f;
    private float alignmentRadius = 80f;
    private float cohesionRadius = 100f;
    private float separationWeight = 2.0f;
    private float alignmentWeight = 1.0f;
    private float cohesionWeight = 1.0f;
    private float goalWeight = 3.0f;

    @Override
    public void updateAgents(List<Agent> agents, double deltaTime) {
        for (Agent agent : agents) {
            if (!agent.hasReachedGoal()) {
                PVector separation = separate(agent, agents);
                PVector alignment = align(agent, agents);
                PVector cohesion = cohesion(agent, agents);
                PVector goal = seek(agent, agent.getGoal());

                separation.mult(separationWeight);
                alignment.mult(alignmentWeight);
                cohesion.mult(cohesionWeight);
                goal.mult(goalWeight);

                PVector totalForce = new PVector();
                totalForce.add(separation)
                          .add(alignment)
                          .add(cohesion)
                          .add(goal);
                
                totalForce.limit(agent.getMaxSpeed());
                agent.setVelocity(totalForce);
            } else {
                agent.setVelocity(new PVector(0, 0));
            }
        }
    }

    private PVector separate(Agent agent, List<Agent> agents) {
        PVector steer = new PVector();
        int count = 0;

        for (Agent other : agents) {
            if (other != agent) {
                float distance = PVector.dist(agent.getPosition(), other.getPosition());
                if (distance > 0 && distance < separationRadius) {
                    PVector difference = PVector.sub(agent.getPosition(), other.getPosition());

                    difference.normalize();
                    difference.div(distance);
                    steer.add(difference);
                    count++;
                }
            }
        }

        if (count > 0) {
            steer.div(count);
            steer.normalize();
            steer.mult(agent.getMaxSpeed());
            steer.sub(agent.getVelocity());
        }

        return steer;
    }

    private PVector align(Agent agent, List<Agent> agents) {
        PVector sum = new PVector();
        int count = 0;

        for (Agent other : agents) {
            if (other != agent) {
                float distance = PVector.dist(agent.getPosition(), other.getPosition());
                if (distance > 0 && distance < alignmentRadius) {
                    sum.add(other.getVelocity());
                    count++;
                }
            }
        }

        if (count > 0) {
            sum.div(count);
            sum.normalize();
            sum.mult(agent.getMaxSpeed());
            sum.sub(agent.getVelocity());
            return sum;
        }
        return new PVector();
    }

    private PVector cohesion(Agent agent, List<Agent> agents) {
        PVector sum = new PVector();
        int count = 0;

        for (Agent other : agents) {
            if (other != agent) {
                float distance = PVector.dist(agent.getPosition(), other.getPosition());
                if (distance > 0 && distance < cohesionRadius) {
                    sum.add(other.getPosition());
                    count++;
                }
            }
        }

        if (count > 0) {
            sum.div(count);
            return seek(agent, sum);
        }

        return new PVector();
    }

    private PVector seek(Agent agent, PVector target) {
        PVector desired = PVector.sub(target, agent.getPosition());
        desired.normalize();
        desired.mult(agent.getMaxSpeed());

        PVector steer = PVector.sub(desired, agent.getVelocity());
        return steer;
    }

    @Override
    public String toString() {
        return "Boids Algorithm (Reactive Approach)";
    }

    // Setters for parameter fields
    public void setSeparationRadius(float separationRadius) {
        this.separationRadius = separationRadius;
    }

    public void setAlignmentRadius(float alignmentRadius) {
        this.alignmentRadius = alignmentRadius;
    }

    public void setCohesionRadius(float cohesionRadius) {
        this.cohesionRadius = cohesionRadius;
    }

    public void setSeparationWeight(float separationWeight) {
        this.separationWeight = separationWeight;
    }

    public void setAlignmentWeight(float alignmentWeight) {
        this.alignmentWeight = alignmentWeight;
    }

    public void setCohesionWeight(float cohesionWeight) {
        this.cohesionWeight = cohesionWeight;
    }

    public void setGoalWeight(float goalWeight) {
        this.goalWeight = goalWeight;
    }
}