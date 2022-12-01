package dev.azn9.nntest.genetic;

import java.util.ArrayList;
import java.util.List;

public class Network {

    private final List<Node> inputLayer = new ArrayList<>();
    private final List<Node> outputLayer = new ArrayList<>();
    private final List<List<Node>> hiddenLayers = new ArrayList<>();

    public Network(Network inputNetwork) {
        this.inputLayer.addAll(inputNetwork.inputLayer);
        this.outputLayer.addAll(inputNetwork.outputLayer);
        this.hiddenLayers.addAll(inputNetwork.hiddenLayers);

        for (Node node : this.inputLayer) {
            node.getOutputSynapses().forEach(Synapse::mutate);
        }

        for (List<Node> layer : this.hiddenLayers) {
            for (Node node : layer) {
                node.getOutputSynapses().forEach(Synapse::mutate);
            }
        }
    }

    public Network(int inputs, int outputs, int hiddenLayers, int hiddenNodes) {
        this.setup(inputs, outputs, hiddenLayers, hiddenNodes);
    }

    public Network(List<Node> inputLayer, List<List<Node>> hiddenLayers, List<Node> outputLayer) {
        this.inputLayer.addAll(inputLayer);
        this.hiddenLayers.addAll(hiddenLayers);
        this.outputLayer.addAll(outputLayer);

        this.connect();

        for (Node node : this.inputLayer) {
            node.getOutputSynapses().forEach(Synapse::mutate);
        }

        for (List<Node> layer : this.hiddenLayers) {
            for (Node node : layer) {
                node.getOutputSynapses().forEach(Synapse::mutate);
            }
        }
    }

    private void setup(int inputs, int outputs, int hiddenLayers, int hiddenNodes) {
        for (int i = 0; i < inputs; i++) {
            this.inputLayer.add(new Node(NodeType.INPUT));
        }

        for (int i = 0; i < outputs; i++) {
            this.outputLayer.add(new Node(NodeType.OUTPUT));
        }

        for (int i = 0; i < hiddenLayers; i++) {
            List<Node> layer = new ArrayList<>();

            for (int j = 0; j < hiddenNodes; j++) {
                layer.add(new Node(NodeType.HIDDEN));
            }

            this.hiddenLayers.add(layer);
        }

        this.connect();
    }

    private void connect() {
        for (Node node : this.inputLayer) {
            for (Node hiddenNode : this.hiddenLayers.get(0)) {
                node.addConnection(hiddenNode);
            }
        }

        for (int i = 0; i < this.hiddenLayers.size() - 1; i++) {
            List<Node> layer = this.hiddenLayers.get(i);
            List<Node> nextLayer = this.hiddenLayers.get(i + 1);

            for (Node node : layer) {
                for (Node nextNode : nextLayer) {
                    node.addConnection(nextNode);
                }
            }
        }

        for (Node node : this.hiddenLayers.get(this.hiddenLayers.size() - 1)) {
            for (Node node2 : this.outputLayer) {
                node.addConnection(node2);
            }
        }
    }

    public double[] feedForward(double[] inputs) {
        for (int i = 0; i < inputs.length; i++) {
            this.inputLayer.get(i).setOutput(inputs[i]);
        }

        for (Node node : this.inputLayer) {
            node.feedForward();
        }

        for (List<Node> layer : this.hiddenLayers) {
            for (Node node : layer) {
                node.feedForward();
            }
        }

        for (Node node : this.outputLayer) {
            node.feedForward();
        }

        double[] outputs = new double[this.outputLayer.size()];
        for (int i = 0; i < this.outputLayer.size(); i++) {
            Node node = this.outputLayer.get(i);
            outputs[i] = node.getOutputValue();
        }

        this.inputLayer.forEach(Node::reset);
        this.hiddenLayers.forEach(layer -> layer.forEach(Node::reset));
        this.outputLayer.forEach(Node::reset);

        return outputs;
    }

    public void reset() {
        this.inputLayer.forEach(Node::reset);
        this.hiddenLayers.forEach(layer -> layer.forEach(Node::reset));
        this.outputLayer.forEach(Node::reset);
    }

    @Override
    public Network clone() {
        List<Node> inputLayer = new ArrayList<>();
        for (Node node : this.inputLayer) {
            inputLayer.add(node.clone());
        }

        List<List<Node>> hiddenLayer = new ArrayList<>();
        for (List<Node> layer : this.hiddenLayers) {
            List<Node> newLayer = new ArrayList<>();
            for (Node node : layer) {
                newLayer.add(node.clone());
            }
            hiddenLayer.add(newLayer);
        }

        List<Node> outputLayer = new ArrayList<>();
        for (Node node : this.outputLayer) {
            outputLayer.add(node.clone());
        }

        return new Network(inputLayer, hiddenLayer, outputLayer);
    }

    public List<Node> getInputLayer() {
        return this.inputLayer;
    }

    public List<List<Node>> getHiddenLayers() {
        return this.hiddenLayers;
    }

    public List<Node> getOutputLayer() {
        return this.outputLayer;
    }
}
