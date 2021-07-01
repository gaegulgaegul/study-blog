package me.gaegul.ch09.templatemethod;

public interface DataBase {
    static Customer getCustomerWithId(int id) {
        return new Customer();
    }
}
