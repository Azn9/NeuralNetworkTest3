package dev.azn9.nntest.genetic;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final List<Synapse> outputSynapses = new ArrayList<>();
    private final NodeType nodeType;

    private double inputValue = 0;
    private double outputValue = 0;
    private boolean initialized = false;

    public Node(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void feedForward() {
        if (this.nodeType != NodeType.INPUT) {
            this.outputValue = Functions.sigmoid(this.inputValue);
        }

        this.initialized = true;

        for (Synapse synapse : this.outputSynapses) {
            if (synapse.isEnabled()) {
                synapse.getEndNode().inputValue += synapse.getWeight() * this.outputValue;
            }
        }
    }

    public void addConnection(Node node2) {
        Synapse synapse = new Synapse(node2);

        this.outputSynapses.add(synapse);
    }

    public void setOutput(double input) {
        this.outputValue = input;
    }

    public NodeType getNodeType() {
        return this.nodeType;
    }

    public double getOutputValue() {
        return this.outputValue;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public List<Synapse> getOutputSynapses() {
        return this.outputSynapses;
    }

    public void reset() {
        this.inputValue = 0;
        this.initialized = false;
    }

    @Override
    public Node clone() {
        return new Node(this.nodeType);
    }
}
