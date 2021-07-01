package me.gaegul.ch10.mixed;

import me.gaegul.ch10.stock.Order;

import static me.gaegul.ch10.mixed.MixedBuilder.*;


public class StockApp {
    public static void main(String[] args) {
        Order order = forCustomer("BigBank",
                buy(t -> t.quantity(80).at(125.00).stock("IBM").on("NYSE")),
                sell(t -> t.quantity(50).at(375.00).stock("GOOGLE").on("NASDAQ")));
        System.out.println(order);
    }
}
