package me.gaegul.ch15;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Functions {

    public static int f(int x) {
        return x * 1;
    }

    public static int g(int x) {
        return x * 2;
    }

    public static Future<Integer> ff(int x) {
        return new CompletableFuture<Integer>().completeAsync(() -> Integer.valueOf(f(x)));
    }

    public static Future<Integer> gg(int x) {
        return new CompletableFuture<Integer>().completeAsync(() -> Integer.valueOf(g(x)));
    }
}
