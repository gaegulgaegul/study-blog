# 아이템 46. 스트림에서는 부작용 없는 함수를 사용하라

### 스트림의 패러다임

- 계산을 일련의 변환으로 재구성하는 부분
- 변환 단계는 가능한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다.
    - 순수 함수
        - 오직 입력만이 결과에 영향을 주는 함수
        - 다른 가변 상태를 참조하지 않고 함수 스스로도 다른 상태를 변경하지 않는다.

### 스트림의 패러다임을 이해하지 못한 채 스트림 API를 사용하는 경우

```java
Map<String, Long> freq = new HashMap<>();
try (Stream<String> words = new Scanner(file).tokens()) {
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
    });
}
```

- 종단 연산인 forEach에서 작업이 수행되는데 이때 외부 상태를 수정하는 람다를 실행하면서 문제가 생긴다.
    - forEach는 스트림이 수행한 연산 결과를 보여주는 일만 하는 것이 좋다.


### 스트림을 제대로 활용해 빈도를 초기화

```java
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words.collect(groupingBy(String::toLowerCase, counting()));
}
```

### 수집기(collector)

- java.util.stream.Collectors
    - 39개의 메서드, 5개의 타입 매개변수를 가진다.
- 축소 전략을 캡슐화한 블랙박스 객체
    - 축소는 스트림의 원소들을 객체 하나에 취합한다는 뜻
- 스트림의 원소를 컬렉션으로 쉽게 모을 수 있는 수집기
    - `toList()`
    - `toSet()`
    - `toCollection(collectionFactory)`
- 가장 흔한 단어 10개를 뽑아내는 파이프라인

    ```java
    List<String> topTen = freq.keySet().stream()
                    .sorted(Comparator.comparing(freq::get).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
    ```

    - `Comparator.comparing(freq::get).reversed()`
        - `Comparator.comparing`
            - 키 추출 함수를 받는 비교자 생성 메서드
        - `freq::get`
            - 한정적 메서드 참조
            - 키 추출 함수
            - 입력받은 단어를 빈도표에서 찾아 빈도를 반환한다.
        - `reversed`
            - 가장 흔한 단어가 상위로 오도록 역순으로 정렬한다.
- 문자열을 열거 타입 상수에 매핑

    ```java
    private static final Map<String, Operation> stringToEnum = 
            Stream.of(Operation.values())
                .collect(Collectors.toMap(Object::toString, e -> e));
    ```

    - `toMap`
        - 스트림의 각 원소가 고유한 키에 매핑되어 있을 때 적합하다.
        - 스트림 원소 다수가 같은 키를 사용한다면 `IllegalStateException`
        - 병합(merge) 함수
            - BinaryOperator<U>, U는 해당 맵의 값 타입
            - 같은 키를 공유하는 값들은  기존 값과 합쳐진다.
- 인수 3개를 받는 toMap

    ```java
    Map<Artist, Album> topHits = albums.stream()
                    .collect(toMap(Album::artist,
                             Function.identity(),
                             maxBy(comparing(Album::sales))));
    ```

    - `maxBy` - 비교자
        - Comparator<T>를 입력받아 BinaryOperator<T>를 반환한다.
    - `comparing` - 비교자 생성 메서드
    - `Album::artist` - 키 추출 함수
- groupingBy
    - 분류 함수를 입력받고 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다.
    - 분류 함수는 입력 받은 원소가 속하는 카테고리를 반환, 카테고리는 원소의 맵 키로 쓰인다.
- groupingBy 다중 정의
    - 첫번째 버전
        - 분류 함수 하나를 인수로 받아 맵을 반환

            ```java
            words.collect(groupingBy(word -> alphabetize(word)));
            ```

    - 두번째 버전
        - 반환되는 수집기가 리스트 외의 값을 갖는 맵을 생성하면 분류 함수와 함께 다운 스트림 수집기도 명시
            - 다운 스트림으로 `toSet`이 가장 간단하다.
            - `toCollection(collectionFactory)`
                - 원하는 컬렉션 타입을 선택할 수 있다.
            - `counting()`
                - 카테고리에 해당하는 원소의 개수를 알 수 있다.

                ```java
                Map<String, Long> freq = words
                        .collection(groupingBy(String::toLowerCase, counting()));
                ```

    - 세번째 버전
        - 맵 팩터리도 지정할 수 있다.
            - 맵과 그 안에 담긴 컬렉션의 타입을 모두 지정할 수 있다.

### Collectors.joining

- CharSequence 인스턴스의 스트림에서만 사용할 수 있다.
- 매개변수가 없는 joining은 원소들을 연결하는 수집기를 반환하고 접두, 구분, 접미 문자를 지정하여 연결할 수 있다.