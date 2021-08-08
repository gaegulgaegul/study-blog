package me.gaegul.ch03.item13;

import java.util.Objects;

public class Foo implements Cloneable {
    private int number;

    public Foo() {
        System.out.println("Foo Constructor");
    }

    public Foo(int number) {
        System.out.println("Foo Constructor");
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foo foo = (Foo) o;
        return number == foo.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    protected Object clone() {
        try {
            System.out.println("Foo clone");
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

}
