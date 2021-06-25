package me.gaegul.ch09.strategy;

public class Main {
    public static void main(String[] args) {
        execStrategy();
        execStrategyWithLambda();
    }

    private static void execStrategy() {
        Validator numericValidator = new Validator(new IsNumeric());
        boolean b1 = numericValidator.validate("aaaa");
        Validator lowerCaseValidator = new Validator(new isAllLowerCase());
        boolean b2 = lowerCaseValidator.validate("bbbb");
    }

    private static void execStrategyWithLambda() {
        Validator numericValidator = new Validator(s -> s.matches("\\d+"));
        boolean b1 = numericValidator.validate("aaaa");
        Validator lowerCaseValidator = new Validator(s -> s.matches("[a-z]+"));
        boolean b2 = lowerCaseValidator.validate("bbbb");
    }

}
