package me.gaegul.ch08.item54;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

enum Cheese {
    STILTON
}

public class Shop {

    private final List<Cheese> cheesesInStock = Collections.emptyList();

    private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

    public List<Cheese> getCheeses() {
        return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);
    }

    public List<Cheese> getCheesesInStock() {
        return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArrayList<>(cheesesInStock);
    }

    public Cheese[] getCheeseArray() {
        return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
    }

    public static void main(String[] args) {
        Shop shop = new Shop();
        List<Cheese> cheeses = shop.getCheeses();
        if (cheeses != null && cheeses.contains(Cheese.STILTON))
            System.out.println("좋았어 바로 그거야.");
    }
}
