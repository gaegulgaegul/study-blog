# 아이템 49. 매개변수가 유효한지 검사하라

### 매개변수 검사를 제대로 하지 못하면 발생하는 문제

- 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.
- 메서드가 잘 수행되지만 잘못된 결과를 반환할 수 있다.
- 메서드는 문제없이 수행됐지만, 어떤 객체를 이상한 상태로 만들어놓아서 미래의 알 수 없는 시점에 이 메서드와 관련 없는 오류를 낼 수 있다. → 실패 원자성을 어긴다.

### public과 protected 메서드는 매개변수 값이 잘못 되었을 때 던지는 예외를 문서화한다.

- 보통은 `IllegalArgumentException, IndexOutOfBoundsException, NullPointerException`

```java
/**
 * (현재 값 mod m) 값을 반환한다. 이 메서드는
 * 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder 메서드와 다르다.
 * 
 * @param m 계수(양수여야 한다.)
 * @return 현재 값 mod m
 * @throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
 */
public BigInteger mod(BigInteger m) {
    if (m.signum() <= 0)
        throw new AritheticException("계수(m)는 양수여야 합니다. " + m);
    ... // 계산 수행
}
```

- `mod 메서드`는 m이 null이면 `m.signum()` 호출 시 `NullPointerException`을 던진다.
    - 메서드 설명은 없지만 `BigInteger` 클래스 수준에서 기술되어 있다.


### java.util.Objects.requireNonNull

- null 검사를 수동으로 하지 않아도 된다.

```java
this.strategy = Objects.requireNonNull(strategy, "전략");
```

### 단언문(assert)

- public이 아닌 메서드는 매개변수 유효성을 검증할 수 있다.

```java
private static void sort(long a[], int, offset, int length) {
    assert a !=null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length = offset;
    ... // 계산 수행
}
```

- 단언문은 자신이 단언한 조건이 무조건 참이라고 선언한다.
- 유효성 검사와 다른점
    - 실패하면 `AssertionError` 발생
    - 런타임에 아무런 효과도, 아무런 성능 저하도 없다.


### 정적 팩터리

```java
public static List<Integer> toList(int[] array) {
    // 검사가 없으면 반환된 결과를 사용했을 때 오류가 발생한다.
    Objects.requireNonNull(array);
    return Arrays.stream(array)
            .mapToObj(Integer::valueOf)
            .collect(Collectors.toList());
}
```

### 생성자

- "나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라"는 원칙의 특수 사례
- 매개변수의 유효성 검사는 클래스 불변식을 어기는 객체가 만들어지지 않게 하는 데 꼭 필요하다.

### 메서드 몸체 실행 전에 매개변수 유효성을 검사해야 한다는 규칙의 예외

- 유효성 검사 비용이 지나치게 높거나 실용적이지 않을 때
- 계산 과정에서 암묵적으로 검사가 수행될 때
    - `Collections.sort(list)`
        - 리스트 안의 객체들은 모두 상호 비교할 수 있어야 된다.
        - 정렬 과정에서 상호 비교할 수 없는 타입의 객체가 있다면 `ClassCastException` 발생


### 계산 과정에서 유효성 검사가 이뤄지지만 실패했을 때 잘못된 예외 발생

- 계산 중 잘못된 매개변수 값을 사용해 발생한 예외와 API 문서에서 던지기로 한 예외가 다를 수 있다.
- 예외 번역 관용구를 사용하여 API 문서에 기재된 예외로 번역해줘야 한다.

<aside>
💡 메서드는 최대한 범용적으로 설계해야 한다.
<br/>메서드가 건네 받은 값으로 무언가 제대로 된 일을 할 수 있다면 매개변수 제약은 적을수록 좋다.

</aside>