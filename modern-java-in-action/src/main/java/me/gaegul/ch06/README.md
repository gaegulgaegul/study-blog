# 6. 스트림으로 데이터 수집

- 컬렉터란 무엇인가?
    - 고급 리듀싱 기능을 수행하는 컬렉터
        - collect로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다.
        - collect를 호출하면 내부에 리듀싱 연산이 수행된다.
        - Collector 인터페이스 메서드를 어떻게 구현하는지에 따라 스트림에서 어떤 리듀싱 연산을 수행할지 결정된다.
    - 미리 정의된 컬렉터
        - Collectors 유틸리티 클래스
            - 메서드의 기능
                - 스트림 요소를 하나의 값으로 리듀스하고 요약
                - 요소 그룹화
                - 요소 분할
- 리듀싱과 요약
    - 스트림값에서 최댓값과 최솟값 검색
        - maxBy, minBy
        - 스트림에 있는 객체의 숫자 필드의 합계나 평균 등 반환하는 연산을 요약 연산이라 한다.

        ```java
        Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));
        ```

    - 요약 연산
        - summingInt → 객체를 int로 매핑하는 컬렉터를 반환한다.
        - summingDouble, summingLong 메서드도 제공하고 평균값, 요소 수 등 요약 연산 기능을 제공한다.

        ```java
        Optional<Dish> mostCalorieDish = menu.stream().collect(summingInt(Dish::getCalories));
        ```

        - summarizingInt
            - 하나의 요약 연산으로 요소 수, 합계, 평균, 최댓값, 최솟값 등을 계산한다.
            - 리턴 타입은 SummaryStatistics

        ```java
        IntSummaryStatistics menuStatistics = menu.stream.collec(summarizingInt(Dish::getCalories));

        IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}
        ```

    - 문자열 연결
        - joining → 스트림 각 객체에 toString 메서드를 호출하여 문자열을 하나로 연결해서 반환한다.

        ```java
        String shortMenu = menu.stream().map(Dish::getName).collect(joining());

        String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
        ```

    - 범용 리듀싱 요약 연산
        - reducing
            - 인수를 유연하게 사용할 수 있다.
            - 첫번째 인수 → 리듀싱 연산의 시작값 또는 스트림에 인수가 없을 때는 반환값
            - 두번째 인수 → 변환 함수
            - 세번째 인수 → BinaryOperator<T>(BiFunction<T, T, T>) 연산 기능

        ```java
        int totalCalories = menu.stream().collect(reducing(o, Dish::getCalories, (i, j) -> i + j));

        Optional<Dish> mostCalorieDish = menu.stream().collect(reducing((d1, d2) -> d2.getCalories() > d2.getCalories() ? d1 : d2));
        ```

- 그룹화
    - groupingBy → 스트림의 그룹화 연산

    ```java
    // 메뉴의 요리 종류로 그룹화
    Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));

    {FISH=[prawns, salmon], OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork, beef, chicken]}

    // 메뉴의 칼로리로 그룹화
    public enum CaloricLevel {
    	DIET, NORMAL, FAT
    }
    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
            groupingBy(dish -> {
                if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                else return CaloricLevel.FAT;
            }));
    ```

    - 그룹화된 요소 조작
        - 500 칼로리가 넘는 요리만 필터하여 그룹화
            - FISH 종류 요리가 없으므로 결과 맵에서 해당 키가 사라진다.

        ```java
        Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream()
                                                             .filter(dish -> dish.getCalories() > 500)
                                                             .collect(groupingBy(Dish::getType));

        {OTHER=[french fries, pizza], MEAT=[pork, beef]}
        ```

        - Collector 안으로 필터 프레디케이트 추가
            - filtering → 프레디케이트를 인수로 받아 각 그룹의 요소와 필터링 된 요소를 재그룹화 한다.

            ```java
            Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream()
                                                                 .collect(groupingBy(Dish::getType,
                                                                          filtering(dish -> dish.getCalories() > 500, toList())));

            {OTHER=[french fries, pizza], MEAT=[pork, beef], FISH=[]}
            ```

        - 그룹의 각 목록을 변환
            - mapping → 각 그룹의 요소를 인수로 받은 Function<T, R>을 통해 변환한다.

            ```java
            Map<Dish.Type, List<Dish>> dishNamesByType = menu.stream().collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
            ```

            - flatMapping → 각 그룹의 스트림을 평면화한다.
                - list가 아니라 set을 통해 중복을 제거했다.

            ```java
            Map<Dish.Type, List<Dish>> dishNamesByType = menu.stream()
                                                             .collect(groupingBy(Dish::getType,
                                                                      flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
            ```

    - 다수준 그룹화
        - 두번째 기준을 정의하는 내부 groupingBy를 전달해서 두 수준으로 스트림을 항목화한다.

        ```java
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream()
                  .collect(
                      groupingBy(Dish::getType,
                          groupingBy(dish -> {
                              if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                              else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                              else return CaloricLevel.FAT;
                          })
                      )
                  );

        {
          MEAT={DEIT[chicken], NORMAL=[beef], FAT=[pork]},
          FISH={DIET=[prawns], NORMAL=[salmon]},
          OTHER={DIET=[rice, season fruit], NORMAL=[french fries, pizza]}
        }
        ```

    - 서브그룹으로 데이터 수집
        - groupingBy(f) → groupingBy(f, toList())
        - groupingBy로 넘겨주는 컬렉터의 형식은 제한이 없다.

        ```java
        Map<Dish.Type, Long> typesCount = menu.stream().collect(groupingBy(Dish::getType, counting()));

        {MEAT=3, FISH=2, OTHER=4}

        Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream()
                                                               .collect(groupingBy(Dish::getType,
                                                                                   maxBy(comparingInt(Dish::getCalories))));

        {FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}
        ```

        - collectingAnsThen → 반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 하며 collect의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑한다.

        ```java
        Map<Dish.Type, Dish> mostCaloricByType = menu.stream()
                                                     .collect(groupingBy(Dish::getType,
                                                              collectingAndThen(
                                                                  maxBy(comparingInt(Dish::getCalories)),
                                                                  Optional::get)));

        {FISH=salmon, OTHER=pizza, MEAT=pork}
        ```

    - groupingBy와 함께 사용하는 다른 컬렉터 예제
        - Collectors의 컬렉션 변환 함수 또는 toCollection(컬렉션 타입)을 통해 결과를 전달할 수 있다.

        ```java
        Map<Dish.Type, Integer> totalCaloriesByType = menu.stream().collect(groupingBy(Dish::Type, summingInt(Dish::getCalories)));

        Map<Dish.Type, Set<CaloricLevel>> caloricLevelByType = menu.stream().collect(
            groupingBy(Dish::getType, mapping(dish -> {
                if (dish.getCalories() <= 400) return CaloricLevel.DEIT;
                else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                else return CaloricLevel.FAT;
            },
            toSet())));

        {OTHER=[DEIT, NORMAL], MEAT=[DEIT, NORMAL, FAT], FISH=[DEIT, NORMAL]}

        Map<Dish.Type, Set<CaloricLevel>> caloricLevelByType = menu.stream().collect(
            groupingBy(Dish::getType, mapping(dish -> {
                if (dish.getCalories() <= 400) return CaloricLevel.DEIT;
                else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                else return CaloricLevel.FAT;
            },
            toCollection(HashMap::new))));
        ```

- 분할
    - 프레디케이트를 분류 함수로 사용하는 그룹화 기능
    - 결과적으로 최대 두개의 그룹으로 분류된다.
    - 분할의 장점
        - 분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다.
        - partitioningBy → 특정 조건으로 참, 거짓 두 타입으로 그룹화한다.

        ```java
        Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream().collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));

        {false={FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, true=[OTHER=[french fries, rice, season fruit, pizza]]}
        ```

    - 숫자를 소수와 비소수로 분할하기
        - 정수 n을 인수로 받아서 2에서 n까지의 자연수를 소수와 비소수로 나누는 프로그램
        - isPrime 메서드를 프레디케이트로 이용하고 partitioningBy 컬렉터로 리듀스해서 숫자를 소수와 비소수로 분류할 수 있다.

        ```java
        public boolean isPrime(int candidate) {
        	int candidateRoot = (int) Math.sqrt((double) candidate);
        	return IntStream.rangeClosed(2, candidate).noneMatch(i -> candidate % i == 0);
        }

        public Map<Boolean, List<Integer>> partitionPrimes(int n) {
        	return IntStream.rangeClosed(2, candidate)
                          .boxed()
                          .collect(partitioningBy(candidate -> isPrime(candidate)));
        }
        ```

- Collector 인터페이스

    ```java
    public interface Collector<T, A, R> {
    	Supplier<A> supplier();
    	BiConsumer<A, T> accumulator();
    	Function<A, R> finisher();
    	BinaryOperator<A> combiner();
    	Set<Characteristics> characteristics();
    }
    ```

    - T는 수집될 스트림 항목의 제네릭 형식
    - A는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식
    - R은 수집 연산 결과 객체의 형식(대게 컬렉션 형식)
    - Collector 인터페이스의 메서드 살펴보기
        - supplier
            - 새로운 컨테이너 결과 만들기
            - 빈 결과로 이루어진 Supplier를 반환한다.

            ```java
            public Supplier<List<T>> supplier() {
            	return () -> new ArrayList<T>();
            }

            public Supplier<List<T>> supplier() {
            	return ArrayList::new;
            }
            ```

        - accumulator
            - 결과 컨테이너에 요소 추가하기
            - 리듀싱 연산을 수행하는 함수를 반환한다.

            ```java
            public BiConsumer<List<T>, T> accumulator() {
            	return (list, item) -> list.add(item);
            }

            public BiConsumer<List<T>, T> accumulator() {
            	return List::add;
            }
            ```

        - finisher
            - 최종 변환값을 결과 컨테이너로 적용하기
            - 스트림 탐색을 끈내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다.

            ```java
            public Function<List<T>, List<T>> finisher() {
            	return Function.identity();
            }
            ```

        - combiner
            - 두 결과 컨테이너 병합
            - 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다.

            ```java
            public BinaryOperator<List<T>> combiner() {
            	return (list1, list2) -> {
            		list1.addAll(list2)
            		return list1;
            	}
            }
            ```

        - characteristics
            - 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환한다.
            - Characteristics는 스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀스하면 어떤 최적화를 선택해야 할지 힌트를 제공
            - Characteristics 열거형
                - UNORDERED
                    - 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
                - CONCURRENT
                    - 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있다.
                    - UNORDERED와 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않은 상황에서만 병렬 리듀싱을 수행한다.
                - IDENTITY_FINISH
                    - finisher 메서드가 반환하는 함수인 identity를 생략한다.
                    - 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용한다.
    - 커스텀 컬렉터를 구현해서 성능 개선하기
        - 소수로만 나누기
            - isPrime 메서드

            ```java
            public static boolean isPrime(List<Integer> primes, int candidate) {
            	int candidateRoot = (int) Math.sqrt((double) candidate);
            	return primes.stream()
                           .takeWhile(i -> i <= candidateRoot)
                           .noneMatch(i -> candidate % i == 0);
            }
            ```

            - 1단계 : Collector 클래스 시그니처 정의

            ```java
            public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>, Map<Boolean, List<Integer>>>
            ```

            - 2단계 : 리듀싱 연산 구현
                - supplier → 누적자를 만드는 함수를 반환한다.
                - accumulator → 지금까지 발견한 소수를 포함하는 누적자에 접근한다.

            ```java
            public Supplier<Map<Boolean, List<Integer>>> supplier() {
            	return () -> new HashMap<Boolean, List<Integer>>() {{
            		put(true, new ArrayList<Integer>());
            		put(false, new ArrayList<Integer>());
            	}}
            }

            public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
            	return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            		acc.get(isPrime(acc.get(true), candidate)).add(candidate);
            	}
            }
            ```

            - 3단계 : 병렬 실행할 수 있는 컬렉터 만들기
                - 병렬 수집 과정에서 두 부분 누적자를 합칠 수 있는 메서드를 만든다.
                - combiner 메서드는 호출될 일이 없으므로 빈 구현으로 남겨두거나 UnSupportedOperationException을 던지도록 한다.

            ```java
            public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
            	return (Map<Boolean, List<Integer> map1, Map<Boolean, List<Integer> map2) -> {
            		map1.get(true).addAll(map2.get(true));
            		map1.get(false).addAll(map2.get(false));
            		return map1;
            	};
            }
            ```

            - 4단계 : finisher 메서드와 컬렉터의 characteristics 메서드
                - finisher → accumulator의 형식은 컬렉터 결과 형식과 같으므로 변환 과정이 필요 없다.
                - characteristics → 커스텀 컬렉터는 IDENTITY_FINISH만 포함하여 구현한다.

            ```java
            public Function<Map<Boolean, List<Integer>, Map<Boolean, List<Integer>>> finisher() {
            	return Function.identity();
            }

            public Set<Characteristics> characteristics() {
            	return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
            }
            ```

            - 커스텀 컬렉터 사용

            ```java
            public Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
            	return IntStream.rangeClosed(2, candidate)
                              .boxed()
                              .collect(new PrimeNumbersCollector());
            }
            ```