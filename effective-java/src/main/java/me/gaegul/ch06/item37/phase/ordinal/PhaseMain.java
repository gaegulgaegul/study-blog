package me.gaegul.ch06.item37.phase.ordinal;

public class PhaseMain {
    public static void main(String[] args) {
        for (Phase from : Phase.values()) {
            for (Phase to : Phase.values()) {
                System.out.println(Phase.Transaction.from(from, to));
            }
        }
    }

}
