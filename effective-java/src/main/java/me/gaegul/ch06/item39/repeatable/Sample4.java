package me.gaegul.ch06.item39.repeatable;

import java.util.ArrayList;
import java.util.List;

public class Sample4 {
    @ExceptionTest(IndexOutOfBoundsException.class)
    @ExceptionTest(NullPointerException.class)
    public static void doublyBad() {
        List<String> list = new ArrayList<>();
        list.addAll(5, null);
    }
}
