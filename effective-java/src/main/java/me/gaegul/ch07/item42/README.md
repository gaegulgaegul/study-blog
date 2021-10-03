# 아이템 42. 익명 클래스보다는 람다를 사용하라

### 익명 클래스

- 함수 객체를 만드는 수단

```java
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
});
```

- 전략 패턴
    - 함수 객체를 사용하는 과거 객체 지향 디자인 패턴에 익명 클래스를 사용
    - 익명 클래스를 사용하면 코드가 너무 길어져 함수형 프로그래밍에 적합하지 않다.
    

### 람다식

- 함수형 인터페이스의 인스턴스를 람다식을 사용해 만들 수 있다.
- 함수나 익명 클래스와 개념은 비슷하지만 코드는 훨씬 간결하다.
- 어떤 동작을 하는지 명확하게 들어난다.

```java
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

- 익명 클래스 코드와 비교했을 때 String, int에 대한 정보가 없지만 컴파일러가 문맥을 살펴 타입을 추론한다.
- 타입을 명시해야 코드가 더 명확할 때만 제외하고 람다의 모든 매개변수 타입은 생략

### Operation 열거 타입

- 추상 메서드를 이용한 apply 구현

```java
public enum Operation {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}
```

- 함수 객체를 인스턴스 필드에 저장해 상수별 동작 구현

```java
public enum Operation {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```

### 람다를 사용하는 방법

- 람다는 메서드 이름이 없고 문서화도 못한다.
- 코드 자체로 동작이 명확하지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.
    - 한 줄일 때 가장 좋고 길어야 세 줄 안에 끝내는게 좋다.
    - 람다가 길거나 가독성이 좋지 않다면 람다를 쓰지 않는 쪽으로 리펙터링 하는게 좋다.