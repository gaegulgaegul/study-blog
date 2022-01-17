package me.gaegul.ch09.item58;

import java.util.*;

public class ForEach {

    static Collection<Suit> suits = Arrays.asList(Suit.values());
    static Collection<Rank> ranks = Arrays.asList(Rank.values());

    public static void main(String[] args) {
//        wrongIteratorNextMethod();
//        wrongFor();
//        fixFor();
        forEach();
    }

    private static void wrongIteratorNextMethod() {
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();)
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();)
                deck.add(new Card(i.next(), j.next()));

        deck.forEach(System.out::println);
    }

    private static void wrongFor() {
        Collection<Face> faces = EnumSet.allOf(Face.class);
        for (Iterator<Face> i = faces.iterator(); i.hasNext();)
            for (Iterator<Face> j = faces.iterator(); j.hasNext();)
                System.out.println(i.next() + " " + j.next());
    }

    private static void fixFor() {
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            Suit suit = i.next();
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();) {
                deck.add(new Card(suit, j.next()));
            }
        }

        deck.forEach(System.out::println);
    }

    private static void forEach() {
        List<Card> deck = new ArrayList<>();
        for (Suit suit : suits)
            for (Rank rank : ranks)
                deck.add(new Card(suit, rank));
        deck.forEach(System.out::println);
    }
}
