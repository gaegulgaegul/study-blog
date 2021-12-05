package me.gaegul.ch07.item46.streamWithFile;

import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class CorrectStreamWithFile {

    private static String file = "";

    public static void main(String[] args) {
        Map<String, Long> freq;
        try (Stream<String> words = new Scanner(file).tokens()) {
            freq = words.collect(groupingBy(String::toLowerCase, counting()));
        }
    }
}
