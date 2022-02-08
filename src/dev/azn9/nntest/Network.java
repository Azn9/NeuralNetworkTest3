package dev.azn9.nntest;

import java.util.ArrayList;
import java.util.List;

public class Network {

    private final List<Layer> layerList = new ArrayList<>();

    public Network(int inputNeuronCount, List<Integer> internalLayerNeuronCount, int outputNeuronCount) {
        this.layerList.add(new Layer(inputNeuronCount, true, 0));

        int lastLayerNeuronCount = inputNeuronCount;

        for (Integer count : internalLayerNeuronCount) {
            this.layerList.add(new Layer(inputNeuronCount, false, lastLayerNeuronCount));

            lastLayerNeuronCount = count;
        }

        this.layerList.add(new Layer(outputNeuronCount, false, lastLayerNeuronCount));
    }

    public Vector evaluate(Vector input) {
        List<Layer> list = this.layerList;
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Evaluating layer " + i + "...");

            Layer layer = list.get(i);
            input = layer.evaluate(input);
        }

        return input;
    }

    public List<Layer> getLayerList() {
        return this.layerList;
    }
}
