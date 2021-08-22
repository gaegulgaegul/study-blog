package me.gaegul.ch04.item24;

public class AnonymousClass {
    private double x;
    private double y;

    public double operate() {
        Operator operator = new Operator() {
            @Override
            public double plus() {
                System.out.printf("%f + %f = %f\n", x, y, x + y);
                return x + y;
            }

            @Override
            public double minus() {
                System.out.printf("%f - %f = %f\n", x, y, x - y);
                return x - y;
            }
        };
        return operator.plus();
    }
}

interface Operator {
    double plus();

    double minus();
}
