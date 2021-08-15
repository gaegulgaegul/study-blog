package me.gaegul.ch04.item18;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class InstrumentedSet<E> extends ForwardingSet<E> {

    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        System.out.println("add~"); // 호출 안됨
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

    public static void main(String[] args) {
        // InstrumentedSet -> 다른 Set 인스턴스를 감싸고 있다.
        // 데코레이터 패턴
        InstrumentedSet<String> s = new InstrumentedSet<>(new TreeSet<>());
        s.addAll(Arrays.asList("하나", "둘", "셋"));
        System.out.println(s.getAddCount()); // 3
    }
}
