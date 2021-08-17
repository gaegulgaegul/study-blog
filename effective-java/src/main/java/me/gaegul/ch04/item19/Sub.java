package me.gaegul.ch04.item19;

import java.time.Instant;

public class Sub extends Super {

    private final Instant instant;

    public Sub() {
        this.instant = Instant.now();
    }

    @Override
    public void overrideMe() {
        System.out.println(instant);
    }

    public static void main(String[] args) {
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
