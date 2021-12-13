package me.gaegul.ch08.item49;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyList {

    public static List<Integer> toList(int[] array) {
        Objects.requireNonNull(array);
        return Arrays.stream(array)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
    }

}
