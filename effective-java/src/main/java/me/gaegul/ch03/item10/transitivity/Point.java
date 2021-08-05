package me.gaegul.ch03.item10.transitivity;

import java.awt.*;
import java.util.Objects;

public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return point.x == x && point.y == y;
    }

    // 리스코프 치환 원칙 위배
//    @Override
//    public boolean equals(Object o) {
//        if (o == null || o.getClass() != getClass()) return false;
//        Point point = (Point) o;
//        return point.x == x && point.y == y;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
