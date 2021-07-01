package me.gaegul.ch09.templatemethod;

public abstract class OnlineBanking {
    public void processCustomer(int id) {
        Customer c = DataBase.getCustomerWithId(id);
    }

    abstract void makeCustomerHappy(Customer c);
}
