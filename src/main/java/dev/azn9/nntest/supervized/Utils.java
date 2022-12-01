package dev.azn9.nntest.supervized;

public class Utils {

    public static double sigmoid(double d) {
        return 1.0 / (1.0 + Math.exp(-d));
    }

}
