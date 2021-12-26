# 아이템 61. 박싱된 기본 타입 보다는 기본 타입을 사용하라

### 데이터 타입

- 기본 타입
    - `int, double, boolean`
- 참조 타입
    - `String, List`
- 박싱 타입
    - 각각의 기본 타입에 대응하는 참조 타입
    - `int, double, boolean` → `Integer, Double, Boolean`
    - 오토박싱과 오토언박싱 덕분에 타입을 구분하지 않아도 되지만 차이가 사라지는 것이 아니다.

      → 어떤 타입을 사용하는지 상당히 중요하다.


### 기본 타입과 박싱된 기본 타입의 차이점

- 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 값에 더해 식별성이란 속성을 갖는다.
    - 두 인스턴스의 값이 같아도 서로 다르다고 식별된다.
- 기본 타입의 값은 언제나 유효하나, 박싱된 기본 타입은 유효하지 않은 값(null)을 가질 수 있다.
- 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다.

### 식별성

```java
Comparator<Integer> naturalOrder = (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);

int compare = naturalOrder.compare(new Integer(42), new Integer(42));
System.out.println(compare);

// console
1
```

- 두 Integer 인스턴스의 값은 42로 같으므로 0이 출력되어야 하지만 1이 출력되었다. → 결함
    - 첫번째 검사 `(i < j)`는 잘 동작하지만 `(i == j)`는 두 **객체 참조**의 식별성을 검사한다.
    - 박싱된 기본 타입에 == 연산자를 사용하면 오류가 발생한다.
- 기본 타입 정수로 변환하여 비교를 수행하는 것으로 결함을 해결한다.

    ```java
    Comparator<Integer> naturalOrder = (iBoxed, jBoxed) -> {
        int i = iBoxed, j = jBoxed; // 오토 박싱
        return i < j ? -1 : (i == j ? 0 : 1);
    };
    ```

    - `Comparator.naturalOrder()`를 사용하여 기본타입을 다루는 비교자로 사용할 수 있다.

### NPE

```java
public class Unbelievable {
    static Integer i;

    public static void main(String[] args) {
        if (i == 42)
            System.out.println("믿을 수 없군");
    }
}
```

- `i == 42`를 비교할 때 `NullPointerException`을 던진다.
    - `i`는 `int`가 아닌 `Integer`이며, 다른 참조 타입 필드와 마찬가지로 `i`의 초깃값도 `null`이라는데 있다.
    - 기본 타입과 박싱된 기본 타입을 혼용한 연산에서는 박싱된 기본 타입의 박싱이 자동으로 풀린다.

### 박싱과 언박싱

```java
public class BoxAndUnbox {
    public static void main(String[] args) {
        Long sum = 0L;
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            sum += i;
        }
        System.out.println(sum);
    }
}

// console
2305843008139952128
```

- 오류나 경고 없이 컴파일 되어 실행하지만, 박싱과 언박싱이 반복해서 일어나 성능이 느려진다.

### 박싱된 기본 타입 사용

- 컬렉션의 원소, 키, 값으로 사용한다.
- 리플렉션을 통해 메서드를 호출할 때도  박싱된 기본 타입을 사용해야 한다.