package me.gaegul.ch07.item46.topHits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.toMap;

public class TopHits {

    public class Artist {}
    public class Album {
        private Artist artist;
        private int sales;

        public Artist artist() {
            return artist;
        }

        public int sales() {
            return sales;
        }
    }

    public static void main(String[] args) {
        List<Album> albums = new ArrayList<>();
        Map<Artist, Album> topHits = albums.stream()
                .collect(toMap(Album::artist, Function.identity(), maxBy(comparing(Album::sales))));
    }
}
