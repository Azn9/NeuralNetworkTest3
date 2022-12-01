package dev.azn9.nntest.genetic;

public class Functions {

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        return Functions.sigmoid(x) * (1 - Functions.sigmoid(x));
    }

}
