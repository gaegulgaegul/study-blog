package me.gaegul.ch02;

import me.gaegul.ch02.apple.Apple;
import me.gaegul.ch02.apple.ApplePredicate;
import me.gaegul.ch02.apple.AppleRedAndHeavyPredicate;
import me.gaegul.ch02.apple.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static me.gaegul.ch02.apple.Color.GREEN;
import static me.gaegul.ch02.apple.Color.RED;

public class AfterApp {

    public static void main(String[] args) {
        List<Apple> inventory = List.of(new Apple(GREEN, 100), new Apple(RED, 200));

//        List<Apple> redAndHeavyApples = filterApples(inventory, new AppleRedAndHeavyPredicate());
//        redAndHeavyApples.forEach(System.out::println);

        List<Apple> greenApples = filterApples(inventory, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return GREEN.equals(apple.getColor());
            }
        });
        greenApples.forEach(System.out::println);

        List<Apple> heavyApples = filter(inventory, new Predicate<Apple>() {
            @Override
            public boolean test(Apple apple) {
                return apple.getWeight() > 150;
            }
        });
        heavyApples.forEach(System.out::println);
    }

    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }

}
