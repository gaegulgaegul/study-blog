package me.gaegul.ch03.item10.transitivity;

import java.awt.*;
import java.util.Objects;

public class ExceptColorPoint extends Point {
    private final Color color;

    public ExceptColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    // 대칭성 위배 예제
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof ColorPoint)) return false;
//        return super.equals(o) && ((ColorPoint) o).color == color;
//    }


    // 추이성 위배 예제
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;

        // o가 일반 Point면 색상을 무시하고 비교한다.
        if (!(o instanceof ExceptColorPoint)) return o.equals(this);

        // o가 ColorPoint면 색상까지 비교한다.
        return super.equals(o) && ((ExceptColorPoint) o).color == color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color);
    }

    public static void main(String[] args) {
        // 대칭성 위배 예제
        Point point = new Point(1, 2);
        ExceptColorPoint colorPoint = new ExceptColorPoint(1, 2, Color.RED);

        System.out.println("point.equals(colorPoint) : " + point.equals(colorPoint)); // true
        System.out.println("colorPoint.equals(point) : " + colorPoint.equals(point)); // false

        // 추이성 위배 예제
        ExceptColorPoint point1 = new ExceptColorPoint(1, 2, Color.RED);
        Point point2 = new Point(1, 2);
        ExceptColorPoint point3 = new ExceptColorPoint(1, 2, Color.BLUE);

        System.out.println("point1.equals(point2) : " + point1.equals(point2)); // true
        System.out.println("point2.equals(point3) : " + point2.equals(point3)); // true
        System.out.println("point1.equals(point3) : " + point1.equals(point3)); // false
    }
}
