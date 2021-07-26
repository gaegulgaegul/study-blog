package me.gaegul.ch02.item02.javaBeans;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================");

        NutritionFacts cocaCola = new NutritionFacts();
        cocaCola.setServingSize(240);
        cocaCola.setServings(8);
        cocaCola.setCalories(100);
        cocaCola.setSodium(35);
        cocaCola.setCarbohydrate(27);

        System.out.println("JavaBeans Pattern");
        System.out.println(cocaCola);

        System.out.println("================================");
    }
}
