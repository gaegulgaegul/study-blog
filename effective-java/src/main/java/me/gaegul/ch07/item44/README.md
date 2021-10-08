# 아이템 44. 표준 함수형 인터페이스를 사용하라

### 자바가 API를 작성하는 모범 사례

- 이전 버전에서 상위 클래스를 재정의해 원하는 동작을 구현하는 탬플릿 메서드 패턴 사용
- 현대적인 해법으로 함수를 객체로 받는 정적 팩터리나 생성자를 제공
→ **함수형 매개변수 타입을 올바르게 선택해야 한다.**

### LinkedHashMap

- removeEldestEntry를 재정의해 캐시로 사용할 수 있다.
    - put 메서드는 위 메서드를 호출해 true가 되면 가장 오래된 원소를 제거한다.
    - 가장 최근 원소 100개를 유지하도록 재정의
        
        ```java
        protected boolean removeEldestEntry(Map.Entry<K,Y> eldest) {
            return size() > 100;
        }
        ```
        
        - removeEldestEntry는 size()를 호출해 맵 안의 원소 수를 알아내는데, removeEldestEntry가 인스턴스 메서드라서 가능한 방식
    - 함수형 인터페이스로 정의
        - 생성자에 넘기는 함수 객체는 이 맵의 인스턴스 메서드가 아니다. 팩터리나 생성자를 호출할 때는 맵의 인스턴스가 존재하지 않기 때문이다. 따라서 맵은 자기 자신도 함수 객체에 넘겨야 한다.
        
        ```java
        @FunctionalInterface
        interface EldestEntryRemovalFunction<K,V> {
            boolean remove(Map<K,V> map, Map.Entry<K,V> eldest);
        }
        ```
        
        - 필요한 용도에 맞는 게 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 활용하라
        → `BiPredicate<Map<K,V>, Map.Entry<K,V>>`

### 기본 **함수형 인터페이스**

- UnaryOperator<T>
    - 함수 시그니처 → `T apply(T t)`
    - 예시 → `String::toLowerCase`
- BinaryOperator<T>
    - 함수 시그니처 → `T apply(T t1, T t2)`
    - 예시 → `BigInteger::add`
- Predicate<T>
    - 함수 시그니처 → `boolean test(T t)`
    - 예시 → `Collection::isEmpty`
- Function<T,R>
    - 함수 시그니처 → `R apply(T t)`
    - 예시 → `Arrays::asList`
- Supplier<T>
    - 함수 시그니처 → `T get()`
    - 예시 → `Instant::now`
- Consumer<T>
    - 함수 시그니처 → `void accept(T t)`
    - 예시 → `System.out::println`

### 변형된 기본 인터페이스

- int, Long, double 3가지 기본 인터페이스 변형→ LongBinaryOperator, IntPredicate  등
    - Function의 변형만 매개변수화 가능 → LongFunction<int[]>은 long 인수를 받아 int[]을 반환
- Function 인터페이스에는 9개의 기본 타입을 반환하는 변형
    - 입력과 결과 타입이 모두 기본 타입이면 접두어로 *SrcToResult*를 사용
    → long을 받아 int를 반환하면 LongToIntFunction
    - int, long, double인 변형은 접두어로 *ToResult*를 사용
    → int[] 인수를 받아 long을 반환하면 ToLongFunction<int[]>
- 인수를 2개씩 받는 변형
    - BiPredicate<T,U>, BiFunction<T,U,R>, BiConsumer<T,U>
    - BiFunction
        - ToIntBiFunction<T,U>, ToLongBiFunction<T,U>, ToDoubleBiFunction<T,U>
    - Consumer
        - ObjDoubleConsumer<T>, ObjIntConsumer<T>, ObjLongConsumer<T>
- BooleanSupplier
    - boolean을 반환하는 Supplier 변형

### 표준 함수형 인터페이스 특징

- 표준 함수형 인터페이스 대부분 기본 타입만 지원
- 기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하면 안된다.

### Comparator<T> 인터페이스

- 구조적으로 ToIntBiFunction<T,U>와 동일
- Comparator을 사용하는 이유
    - 이름이 용도를 잘 설명한다.
    - 구현하는 쪽에서 반드시 지켜야 할 규약을 담는다.
    - 비교자들을 변환하고 조립하는 디폴트 메서드가 많다.
- 함수형 인터페이스에서 처리가 안되고 Comparator을 사용하는 이유와 같으면 전용 함수형 인터페이스 구현을 심사해야 한다.

### @FunctionalInterface

- 해당 클래스의 코드나 문서를 읽은 사람에게 람다용으로 설계된 인터페이스임을 알린다.
- 해당 인터페이스가 추상 메서드 하나만 가지고 있어야 컴파일된다.
- 유지보수 과정에서 실수로 메서드를 추가하지 못하도록 막아준다.
- 직접 만든 함수형 인터페이스에서는 항상 @FunctionalInterface를 사용한다.

### 함수형 인터페이스를 API에서 사용할 때 주의점

- 서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중 정의하면 안된다.
- 클라이언트에게 불필요한 모호함을 준다.