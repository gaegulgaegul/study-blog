package me.gaegul.ch05.item26;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AntiMain {
    private static final Collection<Stamp> stamps = new ArrayList<>();

    public static void main(String[] args) {
//        stamps.add(new Coin()); // "unchecked call" 경고 발생

        for (Iterator i = stamps.iterator(); i.hasNext(); ) {
            Stamp stamp = (Stamp) i.next(); // ClassCastException 발생
            stamp.cancel();
        }

    }
}
