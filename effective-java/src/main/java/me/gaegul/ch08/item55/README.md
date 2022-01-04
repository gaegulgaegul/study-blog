# 아이템 55. 옵셔널 반환은 신중히 하라

### 자바 8 이전

- 메서드가 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 선택지
    - 예외를 던진다.
        - 진짜 예외인 상황에서만 사용해야 한다.
    - null을 반환한다.
        - 별도의 null 처리 코드를 추가해야 한다.
        - null 처리를 무시하고 반환된 null을 어딘가에 저장해두면 언젠가 `NullPointerException`이 발생할 수 있다.
- 컬렉션에서 최댓값을 구한다. - 예외/null

    ```java
    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty())
            throw new IllegalArgumentException("빈 컬렉션");
        
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
    ```


### Optional<T>

- null이 아닌 T 타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.
- 옵셔널은 원소를 최대 1개 가질 수 있는 **불변 컬렉션**이다.
- 옵셔널은 반환하는 메서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작다.
- 컬렉션에서 최댓값을 구한다. - Optional

    ```java
    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty())
            return Optional.empty();
    
        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return Optional.of(result);
    }
    ```

    - `Optional.of(value)`에 null을 넣으면 `NullPointerException` 발생
    - 옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말자
- Stream에서 Optional 반환

    ```java
    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        return c.stream().max(Comparator.naturalOrder());
    }
    ```


### Optional 반환

- orElse
    - 기본값을 지정한다.

    ```java
    String lastWordInLexicon = max(words).orElse("단어 없음...");
    ```

- orElseThrow
    - 원하는 예외를 던진다.

    ```java
    Toy toy = max(toys).orElseThrow(TemperTanturmException::new);
    ```

- get
    - 항상 값이 채워져 있다고 가정한다.

    ```java
    Element lastNobleGas = max(Elements.NOBLE_GASES).get();
    ```

- orElseGet
    - 값이 처음 필요할 때 Supplier<T>를 사용해 생성하므로 초기 설정 비용을 낮출 수 있다.
- isPresent
    - 옵셔널 값이 있으면 true, 없으면 false 반환
    - 원하는 작업을 수행할 수 있지만 신중히 사용해야 한다.

    ```java
    Optional<ProcessHandle> parentProcess = ph.parent();
    System.out.println("부모 PID: " + (parentProcess.isPresent() ?
        String.valueOf(parentProcess.get().pid() : "N/A"));
    
    System.out.println("부모 PID: " + 
        ph.parent.map(h -> String.valueOf(h.pid())).orElse("N/A"));
    ```


### 옵셔널을 활용하는 방법

- 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안 된다.
    - Optional<List<T>>을 반환하기 보다 빈 List를 반환하자
- 메서드 반환 타입을 Optional<T>로 선언해야 하는 경우
    - 결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T>를 반환한다.
    - Optional을 새로 할당하고 초기화 해야 하는 객체이고 반환된 Optional에서 값을 꺼낼 때 메서드를 거처야 한다.
    - 성능이 중요하다면 다른 방법을 사용해야 한다.
- 기본 타입 전용 Optional → OptionalInt, OptionalLong, OptionalDouble
- 옵셔널을 컬렉션의 키, 값, 원소나 배열의 원소로 사용하는 게 적절한 상황은 거의 없다.