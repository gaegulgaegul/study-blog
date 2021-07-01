package me.gaegul.ch10.DslMethodRefrence;

import me.gaegul.ch10.stock.Order;

import static me.gaegul.ch10.mixed.MixedBuilder.*;

public class TaxApp {
    public static void main(String[] args) {
        Order order = forCustomer("BigBank",
                buy(t -> t.quantity(80).at(125.00).stock("IBM").on("NYSE")),
                sell(t -> t.quantity(50).at(375.00).stock("GOOGLE").on("NASDAQ")));
        double value = new TaxCalculator().withTaxGeneral().withTaxSurcharge().calculate(order);
        System.out.println(value);

        double valueFromLambda = new TaxCalculatorWithLambda().with(Tax::regional).with(Tax::surcharge).calculate(order);
        System.out.println(valueFromLambda);
    }
}
