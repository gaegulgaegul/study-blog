# 5. 스트림 활용

- 필터링
    - filter → 프레디케이트로 필터링

        ```java
        List<Dish> vegetarianMenu = menu.stream()
                                        .filter(Dish::isVegetarian)
                                        .collect(toList());
        ```

    - 고유 요소 필터링
        - distinct(고유 여부는 스트림에서 만든 객체의 hashCode, equals로 결정된다.)

        ```java
        List<Integer> numbers = Arrays.asList(1,2,1,3,3,2,4);
        numbers.stream()
               .filter(i -> i % 2 == 0)
               .distinct()
               .forEach(System.out::println);
        ```

- 스트림 슬라이싱
    - 프레디케이트를 이용한 슬라이싱
        - takeWhile
            - 무한 스트림을 포함한 모든 스트림에 프레디케이트를 적용해 스트림을 슬라이스 할 수 있다.
            - 처음으로 거짓이 되는 지점까지 요소를 사용한다.
            - `filter`는 거짓이 되는 지점은 넘어간다.

            ```java
            List<Dish> slicedMenu1 = specialMenu.stream()
                                                .takeWhile(dish -> dish.getCalories() < 320)
                                                .collect(toList());
            ```

        - dropWhile
            - 처음으로 거짓이 되는 지점까지 발견된 요소를 버린다.

            ```java
            List<Dish> slicedMenu2 = specialMenu.stream()
                                                .dropWhile(dish -> dish.getCalories() < 320)
                                                .collect(toList());
            ```

    - 스트림 축소
        - limit → 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환한다.

        ```java
        List<Dish> dishes = specialMenu.stream()
                                       .filter(dish -> dish.getCalories() > 300)
                                       .limit(3)
                                       .collect(toList());
        ```

    - 요소 건너뛰기
        - skip → 처음 n개 요소를 제외한 스트림을 반환한다.

        ```java
        List<Dish> dishes = specialMenu.stream()
                                       .filter(dish -> dish.getCalories() > 300)
                                       .skip(2)
                                       .collect(toList());
        ```

- 매핑
    - 스트림의 각 요소에 함수 적용하기
        - map → 인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다.

        ```java
        List<String> dishNames = menu.stream()
                                     .map(Dish::getName)
                                     .collect(toList());
        ```

    - 스트림 평면화
        - map과 Arrays.stream 활용

            ```java
            String[] arrayOfWords = {"Goodbye", "World"};
            Stream<String> streamOfWords = Arrays.stream(arrayOfWords);

            words.stream()
                 .map(word -> word.split(""))
                 .map(Arrays::stream)
                 .distinct()
                 .collect(toList())
            ```

            - 결과적으로 List<Stream<String>> 스트림 리스트가 만들어지게 된다.
        - flatMap 사용
            - 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑한다.
            - 스트림의 각 값을 다른 스트림으로 만든 다음에 모든 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.

            ```java
            List<Stirng> uniqueCharacters = words.stream()
                                                 .map(word -> word.split(""))
                                                 .flatMap(Arrays::stream)
                                                 .distinct()
                                                 .collect(toList());
            ```

- 검색과 매칭
    - 쇼트서킷
        - 전체 스트림을 처리하지 않고 결과를 반환하는 상황
        - 원하는 요소를 찾았다면 즉시 결과를 반환한다.
        - allMatch, noneMatch, findFirst, findAny 등
    - 프레디케이트가 적어도 한 요소와 일치하는지 확인
        - anyMatch → 스트림에서 적어도 한 요소와 일치하는지 확인

        ```java
        if (menu.stream().anyMatch(Dish::isVegetarian)) {
        	System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }
        ```

    - 프레디케이트가 모든 요소와 일치하는지 확인
        - allMatch → 스트림에서 모든 요소와 일치하는지 확인

            ```java
            boolean isHealthy = menu.stream()
                                    .allMatch(dish -> dish.getCalories() < 1000);
            ```

        - noneMatch → 스트림에서 모든 요소와 일치하지 않는지 확인

            ```java
            boolean isHealthy = menu.stream()
                                    .noneMatch(d -> d.getCalories() >= 1000);
            ```

    - 요소 검색
        - findAny → 스트림에서 임의의 요소를 반환한다.

        ```java
        Optional<Dish> dish = menu.stream()
                                  .filter(Dish::isVegetarian)
                                  .findAny();
        ```

    - 첫번째 요소 찾기
        - findFirst → 스트림에서 첫번째 요소를 반환한다.
        - 병렬 스트림에서 첫번째 요소를 찾기 어렵다. → 요소 순서 반환에 상관이 없다면 `findAny` 사용

        ```java
        List<Integer> someNumbers = Arrays.asList(1,2,3,4,5);
        Optional<Integer> firstSquareDivisibleByThere = somNumbers.stream()
                                                                  .map(n -> n * n)
                                                                  .filter(n -> n % 3 == 0)
                                                                  .findFirst();
        ```

- 리듀싱
    - 요소의 합
        - reduce
            - 스트림이 하나의 값으로 줄어들 때까지 각 요소를 반복해 조합한다.
            - 초깃값을 선언할 수 있다.(초깃값이 없다면 해당 스트림의 결과는 Optional<T>을 반환한다.)

        ```java
        List<Integer> numbers = Arrays.asList(4,5,3,9);
        int sum = numbers.stream().reduce(0, (a, b) -> a + b);

        int sum = numbers.stream().reduce(0, Integer::sum);
        ```

    - 최댓값과 최솟값
        - max → 스트림의 최댓값을 반환한다.

        ```java
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        ```

        - min → 스트림의 최솟값을 반환한다.

        ```java
        Optional<Integer> max = numbers.stream().reduce(Integer::min);
        ```

- 숫자형 스트림
    - 리듀스에서 Integer를 기본형으로 언박싱하고 sum을 한다.
    - 스트림 API 숫자 스트림을 효율적으로 처리할 수 있는 기본형 특화 스트림을 제공한다.

        ```java
        int clories = menu.stream().map(Dish::getCalories).reduce(0, Integer::sum);

        int clories = menu.stream().map(Dish::getCalories).sum();
        ```

    - 숫자 스트림으로 매핑
        - mapToInt, mapToDouble, mapToLong → map과 같은 기능을 수행하지만, Stream<T> 대신 특화된 스트림(IntStream, DoubleStream, LongStream)을 반환한다.

        ```java
        int clories = menu.stream().mapToInt(Dish::getCalories).sum();
        ```

- 스트림 만들기
    - 컬렉션 이외의 값, 배열, 파일, iterate/generate 메서드로도 스트림을 만들 수 있다.