package me.gaegul.ch15;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static me.gaegul.ch15.Functions.*;

public class ExecutorServiceExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int x = 1337;

        useExecutorService(x);
        useExecutorServiceWithFuture(x);
    }

    private static void useExecutorService(int x) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> y = executorService.submit(() -> f(x));
        Future<Integer> z = executorService.submit(() -> g(x));
        System.out.println(y.get() + z.get());

        executorService.shutdown();
    }

    private static void useExecutorServiceWithFuture(int x) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> y = ff(x);
        Future<Integer> z = gg(x);
        System.out.println(y.get() + z.get());

        executorService.shutdown();
    }

}
