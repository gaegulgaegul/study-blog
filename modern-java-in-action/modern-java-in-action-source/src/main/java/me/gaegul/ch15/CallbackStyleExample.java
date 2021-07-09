package me.gaegul.ch15;

import java.util.function.IntConsumer;

import static me.gaegul.ch15.Functions.f;
import static me.gaegul.ch15.Functions.g;

public class CallbackStyleExample {
    public static void main(String[] args) {
        int x = 1337;
        Result result = new Result();

        fr(x, y -> {
            result.left = y;
            System.out.println(result.left + result.right);
        });

        gr(x, z -> {
            result.right = z;
            System.out.println(result.left + result.right);
        });

    }

    private static class Result {
        private int left;
        private int right;
    }

    private static void fr(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(f(x));
    }

    private static void gr(int x, IntConsumer dealWithResult) {
        dealWithResult.accept(g(x));
    }
}
