package crowd_simulation.algorithms;

import java.util.List;

import crowd_simulation.Agent;

public interface CrowdAlgorithm {
    void updateAgents(List<Agent> agents, double deltaTime);
}