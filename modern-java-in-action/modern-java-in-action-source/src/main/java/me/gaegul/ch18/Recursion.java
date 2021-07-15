package me.gaegul.ch18;

import java.util.stream.LongStream;

public class Recursion {
    public static void main(String[] args) {
        System.out.println(factorialIterative(5));
        System.out.println(factorialRecursion(5));
        System.out.println(factorialStreams(5));
        System.out.println(factorialTailRecursion(5));
    }

    public static int factorialIterative(int n) {
        int r = 1;
        for (int i = 1; i <= n; i++) {
            r *= i;
        }
        return r;
    }

    public static int factorialRecursion(int n) {
        return n == 1 ? 1 : n * factorialRecursion(n-1);
    }

    public static long factorialStreams(long n) {
        return LongStream.rangeClosed(1, n)
                .reduce(1, (long a, long b) -> a * b);
    }

    public static long factorialTailRecursion(long n) {
        return factorialHelper(1, n);
    }

    public static long factorialHelper(long acc, long n) {
        return n == 1 ? acc : factorialHelper(acc * n, n-1);
    }
}
