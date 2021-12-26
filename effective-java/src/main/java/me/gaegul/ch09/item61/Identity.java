package me.gaegul.ch09.item61;

import java.util.Comparator;

public class Identity {
    public static void main(String[] args) {
//        Comparator<Integer> naturalOrder = (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);

        Comparator<Integer> naturalOrder = (iBoxed, jBoxed) -> {
            int i = iBoxed, j = jBoxed; // 오토 박싱
            return i < j ? -1 : (i == j ? 0 : 1);
        };

        int compare = naturalOrder.compare(new Integer(42), new Integer(42));
        System.out.println(compare);
    }
}
