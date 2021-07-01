package me.gaegul.ch02;

import me.gaegul.ch02.apple.Apple;
import me.gaegul.ch02.apple.Color;

import java.util.ArrayList;
import java.util.List;

import static me.gaegul.ch02.apple.Color.GREEN;
import static me.gaegul.ch02.apple.Color.RED;

public class BeforeApp {

    public static void main(String[] args) {
        List<Apple> inventory = List.of(new Apple(GREEN, 100), new Apple(RED, 200));

//        List<Apple> greenApples = filterGreenApples(inventory);
//        greenApples.forEach(System.out::println);

//        List<Apple> redApples = filterApplesByColor(inventory, RED);
//        redApples.forEach(System.out::println);
//
//        List<Apple> heavyApples = filterApplesByWeight(inventory, 150);
//        heavyApples.forEach(System.out::println);

        List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
        greenApples.forEach(System.out::println);

        List<Apple> heavyApples = filterApples(inventory, null, 150, false);
        heavyApples.forEach(System.out::println);
    }

    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (GREEN.equals(apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getWeight() > weight) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if ((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)) {
                result.add(apple);
            }
        }
        return result;
    }

}
