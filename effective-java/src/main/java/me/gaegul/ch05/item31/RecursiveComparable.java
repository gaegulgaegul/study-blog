package me.gaegul.ch05.item31;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecursiveComparable {

    public static <E extends Comparable<? super E>> Optional<E> max(List<? extends E> list) {
        E result = null;
        for (E e : list) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return Optional.ofNullable(result);
    }

}
