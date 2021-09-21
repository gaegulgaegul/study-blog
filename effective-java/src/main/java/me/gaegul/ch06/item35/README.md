# 아이템 35. ordinal 메서드 대신 인스턴스 필드를 사용하라

### ordinal

- 열거 타입 상수가 몇번째 위치인지 반환하는 메서드
- EnumSet과 EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계
→ 위 용도가 아니라면 사용하지 않는다.

### ordinal 메서드를 잘못 사용한 예

```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTER, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

- 상수 선언 위치가 바뀌면 오동작하고 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다.
- 값을 중간에 비워둘 수 없다.

### 열거 타입 상수에 연결된 값은 ordinal 메서드로 얻지 말고 인스턴스 필드에 저장

```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTER(4),
    QUINTET(5), SEXTET(6), SEPTET(7), OCTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTER(12);
    
    private final int numberOfMusicians;

    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }
}
```