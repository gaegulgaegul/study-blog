package me.gaegul.ch04.item15;

public class Main {

    public static void main(String[] args) {
        System.out.println(Foo.NAME + Bar.NAME);
    }

    private static class Foo {
        static final String NAME = "foo";
    }

    public static class Bar {
        static final String NAME = "bar";
    }
}
