package me.gaegul.ch06.item37.phase.ordinal;

public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transaction {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
        private static final Transaction[][] TRANSACTIONS = {
                { null, MELT, SUBLIME },
                { FREEZE, null, BOIL},
                { DEPOSIT, CONDENSE, null }
        };

        // 한 상태에서 다른 상태로의 전이를 반환한다.
        public static Transaction from(Phase from, Phase to) {
            return TRANSACTIONS[from.ordinal()][to.ordinal()];
        }
    }
}