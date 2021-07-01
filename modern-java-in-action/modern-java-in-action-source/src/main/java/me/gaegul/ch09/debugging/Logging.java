package me.gaegul.ch09.debugging;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Logging {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(2,3,4,5);
        getStream(numbers);
        getStreamWithPeek(numbers);
    }

    private static void getStream(List<Integer> numbers) {
        numbers.stream()
                .map(x -> x + 17)
                .filter(x -> x % 2 == 0)
                .limit(3)
                .forEach(System.out::println);
    }

    private static void getStreamWithPeek(List<Integer> numbers) {
        numbers.stream()
                .peek(x -> System.out.println("from stream: " + x))
                .map(x -> x + 17)
                .peek(x -> System.out.println("after map: " + x))
                .filter(x -> x % 2 == 0)
                .peek(x -> System.out.println("after filter: " + x))
                .limit(3)
                .peek(x -> System.out.println("after limit: " + x))
                .collect(toList());
    }
}
