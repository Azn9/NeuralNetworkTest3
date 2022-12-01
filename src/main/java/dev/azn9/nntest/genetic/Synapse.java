package dev.azn9.nntest.genetic;

import java.security.SecureRandom;
import java.util.Random;

public class Synapse {

    private static final Random random = new SecureRandom();

    private final Node endNode;
    private double weight;
    private boolean enabled = true;

    public Synapse(Node endNode) {
        this.endNode = endNode;
        this.weight = Synapse.random.nextDouble(-1, 1);
    }

    public Synapse(Node endNode, double weight) {
        this.endNode = endNode;
        this.weight = weight;
    }

    public void mutate() {
        if (Synapse.random.nextDouble() < 0.1) {
            this.weight = Synapse.random.nextDouble(-1, 1);
        } else {
            this.weight += Synapse.random.nextGaussian() / 50;

            if (this.weight > 1) {
                this.weight = 1;
            }

            if (this.weight < -1) {
                this.weight = -1;
            }
        }
    }

    public Node getEndNode() {
        return this.endNode;
    }

    public double getWeight() {
        return this.weight;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
