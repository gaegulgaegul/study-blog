package me.gaegul.ch07.item46.topTenwords;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TopTenWords {
    public static void main(String[] args) {
        Map<String, Long> freq = new HashMap<>();
        List<String> topTen = freq.keySet().stream()
                .sorted(Comparator.comparing(freq::get).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
