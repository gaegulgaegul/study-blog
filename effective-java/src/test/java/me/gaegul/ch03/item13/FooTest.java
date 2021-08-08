package me.gaegul.ch03.item13;

import org.junit.Test;

import static org.junit.Assert.*;

public class FooTest {

    @Test
    public void clone_객체_테스트() {
        Foo foo = new Foo(1);

        assertTrue(foo.clone() != foo);
        assertTrue(foo.clone().getClass() == foo.getClass());
        assertTrue(foo.clone().equals(foo));
    }
}