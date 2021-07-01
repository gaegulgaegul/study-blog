package me.gaegul.ch13.analyticalRules.diamond;

public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}
