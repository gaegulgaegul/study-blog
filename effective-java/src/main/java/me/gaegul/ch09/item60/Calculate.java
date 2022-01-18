package me.gaegul.ch09.item60;

import java.math.BigDecimal;

public class Calculate {
    public static void main(String[] args) {
        System.out.println("test() ======");
        test();
        System.out.println("wrongCalc() ======");
        wrongCalc();
        System.out.println("bigDecimalCalc() ======");
        bigDecimalCalc();
        System.out.println("intCalc() ======");
        intCalc();
    }

    private static void test() {
        System.out.println(1.03 - 0.42);
        System.out.println(1.00- 9 * 0.10);
    }

    private static void wrongCalc() {
        double funds = 1.00;
        int itemsBought = 0;
        for (double price = 0.10; funds >= price; price += 0.10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러):" + funds);
    }

    private static void bigDecimalCalc() {
        final BigDecimal TEN_CENTS = new BigDecimal(".10");

        int itemsBought = 0;
        BigDecimal funds = new BigDecimal("1.00");
        for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0; price = funds.add(TEN_CENTS)) {
            funds = funds.subtract(price);
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러):" + funds);
    }

    private static void intCalc() {
        int itemsBought = 0;
        int funds = 100;
        for (int price = 10; funds >= price; price += 10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러):" + funds);
    }
}
