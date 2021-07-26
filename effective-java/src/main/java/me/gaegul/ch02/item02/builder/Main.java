package me.gaegul.ch02.item02.builder;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================");

        NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8)
                .calories(100).sodium(35).carbohydrate(27).build();
        System.out.println("Builder Pattern");
        System.out.println(cocaCola);

        System.out.println("================================");

        Pizza nyPizza = new NyPizza.Builder(NyPizza.Size.SMALL)
                .addTopping(Pizza.Topping.SAUSAGE)
                .addTopping(Pizza.Topping.ONION)
                .build();
        System.out.println(nyPizza);

        Pizza calzone = new Calzone.Builder()
                .addTopping(Pizza.Topping.HAM)
                .sauceInside()
                .build();
        System.out.println(calzone);

        System.out.println("================================");
    }
}
