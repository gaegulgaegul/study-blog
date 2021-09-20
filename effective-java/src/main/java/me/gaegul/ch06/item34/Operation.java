package me.gaegul.ch06.item34;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum Operation {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    private static final Map<String, Operation> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    // 지정한 문자열에 해당하는 Operation을 반환한다.
    public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }

    public static Operation inverse(Operation op) {
        switch (op) {
            case PLUS: return Operation.MINUS;
            case MINUS: return Operation.PLUS;
            case TIMES: return Operation.DIVIDE;
            case DIVIDE: return Operation.TIMES;
        }
        throw new AssertionError("알 수 없는 연산: " + op);
    }

    public abstract double apply(double x, double y);
}
