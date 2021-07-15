package me.gaegul.ch19;

import java.util.function.DoubleUnaryOperator;

public class Currying {
    public static void main(String[] args) {
        DoubleUnaryOperator convertCtoF = curriedConverter(9.0 / 5, 32);
        DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
        DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214, 0);

        System.out.printf("24 °C = %.2f °F%n", convertCtoF.applyAsDouble(24));
        System.out.printf("US$100 = £%.2f%n", convertUSDtoGBP.applyAsDouble(100));
        System.out.printf("20 km = %.2f miles%n", convertKmtoMi.applyAsDouble(20));
    }

    public static double converter(double x, double f, double b) {
        return x * f + b;
    }

    public static DoubleUnaryOperator curriedConverter(double f, double b) {
        return x -> x * f + b;
    }
}
