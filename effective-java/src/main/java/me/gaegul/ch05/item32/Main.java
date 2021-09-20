package me.gaegul.ch05.item32;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static <T> T[] toArray(T... args) {
        return args;
    }

    static <T> T[] pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0: return toArray(a, b);
            case 1: return toArray(a, c);
            case 2: return toArray(b, c);
        }
        throw new AssertionError(); // 도달할 수 없다.
    }

    @SafeVarargs
    static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    static <T> List<T> flatten(List<List<? extends T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    static <T> List<T> pickTwoList(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0: return List.of(a, b);
            case 1: return List.of(a, c);
            case 2: return List.of(b, c);
        }
        throw new AssertionError(); // 도달할 수 없다.
    }

    public static void main(String[] args) {
//        String[] strings = pickTwo("좋은", "빠른", "저렴한");
//        System.out.println(strings);

        List<String> flatten = flatten(List.of("좋은"), List.of("빠른"), List.of("저렴한"));
        System.out.println(flatten);

        List<String> strings = pickTwoList("좋은", "빠른", "저렴한");
        System.out.println(strings);
    }
}
