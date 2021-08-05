package me.gaegul.ch03.item10.reflexivity;

import java.util.ArrayList;
import java.util.List;

public class Fruit {
    private String name;

    public Fruit(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Fruit apple = new Fruit("Apple");
        List<Fruit> fruits = new ArrayList<>();
        fruits.add(apple);
        boolean contains = fruits.contains(apple);
        System.out.println(contains);
    }
}
