# 아이템 43. 람다보다는 메서드 참조를 사용하라

### 메서드 참조

- 함수 객체를 람다보다 더 간결하게 만든다.
    
    ```java
    // 람다
    map.merge(key, 1, (count, incr) -> count + incr);
    
    // 메서드 참조
    map.merge(key, 1, Integer::sum);
    ```
    

### 람다와 메서드 참조

- 람다의 특징
    - 람다에서 매개변수 이름 자체가 좋은 가이드가 되기도 한다.
    - 메서드 참조보다 읽기 쉽고 유지보수도 쉬워진다.
    - 람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다.
- 메서드 참조의 특징
    - 보통 람다보다 메서드 참조가 가독성이 더 좋다.
    - IDE는 람다보다 메서드 참조로 대체하도록 권한다.
    

### 람다가 메서드 참조보다 간결한 경우

```java
// 메서드 참조
service.execute(GoshThisClassNameIsHumongous::action);

// 람다
service.execute(() -> action());
```

- `Function.identity()`를 사용하기보다 람다 `x → x`를 사용하는 것이 좋다.

### 메서드 참조의 유형

- 정적 메서드를 가리키는 메서드 참조
→ `Integer::parseInt`
- 인스턴스 메서드를 참조하는 유형
    - 수신 객체를 특정하는 한정적 인스턴스 메서드 참조
    → `Instant.now()::isAfter`
    - 수신 객체를 특정하지 않는 비한정적 메서드 참조
    → `String::toLowerCase`
- 클래스 생성자
→ `TreeMap<K,V>::new`
- 배열 생성자
→ `int[]::new`