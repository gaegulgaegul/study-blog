package me.gaegul.ch03.item13;

import org.junit.Test;

import static org.junit.Assert.*;

public class StackTest {

    @Test
    public void Stack_super_clone_반환_테스트() {
        Stack stack = new Stack();
        stack.push(new Foo(1));
        stack.push(new Foo(2));

        Stack cloneStack = stack.clone();
        cloneStack.push(new Foo(3));

        assertNotEquals(stack, cloneStack);
        assertNotEquals(stack.getElements(), cloneStack.getElements());
        assertNotEquals(stack.getSize(), cloneStack.getSize());
    }
}