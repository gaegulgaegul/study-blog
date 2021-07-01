package me.gaegul.ch10.nestedFunctions;

import me.gaegul.ch10.stock.Order;

import static me.gaegul.ch10.nestedFunctions.NestedFunctionOrderBuilder.*;

public class StockApp {
    public static void main(String[] args) {
        Order order = order("BigBank",
                buy(80,
                        stock("IBM", "NYSE"),
                        125.00),
                sell(50,
                        stock("GOOGLE", "NASDAQ"),
                        375.00)
        );
        System.out.println(order);
    }
}
