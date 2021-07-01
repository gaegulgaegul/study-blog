package me.gaegul.ch13.analyticalRules.sameSignature;

public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}
