package me.gaegul.ch02.apple;

import static me.gaegul.ch02.apple.Color.RED;

public class AppleRedAndHeavyPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return RED.equals(apple.getColor()) && apple.getWeight() > 150;
    }
}
