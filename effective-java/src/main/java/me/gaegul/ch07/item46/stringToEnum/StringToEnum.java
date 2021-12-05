package me.gaegul.ch07.item46.stringToEnum;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringToEnum {

    private enum Operation {
        ONE, TWO, THREE;
    }

    private static final Map<String, Operation> stringToEnum = Stream.of(Operation.values())
            .collect(Collectors.toMap(Object::toString, e -> e));
}
