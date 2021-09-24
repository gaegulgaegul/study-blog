# 아이템 37. ordinal 인덱싱 대신 EnumMap을 사용하라

```java
public class Plant {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
```

### ordinal 값을 배열의 인덱스로 사용

```java
List<Plant> garden = List.of(
        new Plant("1년생 식물", LifeCycle.ANNUAL),
        new Plant("다년생 식물", LifeCycle.PERENNIAL),
        new Plant("2년생 식물", LifeCycle.BIENNIAL));

// 배열은 제네릭과 호환되지 않는다.
Set<Plant>[] plantByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];
for (int i = 0; i < plantByLifeCycle.length; i++) {
    plantByLifeCycle[i] = new HashSet<>();
}

for (Plant p : garden)
    plantByLifeCycle[p.lifeCycle.ordinal()].add(p);

// 결과 출력
for (int i = 0; i < plantByLifeCycle.length; i++) {
    // 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다.
    System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantByLifeCycle[i]);
}

// console
**ANNUAL: [1년생 식물]
PERENNIAL: [다년생 식물]
BIENNIAL: [2년생 식물]**
```

- 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 한다.
- 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다.
    - 가장 중요한 문제는 정확한 정숫값을 사용한다는 것을 개발자가 보증해야 한다.
    - 정수는 열거 타입과 달리 타입 안전하지 않다. 잘못된 값을 사용하면 잘못된 동작을 수행한다.

### EnumMap을 사용해 데이터와 열거 타입 매핑

```java
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
    plantsByLifeCycle.put(lc, new HashSet<>());
}

for (Plant p : garden) {
    plantsByLifeCycle.get(p.lifeCycle).add(p);
}

System.out.println(plantsByLifeCycle);

// console
{ANNUAL=[1년생 식물], PERENNIAL=[다년생 식물], BIENNIAL=[2년생 식물]}
```

- 맵이 키인 열거 타입이 열거 타입 자체로 출력용 문자열을 제공 직접 레이블을 달지 않아도 된다.
- 배열 인덱스를 계산하는 과정에서 오류가 날 가능성이 없어진다.

### EnumMap

- 열거 타입 상수를 값으로 매핑한다.
- EnumMap 내부에서 배열을 사용한다. 내부 구현 방식을 안으로 숨겨서 Map의 타입 안전성과 배열의 성능을 모두 가진다.
- EnumMap의 생성자 가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로 런타임 제네릭 타입 정보를 제공한다.

### EnumMap 예제

- 스트림을 사용하는 예제
    - EnumMap을 사용하지 않고 스트림을 사용하는 코드

        ```java
        System.out.println(garden.stream().collect(groupingBy(p -> p.lifeCycle)));

        // console
        {BIENNIAL=[2년생 식물], PERENNIAL=[다년생 식물], ANNUAL=[1년생 식물]}
        ```

        - EnumMap을 사용하지 않고 고유한 맵 구현체 사용하여 EnumMap을 사용해 얻은 공간과 성능 이점이 사라진다.
    - 스트림과 EnumMap을 이용해 데이터와 열거 타입을 매핑

        ```java
        System.out.println(garden.stream()
                    .collect(groupingBy(p -> p.lifeCycle,
                            () -> new EnumMap<>(Plant.LifeCycle.class), toSet())));

        // console
        {ANNUAL=[1년생 식물], PERENNIAL=[다년생 식물], BIENNIAL=[2년생 식물]}
        ```

        - 매개변수 3개의 Collectors.groupingBy 메서드 mapFactory 매개변수에 맵 구현체 명시
        - EnumMap은 식물의 생명주기당 하나씩 중첩 맵을 만든다.
        - 스트림은 해당 생명주기에 속하는 식물이 있을 때만 만든다.

- 두 열거 타입 값을 매핑
    - 배열들의 배열의 인덱스의 ordina()을 사용

        ```java
        public enum Phase {
            SOLID, LIQUID, GAS;

            public enum Transaction {
                MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

                // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
                private static final Transaction[][] TRANSACTIONS = {
                        { null, MELT, SUBLIME },
                        { FREEZE, null, BOIL},
                        { DEPOSIT, CONDENSE, null }
                };

                // 한 상태에서 다른 상태로의 전이를 반환한다.
                public static Transaction from(Phase from, Phase to) {
                    return TRANSACTIONS[from.ordinal()][to.ordinal()];
                }
            }
        }
        ```

        - 컴파일러가 ordinal과 배열 인덱스의 관계를 알 수 없다.
        - Phase나 Phase.Transaction 열거 타입을 수정하면서 TRANSACTIONS를 수정하지 않거나 잘못 수정하면 런타임 오류가 발생한다.
    - 중첩 EnumMap으로 데이터와 열거 타입 쌍을 연결

        ```java
        public enum Phase {
            SOLID, LIQUID, GAS;

            public enum Transaction {
                MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS),
                CONDENSE(GAS, LIQUID), SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);
                
                private final Phase from;
                private final Phase to;

                Transaction(Phase from, Phase to) {
                    this.from = from;
                    this.to = to;
                }
                
                // 상전이 맵을 초기화한다.
                private static final Map<Phase, Map<Phase, Transaction>> m = Stream.of(values())
                        .collect(groupingBy(t -> t.from,
                                () -> new EnumMap<>(Phase.class),
                                toMap(t -> t.to,
                                        t -> t,
                                        (x, y) -> y,
                                        () -> new EnumMap<>(Phase.class))));
                
                public static Transaction from(Phase from, Phase to) {
                    return m.get(from).get(to);
                }
            }
        }
        ```

        - 상전이 맵을 초기화 하는 코드
            - `Map<Phase, Map<Phase, Transaction>>`
                - "이전 상태에서 '이후 상태에서 전이로의 맵'에 대응시키는 맵"
            - 첫번째 수집기 - `groupingBy`
                - 전이를 이전 상태를 기준으로 묶는다.
            - 두번째 수집기 - `toMap`
                - 이후 상태에서 전이에 대응시키는 EnumMap 생성
                - 병합 함수인 `(x, y) -> y`는 선언만 하고 쓰이진 않는다.
    - 새로운 상태 추가 - PLASMA

        ```java
        public enum Phase {
            SOLID, LIQUID, GAS, PLASMA;

            public enum Transaction {
                MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS),
                CONDENSE(GAS, LIQUID), SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
                IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);

                ... 생략
            }
        }
        ```