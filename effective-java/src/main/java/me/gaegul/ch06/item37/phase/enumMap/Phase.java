package me.gaegul.ch06.item37.phase.enumMap;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transaction {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS),
        CONDENSE(GAS, LIQUID), SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        Transaction(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화한다.
        private static final Map<Phase, Map<Phase, Transaction>> m = Stream.of(values())
                .collect(groupingBy(t -> t.from,
                        () -> new EnumMap<>(Phase.class),
                        toMap(t -> t.to,
                                t -> t,
                                (x, y) -> y,
                                () -> new EnumMap<>(Phase.class))));

        public static Transaction from(Phase from, Phase to) {
            return m.get(from).get(to);
        }

        public static Map<Phase, Map<Phase, Transaction>> getM() {
            return m;
        }
    }
}