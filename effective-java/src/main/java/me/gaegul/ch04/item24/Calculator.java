package me.gaegul.ch04.item24;

public class Calculator {

    public enum Operation {
        PLUS("+", (x, y) -> x + y),
        MINUS("-", (x, y) -> x - y);

        private final String token;
        private final Strategy strategy;

        Operation(String token, Strategy strategy) {
            this.token = token;
            this.strategy = strategy;
        }

        public double operate(double x, double y) {
            return strategy.operate(x, y);
        }

        public interface Strategy {
            double operate(double x, double y);
        }
    }
}
