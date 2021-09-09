package me.gaegul.ch05.item29.generic;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack<E> {
//    private E[] elements;
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /*public Stack() {
        // 컴파일 오류가 발생한다.
        // 실체화 불가 타입으로 배열을 만들 수 없다.
        elements = new E[DEFAULT_INITIAL_CAPACITY];
    }*/

    // 배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
    // 따라서 타입 안전성을 보장하지만,
    // 이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0)
            throw new EmptyStackException();
        // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
        @SuppressWarnings("unchecked") E result = (E) elements[--size];
        elements[size] = null; // 다 쓴 객체 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }

}
