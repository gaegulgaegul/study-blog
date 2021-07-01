package me.gaegul.ch13.analyticalRules.subInterface;

public interface B extends A {
    default void hello() {
        System.out.println("Hello from B");
    }
}
