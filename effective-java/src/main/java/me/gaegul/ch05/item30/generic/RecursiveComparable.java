package me.gaegul.ch05.item30.generic;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class RecursiveComparable {

    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return Optional.ofNullable(result);
    }

}
