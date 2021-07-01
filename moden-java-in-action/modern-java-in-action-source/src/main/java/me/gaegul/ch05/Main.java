package me.gaegul.ch05;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


public class Main {

    private static final String FILE_PATH = "src/main/java/me/gaegul/ch05/data.txt";

    public static void main(String[] args) {
        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );

        System.out.println("===========================================================");
        System.out.println("외부 반복 (for-each)");

        List<String> outlineNames = new ArrayList<>();
        for (Dish dish : menu) {
            outlineNames.add(dish.getName());
        }
        System.out.println(outlineNames.toString());

        System.out.println("===========================================================");
        System.out.println("내부 반복 (stream)");

        List<String> inlineNames = menu.stream().map(Dish::getName).collect(toList());
        System.out.println(inlineNames.toString());

        System.out.println("===========================================================");
        System.out.println("필터링");

        List<Dish> vegetarianMenu = menu.stream().filter(Dish::isVegetarian).collect(toList());
        vegetarianMenu.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("고유 요소 필터링");

        List<Integer> numbers = Arrays.asList(1,2,1,3,3,2,4);
        List<Integer> distinctNumbers = numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .collect(toList());
        System.out.println(distinctNumbers.toString());

        System.out.println("===========================================================");
        System.out.println("스트림 슬라이싱");
        System.out.println("===========================================================");
        System.out.println("1. takeWhile");

        List<Dish> specialMenu = Arrays.asList(
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER)
        );

        List<Dish> sliceMenu1 = specialMenu.stream()
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(toList());
        sliceMenu1.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("2. dropWhile");

        List<Dish> sliceMenu2 = specialMenu.stream()
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(toList());
        sliceMenu2.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("스트림 축소");

        List<Dish> specialDishes = specialMenu.stream()
                .filter(dish -> dish.getCalories() > 300)
                .limit(3)
                .collect(toList());
        specialDishes.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("요소 건너뛰기");

        List<Dish> dishes = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(toList());
        dishes.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("매핑");

        List<String> dishNames = menu.stream()
                .map(Dish::getName)
                .collect(toList());
        System.out.println(dishNames.toString());

        System.out.println("===========================================================");

        List<String> words = Arrays.asList("Modern", "Java", "In", "Action");
        List<Integer> wordLengths = words.stream()
                .map(String::length)
                .collect(toList());
        System.out.println(wordLengths.toString());

        System.out.println("===========================================================");

        List<Integer> dishNameLengths = menu.stream()
                .map(Dish::getName)
                .map(String::length)
                .collect(toList());
        System.out.println(dishNameLengths.toString());

        System.out.println("===========================================================");
        System.out.println("스트림 평면화");

        String[] arrayOfWords = new String[] {"Hello", "World"};
        List<String> distinctArrayOfWords = Arrays.stream(arrayOfWords)
                .map(word -> word.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(toList());
        System.out.println(distinctArrayOfWords.toString());

        System.out.println("===========================================================");
        System.out.println("검색과 매칭");
        System.out.println("===========================================================");
        System.out.println("anyMatch");

        if (menu.stream().anyMatch(Dish::isVegetarian)) {
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }

        System.out.println("===========================================================");
        System.out.println("allMatch");

        boolean allMatchHealthy = menu.stream()
                .allMatch(dish -> dish.getCalories() < 1000);
        System.out.println("isHealthy : " + allMatchHealthy);

        System.out.println("===========================================================");
        System.out.println("noneMatch");

        boolean noneMatchHealthy = menu.stream()
                .noneMatch(dish -> dish.getCalories() >= 1000);
        System.out.println("isHealthy : " + noneMatchHealthy);

        System.out.println("===========================================================");
        System.out.println("첫번쨰 요소 찾기");

        List<Integer> someNumbers = Arrays.asList(1,2,3,4,5);
        Optional<Integer> firstSquareDivisibleByThere = someNumbers.stream()
                .map(n -> n * n)
                .filter(n -> n % 3 == 0)
                .findFirst();
        System.out.println(firstSquareDivisibleByThere.get());

        System.out.println("===========================================================");
        System.out.println("리듀싱");

        int product = numbers.stream().reduce(1, (a, b) -> a + b);
        System.out.println(product);

        System.out.println("===========================================================");
        System.out.println("초깃값 없음");

        Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
        System.out.println(sum.get());

        System.out.println("===========================================================");
        System.out.println("최댓값");

        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println(max.get());

        System.out.println("===========================================================");
        System.out.println("최솟값");

        Optional<Integer> min = numbers.stream().reduce(Integer::min);
        System.out.println(min.get());

        System.out.println("===========================================================");
        System.out.println("기본형 특화 스트림");

        int calories = menu.stream().mapToInt(Dish::getCalories).sum();
        System.out.println(calories);

        System.out.println("===========================================================");
        System.out.println("숫자 스트림 활용 : 피타고라스 수");

        Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100)
                .boxed()
                .flatMap(a ->
                        IntStream.rangeClosed(a, 100)
                                .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                                .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})
                );
        pythagoreanTriples.limit(5)
                .forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

        System.out.println("===========================================================");
        System.out.println("피타고라스 수 개선");

        Stream<double[]> pythagoreanTriples2 = IntStream.rangeClosed(1, 100)
                .boxed()
                .flatMap(a ->
                        IntStream.rangeClosed(a, 100)
                                .mapToObj(b -> new double[]{a, b, Math.sqrt(a * a + b * b)})
                                .filter(t -> t[2] % 1 == 0)
                );
        pythagoreanTriples2.limit(5)
                .forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
        System.out.println("===========================================================");
        System.out.println("값으로 스트림 만들기");

        Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("파일로 스트림 만들기");

        long uniqueWords = 0;
        try (Stream<String> lines = Files.lines(Paths.get(FILE_PATH), Charset.defaultCharset())) {
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(",")))
                    .distinct()
                    .count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(uniqueWords);

        System.out.println("===========================================================");
        System.out.println("함수로 무한 스트림 만들기 - 피보나치 수열 만들기");
        System.out.println("===========================================================");
        System.out.println("iterate");

        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(10)
                .map(t -> t[0])
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.println("===========================================================");
        System.out.println("generate");

        IntSupplier fib = new IntSupplier() {
            private int previous = 0;
            private int current = 1;

            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrevious;
            }
        };
        IntStream.generate(fib)
                .limit(10)
                .forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.println("===========================================================");
    }

}
