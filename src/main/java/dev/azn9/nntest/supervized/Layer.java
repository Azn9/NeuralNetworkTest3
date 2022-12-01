package dev.azn9.nntest.supervized;

import java.util.ArrayList;
import java.util.List;

import static dev.azn9.nntest.supervized.Utils.sigmoid;

public class Layer {

    private final int nodeCount;

    private final List<Vector> weights;
    private final Vector bias;
    private Vector out;

    private final boolean input;

    public Layer(int nodeCount, boolean input, int lastLayerNeuronCount) {
        this.nodeCount = nodeCount;

        this.weights = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            this.weights.add(new Vector(lastLayerNeuronCount));
        }

        this.bias = new Vector(nodeCount);
        this.out = new Vector(nodeCount);

        this.input = input;
    }

    public Vector evaluate(Vector input) {
        if (this.input) {
            System.out.println("  This layer is input, saving data");
            this.out.set(input);
            return this.out;
        }

        for (int i = 0; i < this.nodeCount; i++) {
            System.out.println("  Processing node " + i);

            Vector weigths = this.weights.get(i);
            double bias = this.bias.get(i);

            System.out.println("    Weights : " + weigths);
            System.out.println("    Bias : " + bias);

            double value = input.clone().multiply(weigths).sum() + bias;
            value = sigmoid(value);

            this.out.setValue(i, value);
        }

        return this.out;
    }

    public List<Vector> getWeights() {
        return this.weights;
    }

    public Vector getBias() {
        return this.bias;
    }

}
