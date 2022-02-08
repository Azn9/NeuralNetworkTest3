package dev.azn9.nntest;

import java.util.Arrays;

public class Vector {

    private final int size;
    private final double[] data;

    public Vector(int size, double... initialData) {
        this.size = size;
        this.data = new double[size];

        for (int i = 0; i < size; i++) {
            if (i < initialData.length) {
                this.data[i] = initialData[i];
            } else {
                this.data[i] = 0;
            }
        }
    }

    public Vector add(double d) {
        for (int i = 0; i < this.size; i++) {
            this.data[i] += d;
        }

        return this;
    }

    public Vector multiply(double d) {
        for (int i = 0; i < this.size; i++) {
            this.data[i] *= d;
        }

        return this;
    }

    public Vector add(Vector vector) {
        for (int i = 0; i < this.size; i++) {
            this.data[i] += vector.data[i];
        }

        return this;
    }

    public Vector multiply(Vector vector) {
        for (int i = 0; i < this.size; i++) {
            this.data[i] *= vector.data[i];
        }

        return this;
    }

    public void setValue(int i, double d) {
        this.data[i] = d;
    }

    public int getSize() {
        return this.size;
    }

    public double[] getData() {
        return this.data;
    }

    public double get(int i) {
        return this.data[i];
    }

    public double sum() {
        return Arrays.stream(this.data).sum();
    }

    @Override
    public String toString() {
        return "Vector{" +
                "size=" + this.size +
                ", data=" + Arrays.toString(this.data) +
                '}';
    }

    public void set(Vector input) {
        for (int i = 0; i < this.size; i++) {
            this.data[i] = input.data[i];
        }
    }

    @Override
    public Vector clone() {
        return new Vector(this.size, this.data);
    }
}
