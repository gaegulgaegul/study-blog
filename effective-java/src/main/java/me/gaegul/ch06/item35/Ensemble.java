package me.gaegul.ch06.item35;

public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTER(4),
    QUINTET(5), SEXTET(6), SEPTET(7), OCTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTER(12);

    private final int numberOfMusicians;

    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }
}
