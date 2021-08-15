package me.gaegul.ch04.item18;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class InstrumentedHashSet<E> extends HashSet<E> {

    private int addCount = 0;

    public InstrumentedHashSet() {}

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        System.out.println("add~");
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();

        // 3을 더한 후 상위 HashSet의 addAll 호출
        System.out.println("addAll : " + addCount);

        // 상위 HashSet의 add가 호출되지 않고 현재 재정의 된 InstrumentedHashSet의 add가 호출되었다.
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(Arrays.asList("하나", "둘", "셋"));
        System.out.println(s.getAddCount()); // 6
    }
}
