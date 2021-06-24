package me.gaegul.ch09.factory;

public class Main {

    public static void main(String[] args) {
        getFactoryProduct();
        getFactoryProductWithLambda();
    }

    private static void getFactoryProduct() {
        Product p = ProductFactory.createProduct("loan");
        System.out.println(p.getClass());
    }

    private static void getFactoryProductWithLambda() {
        Product p = ProductFactory.createProductWithLambda("loan");
        System.out.println(p.getClass());
    }

}
