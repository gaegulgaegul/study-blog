package me.gaegul.ch05.item26;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Main {
    // Stamp 인스턴스만 취급한다.
    private static final Collection stamps = new ArrayList<>();

    public static void main(String[] args) {
        stamps.add(new Coin()); // "unchecked call" 경고 발생

        for (Iterator i = stamps.iterator(); i.hasNext(); ) {
            Stamp stamp = (Stamp) i.next(); // ClassCastException 발생
            stamp.cancel();
        }

    }
}
