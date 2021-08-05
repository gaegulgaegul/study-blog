package me.gaegul.ch03.item10.transitivity;

import java.awt.*;
import java.util.Objects;

public class SmellPoint extends Point {

    private final Color color;

    public SmellPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    // 무한 재귀 위험에 빠진다.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        if (!(o instanceof SmellPoint)) return o.equals(this);
        return super.equals(o) && ((SmellPoint) o).color == color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color);
    }

    public static void main(String[] args) {
        ColorPoint colorPoint = new ColorPoint(1, 2, Color.BLUE);
        SmellPoint smellPoint = new SmellPoint(1, 2, Color.RED);

        System.out.println("colorPoint.equals(smellPoint) : " + colorPoint.equals(smellPoint));
    }
}
