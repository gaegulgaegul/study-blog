package me.gaegul.ch09.debugging;

import me.gaegul.ch09.testing.Point;

import java.util.Arrays;
import java.util.List;

public class Debugging {
    public static void main(String[] args) {
        lambdaDebugging();
//        methodReferenceDebugging();
    }

    public static void lambdaDebugging() {
        List<Point> points = Arrays.asList(new Point(12, 2), null);
        points.stream().map(p -> p.getX()).forEach(System.out::println);
    }

    public static void methodReferenceDebugging() {
        List<Integer> numbers = Arrays.asList(1,2,3);
        numbers.stream().map(Debugging::divideByZero).forEach(System.out::println);
    }

    public static int divideByZero(int n) {
        return n / 0;
    }

}
