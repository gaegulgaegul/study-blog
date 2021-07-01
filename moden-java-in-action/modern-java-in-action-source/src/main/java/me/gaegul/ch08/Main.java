package me.gaegul.ch08;

import java.util.*;

import static java.util.Map.*;

public class  Main {

    public static void main(String[] args) {
        List<String> asList = Arrays.asList("Raphael", "Olivia", "Thibaut");
//        asList.add(0, "Richard"); // UnsupportedOperationException
//        asList.add("Thibaut");

        List<String> listOf = List.of("Raphael", "Olivia", "Thibaut");
//        listOf.add("Chih-Chun");

//        Set<String> setOf = Set.of("Raphael", "Olivia", "Thibaut", "Raphael", "Olivia", "Thibaut"); // IllegalArgumentException
        Set<String> setOf = Set.of("Raphael", "Olivia", "Thibaut");
        System.out.println(setOf);

        Map<String, Integer> mapOf = of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
        Map<String, Integer> mapEntry = ofEntries(
                entry("Raphael", 30),
                entry("Olivia", 25),
                entry("Thibaut", 26)
        );
        System.out.println(mapOf);
        System.out.println(mapEntry);
    }

}
