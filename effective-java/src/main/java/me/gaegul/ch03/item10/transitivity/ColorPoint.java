package me.gaegul.ch03.item10.transitivity;

import java.awt.*;
import java.util.Objects;

public class ColorPoint {
    private final Point point;
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        this.point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }
    /*
     * 이 ColorPoint의 Point 뷰를 반환한다.
     */
    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint)) return false;
        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), color);
    }

    public static void main(String[] args) {
        // 대칭성 위배 예제
        Point point = new Point(1, 2);
        ColorPoint colorPoint = new ColorPoint(1, 2, Color.RED);

        System.out.println("point.equals(colorPoint) : " + point.equals(colorPoint.asPoint()));
        System.out.println("colorPoint.asPoint().equals(point) : " + colorPoint.asPoint().equals(point));

        // 추이성 위배 예제
        ColorPoint point1 = new ColorPoint(1, 2, Color.RED);
        Point point2 = new Point(1, 2);
        ColorPoint point3 = new ColorPoint(1, 2, Color.BLUE);

        System.out.println("point1.asPoint().equals(point2) : " + point1.asPoint().equals(point2));
        System.out.println("point2.equals(point3.asPoint()) : " + point2.equals(point3.asPoint()));
        System.out.println("point1.asPoint().equals(point3.asPoint()) : " + point1.asPoint().equals(point3.asPoint()));
    }
}
