package me.gaegul.ch09.item63;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringConnect {
    public static void main(String[] args) {
        List<String> items = IntStream.rangeClosed(0, 1000000)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        statement(items);
        statement2(items);
    }

    private static String statement(List<String> items) {
        long beforeTime = System.currentTimeMillis();

        String result = "";
        for (int i = 0; i < items.size(); i++) {
            result += items.get(i);
        }

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime)/1000;
        System.out.println("시간차이(m) : "+secDiffTime);

        return result;
    }

    private static String statement2(List<String> items) {
        long beforeTime = System.currentTimeMillis();

        final int lineWidth = 100;
        StringBuilder b = new StringBuilder(items.size() * lineWidth);
        for (int i = 0; i < items.size(); i++) {
            b.append(items.get(i));
        }

        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime)/1000;
        System.out.println("시간차이(m) : "+secDiffTime);

        return b.toString();
    }
}
