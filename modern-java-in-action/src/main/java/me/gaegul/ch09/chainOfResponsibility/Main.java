package me.gaegul.ch09.chainOfResponsibility;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Main {
    public static void main(String[] args) {
        getChainOfResponsibility();
        getChainOfResponsibilityWithLambda();
    }

    private static void getChainOfResponsibility() {
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        SpellCheckerProcessing p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);
        String result = p1.handle("Aren't labda really sexy?!!");
        System.out.println(result);
    }

    private static void getChainOfResponsibilityWithLambda() {
        UnaryOperator<String> headerProcessing = text -> "Form Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellCheckerProcessing = text -> text.replace("labda", "lambda");
        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        String result = pipeline.apply("Aren't labda really sexy?!!");
        System.out.println(result);
    }
}
