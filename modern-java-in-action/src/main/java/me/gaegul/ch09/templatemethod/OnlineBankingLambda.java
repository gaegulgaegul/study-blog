package me.gaegul.ch09.templatemethod;

import java.util.function.Consumer;

public class OnlineBankingLambda {

    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
        Customer c = DataBase.getCustomerWithId(id);
        makeCustomerHappy.accept(c);
    }
}
