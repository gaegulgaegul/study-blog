package me.gaegul.ch13.analyticalRules.collision;

public class C implements B, A {
    public static void main(String[] args) {
        new C().hello();
    }

    @Override
    public void hello() {
        B.super.hello();
    }
}
