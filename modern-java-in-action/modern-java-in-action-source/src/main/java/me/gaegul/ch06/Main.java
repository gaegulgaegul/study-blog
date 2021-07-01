package me.gaegul.ch06;

import me.gaegul.ch05.Dish;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;
import static me.gaegul.ch05.Dish.Type;

public class Main {

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

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 요약 연산");

		int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
		double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));
		System.out.println(totalCalories);
		System.out.println(avgCalories);

		IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
		System.out.println(menuStatistics.toString());

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 문자열 연결");

		String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
		System.out.println(shortMenu);

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 범용 리듀싱 요약 연산");

		int totalCaloriesReduce = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
		System.out.println(totalCaloriesReduce);

		Optional<Dish> mostCalorieDish = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
		System.out.println(mostCalorieDish.get().toString());

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 그룹화");

		Map<Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
		System.out.println(dishesByType.toString());

/*
{OTHER=[
Dish(name=french fries, vegetarian=true, calories=530, type=OTHER),
Dish(name=rice, vegetarian=true, calories=350, type=OTHER),
Dish(name=season fruit, vegetarian=true, calories=120, type=OTHER),
Dish(name=pizza, vegetarian=true, calories=550, type=OTHER)
],

FISH=[
Dish(name=prawns, vegetarian=false, calories=300, type=FISH),
Dish(name=salmon, vegetarian=false, calories=450, type=FISH)
],

MEAT=[
Dish(name=pork, vegetarian=false, calories=800, type=MEAT),
Dish(name=beef, vegetarian=false, calories=700, type=MEAT),
Dish(name=chicken, vegetarian=false, calories=400, type=MEAT)
]}
 */
		Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
					groupingBy(dish -> {
						if (dish.getCalories() <= 400) return CaloricLevel.DIET;
						else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
						else return CaloricLevel.FAT;
					})
				);
		System.out.println(dishesByCaloricLevel.toString());

		Map<Type, List<Dish>> caloricDishesByType = menu.stream()
				.filter(dish -> dish.getCalories() > 500)
				.collect(groupingBy(Dish::getType));
		System.out.println(caloricDishesByType.toString());

		Map<Type, List<Dish>> caloricDishesByTypeFilering = menu.stream()
				.collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));
		System.out.println(caloricDishesByTypeFilering.toString());

		Map<Type, List<String>> dishNamesByType = menu.stream().collect(groupingBy(Dish::getType,
				mapping(Dish::getName, toList())));
		System.out.println(dishNamesByType.toString());

		Map<String, List<String>> dishTags = new HashMap<>();
		dishTags.put("pork", Arrays.asList("greasy", "salty"));
		dishTags.put("beef", Arrays.asList("salty", "roasted"));
		dishTags.put("chicken", Arrays.asList("fried", "crisp"));
		dishTags.put("french fries", Arrays.asList("greasy", "fried"));
		dishTags.put("rice", Arrays.asList("light", "natural"));
		dishTags.put("season fruit", Arrays.asList("fresh", "natural"));
		dishTags.put("pizza", Arrays.asList("tasty", "salty"));
		dishTags.put("prawns", Arrays.asList("tasty", "roasted"));
		dishTags.put("salmon", Arrays.asList("delicious", "fresh"));

		Map<Type, Set<String>> dishNamesByTypeFlatMapping = menu.stream().collect(groupingBy(Dish::getType,
				flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
		System.out.println(dishNamesByTypeFlatMapping.toString());

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 다수준 그룹화");

		Map<Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(
					groupingBy(Dish::getType,
						groupingBy(dish -> {
							if (dish.getCalories() <= 400) return CaloricLevel.DIET;
							else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
							else return CaloricLevel.FAT;
						})
					)
				);
		System.out.println(dishesByTypeCaloricLevel.toString());

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 서브그룹으로 데이터 수집");

		Map<Type, Long> typesCount = menu.stream().collect(groupingBy(Dish::getType, counting()));
		System.out.println(typesCount.toString());

		Map<Type, Optional<Dish>> mostCaloricByType = menu.stream()
				.collect(groupingBy(Dish::getType,
						maxBy(comparingInt(Dish::getCalories))));
		System.out.println(mostCaloricByType.toString());

		Map<Type, Dish> mostCaloricByTypeWithoutOptional = menu.stream()
				.collect(groupingBy(Dish::getType,
						collectingAndThen(
								maxBy(comparingInt(Dish::getCalories)),
								Optional::get)));
		System.out.println(mostCaloricByTypeWithoutOptional);

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 분할");

		Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian));
		System.out.println(partitionedMenu.toString());

		Map<Boolean, Map<Type, List<Dish>>> vegetarianDishesByType = menu.stream()
				.collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
		System.out.println(vegetarianDishesByType.toString());

		Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream().collect(partitioningBy(Dish::isVegetarian,
				collectingAndThen(maxBy(comparingInt(Dish::getCalories)),
						Optional::get)));
		System.out.println(mostCaloricPartitionedByVegetarian.toString());

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 소수와 비소수로 비교하기");

		System.out.println(partitionPrimes(20).toString());

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- Collector 인터페이스");

		List<Dish> dishes = menu.stream().collect(new ToListCollector<Dish>());
		System.out.println(dishes.toString());

		List<Dish> dishesNonCollector = menu.stream().collect(
				ArrayList::new,
				List::add,
				List::addAll
		);
		System.out.println(dishesNonCollector);

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 커스텀 컬렉터를 구현해서 성능 개선하기");

		System.out.println("Fastest execution done in " + partitionPrimesCollectorHarness() + " msecs");
		System.out.println("Fastest execution done in " + partitionPrimesWithCustomCollectorHarness() + " msecs");

		System.out.println("----------------------------------------------------------------------");
	}

	public static boolean isPrime(int candidate) {
		int candidateRoot = (int) Math.sqrt((double) candidate);
		return IntStream.rangeClosed(2, candidateRoot)
				.noneMatch(i -> candidate % i == 0);
	}

	public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
		return IntStream.rangeClosed(2, n).boxed()
				.collect(
						partitioningBy(candidate -> isPrime(candidate))
				);
	}

	public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
		return IntStream.rangeClosed(2, n).boxed()
				.collect(new PrimeNumbersCollector());
	}

	public static Long partitionPrimesCollectorHarness() {
		long fastest = Long.MAX_VALUE;
		for (int i = 0; i < 10; i++) {
			long start = System.nanoTime();
			partitionPrimes(1_000_000);
			long duration = (System.nanoTime() - start) / 1_000_000;
			if (duration < fastest) fastest = duration;
		}
		return fastest;
	}

	public static Long partitionPrimesWithCustomCollectorHarness() {
		long fastest = Long.MAX_VALUE;
		for (int i = 0; i < 10; i++) {
			long start = System.nanoTime();
			partitionPrimesWithCustomCollector(1_000_000);
			long duration = (System.nanoTime() - start) / 1_000_000;
			if (duration < fastest) fastest = duration;
		}
		return fastest;
	}

}
