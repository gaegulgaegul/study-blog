package me.gaegul.ch04.item15;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImmutableArray {

    private static final Thing[] PRIVATE_VALUES = new Thing[]{};

    // public 불변 리스트 추가
    public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

    // PRIVATE_VALUES 복사본을 반환하는 public 메서드
    public static final Thing[] values() {
        return PRIVATE_VALUES.clone();
    }

    private class Thing implements Cloneable {
        private String some;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
