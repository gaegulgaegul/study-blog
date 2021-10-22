package me.gaegul.ch07.item45;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CartesianProduct {

    private enum Suit { ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

    private enum Rank { HEART, CLOVER, SPACE, DIAMOND }

    private static class Card {
        private Suit suit;
        private Rank rank;

        public Card(Suit suit, Rank rank) {
            this.suit = suit;
            this.rank = rank;
        }
    }

    private static List<Card> newDeck() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank));
            }
        }
        return result;
    }

    private static List<Card> newDeckStream() {
        return Stream.of(Suit.values())
               .flatMap(suit ->
                       Stream.of(Rank.values())
                               .map(rank -> new Card(suit, rank)))
                .collect(Collectors.toList());
    }
}
