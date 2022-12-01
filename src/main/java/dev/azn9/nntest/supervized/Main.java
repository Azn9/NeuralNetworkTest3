package dev.azn9.nntest.supervized;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Network network = new Network(2, List.of(2), 2);

        network.getLayerList().get(1).getBias().setValue(0, 0.25);
        network.getLayerList().get(1).getWeights().get(0).setValue(0, 0.3);
        network.getLayerList().get(1).getWeights().get(0).setValue(1, -0.4);

        network.getLayerList().get(1).getBias().setValue(1, 0.45);
        network.getLayerList().get(1).getWeights().get(1).setValue(0, 0.2);
        network.getLayerList().get(1).getWeights().get(1).setValue(1, 0.6);

        network.getLayerList().get(2).getBias().setValue(0, 0.15);
        network.getLayerList().get(2).getWeights().get(0).setValue(0, 0.7);
        network.getLayerList().get(2).getWeights().get(0).setValue(1, 0.5);

        network.getLayerList().get(2).getBias().setValue(1, 0.35);
        network.getLayerList().get(2).getWeights().get(1).setValue(0, -0.3);
        network.getLayerList().get(2).getWeights().get(1).setValue(1, -0.1);

        Vector input = new Vector(2, 2, 3);
        Vector out = network.evaluate(input);

        System.out.println("\n\nInput:");
        System.out.println(input);
        System.out.println("\nOutput:");
        System.out.println(out);
    }
}
