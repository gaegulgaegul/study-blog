package me.gaegul.ch02.item02.telescopingConstructor;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================");

        NutritionFacts cocaCola = new NutritionFacts(240, 8, 100, 0, 35, 27);
        System.out.println("Telescoping Constructor Pattern");
        System.out.println(cocaCola);

        System.out.println("================================");
    }
}
