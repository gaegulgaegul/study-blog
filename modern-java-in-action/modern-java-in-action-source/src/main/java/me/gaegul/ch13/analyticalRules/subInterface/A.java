package me.gaegul.ch13.analyticalRules.subInterface;

public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}
