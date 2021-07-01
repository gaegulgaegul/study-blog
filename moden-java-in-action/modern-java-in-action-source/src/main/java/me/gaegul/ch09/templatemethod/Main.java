package me.gaegul.ch09.templatemethod;

public class Main {

    public static void main(String[] args) {
        new OnlineBankingLambda().processCustomer(1337, c -> System.out.println("Hello " + c.getName()));
    }
}
