package me.gaegul.ch08.item55;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class StreamMax {

    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        return c.stream().max(Comparator.naturalOrder());
    }
}
