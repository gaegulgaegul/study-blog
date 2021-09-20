# 아이템 34. int 상수 대신 열거 타입을 사용하라

### 정수 열거 패턴

```java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```

- 타입 안전을 보장할 방법이 없다.
- 표현력이 좋지 않다.
- 정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다.
    - 평범한 상수를 나열한 것 뿐아니라 컴파일하면 그 값이 클라이언트 파일에 그래도 새겨진다.
- 정수 상수는 문자열로 출력하기 까다롭다.
    - 값을 출력하거나 디버거로 보면 숫자만 보인다.
- 정수 대신 문자열 상수를 사용하는 변형 패턴
    - 상수의 의미를 출력할 수 있다.
    - 하드코딩 해야 되어 오타가 나면 런타임 버그가 발생한다.

### 열거타입

```java
public enum Apple {
    FUJI, PIPPEN, GRANNY_SMITH
}

public enum Orange {
    NAVEL, TEMPLE, BLOOD
}
```

- 열거 타입 자체는 클래스
- 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다.
- 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않아 사실상 final이다.
- 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재한다.
- 컴파일 타임의 안전성을 보장한다.
    - Apple 열거 타입을 매개변수로 받는 메서드에 넘겨야 하는 값은 Apple의 세가지 타입 중 하나이 확실하다.
    - 다른 타입의 값을 넘기려 한다면 컴파일 에러가 발생한다.
- 각자의 이름공간이 있어서 이름이 같은 상수도 공존한다.
- 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일 하지 않아도 된다.
    - 공개되는 것은 필드의 이름뿐이다.

### 열거 타입에 메서드나 필드를 추가할 때

- 각 상수와 연관된 데이터를 해당 상수 자체에 내재시키고 싶다.
    - 태양계 8개 행성을 가지는 열거 타입

        ```java
        public enum Planet {
            MERCURY(3.302e+23, 2.439e6),
            VENUS(4.869e+24, 6.052e6),
            EARTH(5.975e+24, 6.378e6),
            MARS(6.419e+23, 3.393e6),
            JUPITER(1.899e+27, 7.149e7),
            SATURN(5.685e+26, 6.027e7),
            URANUS(8.683e+25, 2.556e7),
            NEPTUNE(1.024e+26, 2.477e7);

            private final double mass;              // 질량(단위: 킬로그램)
            private final double radius;            // 반지름(단위: 미터)
            private final double surfaceGravity;    // 표면중력(단위: m / s^2)
            
            // 중력상수(단위: m^3 / kg s^2)
            private static double G = 6.67300E-11;

            // 생성자
            Planet(double mass, double radius) {
                this.mass = mass;
                this.radius = radius;
                this.surfaceGravity = G * mass / (radius * radius);
            }

            public double mass() {
                return mass;
            }

            public double radius() {
                return radius;
            }

            public double surfaceGravity() {
                return surfaceGravity;
            }
            
            public double surfaceWeight(double mass) {
                return mass * surfaceGravity;
            }
        }
        ```

        - 열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.
            - 열거 타입의 모든 필드는 final이여야 한다.
    - 8개 행성에서의 무게를 출력하는 코드

        ```java
        public class WeightTable {
            public static void main(String[] args) {
                double earthWeight = Double.parseDouble(args[0]);
                double mass = earthWeight / Planet.EARTH.surfaceGravity();
                for (Planet p : Planet.values()) {
                    System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
                }
            }
        }

        // console
        MERCURY에서의 무게는 69.912739이다.
        VENUS에서의 무게는 167.434436이다.
        EARTH에서의 무게는 185.000000이다.
        MARS에서의 무게는 70.226739이다.
        JUPITER에서의 무게는 467.990696이다.
        SATURN에서의 무게는 197.120111이다.
        URANUS에서의 무게는 167.398264이다.
        NEPTUNE에서의 무게는 210.208751이다.
        ```

        - values() → 자신 안에 정의된 상수들의 값을 배열에 담아 반환
- Planet의 행성 하나를 제거한다면?
    - 클라이언트 코드에서 사용 중이지 않다면 에러가 발생하지 않는다.
    - 클라이언트 코드에서 사용 중이라면 컴파일 에러가 발생한다.
    → 정수 열거 패턴은 에러가 발생하지 않는다.

### 열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 private이나 package-private 메서드로 구현한다.

- 자신을 선언한 클래스 혹은 패키지에서만 사용할 수  있는 기능을 담게 된다.
- 널리 쓰이는 열거 타입은 탑레벨 클래스로 만든다.
- 특정 탑레벨 클래스에서만 쓰인다면 해당 클래스의 멤버 클래스로 만든다.

### 상수가 더 다양한 기능을 제공

- 사칙연산 계산기

    ```java
    public enum Operation {
        PLUS, MINUS, TIMES, DIVIDE;
        
        public double apply(double x, double y) {
            switch (this) {
                case PLUS: return x + y;
                case MINUS: return x - y;
                case TIMES: return x * y;
                case DIVIDE: return x / y;
            }
            throw new AssertionError("알 수 없는 연산: " + this);
        }
    }
    ```

    - 새로운 상수를 추가하면 case를 추가해야 한다. 추가하지 않으면 throw문을 실행한다.
    - 깨지기 쉬운 코드다.
- 상수별 메서드 구현

    ```java
    public enum Operation {
        PLUS{
            @Override
            public double apply(double x, double y) {
                return x + y;
            }
        },
        MINUS{
            @Override
            public double apply(double x, double y) {
                return x - y;
            }
        },
        TIMES{
            @Override
            public double apply(double x, double y) {
                return x * y;
            }
        },
        DIVIDE{
            @Override
            public double apply(double x, double y) {
                return x / y;
            }
        };

        // 추상 메서드를 선언해 각 상수별 상황에 맞게 재정의한다.
        public abstract double apply(double x, double y);
    }
    ```

- 상수별 class body와 데이터를 사용한 열거 타입

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

        public abstract double apply(double x, double y);

        @Override
        public String toString() {
            return symbol;
        }
    }

    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        for (Operation op : Operation.values()) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }

    // console
    2.000000 + 4.000000 = 6.000000
    2.000000 - 4.000000 = -2.000000
    2.000000 * 4.000000 = 8.000000
    2.000000 / 4.000000 = 0.500000
    ```

### fromString

- 문자열을 해당 열거 타입 상수로 변환해준다.

    ```java
    private static final Map<String, Operation> stringToEnum = Stream.of(values())
                .collect(toMap(Object::toString, e -> e));

    // 지정한 문자열에 해당하는 Operation을 반환한다.
    public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }
    ```

### 급여명세서에서 쓸 요일을 표현하는 열거 타입

- 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다.

    ```java
    public enum PayrollDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
        
        private static final int MINS_PER_SHIFT = 8 * 60;
        
        int pay(int minuteWorked, int payRate) {
            int basePay = minuteWorked * payRate;
            
            int overtimePay;
            switch (this) {
                case SATURDAY:
                case SUNDAY:
                    overtimePay = basePay / 2;
                    break;
                default:
                    overtimePay = minuteWorked <= MINS_PER_SHIFT ? 0 : (minuteWorked - MINS_PER_SHIFT) * payRate / 2;
            }
            
            return basePay + overtimePay;
        }
    }
    ```

    - case문을 꼭 쌍으로 넣어야 한다.
    - 코드가 장황해져 가독성이 떨어진다.
- 새로운 상수를 추가해 잔업수당 전략을 선택하도록 한다.

    ```java
    public enum PayrollDay {
        MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY), THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
        SATURDAY(WEEKEND), SUNDAY(WEEKEND);

        private final PayType payType;

        PayrollDay(PayType payType) {
            this.payType = payType;
        }
        
        int pay(int minutesWorked, int payRate) {
            return payType.pay(minutesWorked, payRate);
        }

        enum PayType {
            WEEKDAY {
                @Override
                int overtimePay(int minsWorked, int payRate) {
                    return minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2;
                }
            },
            WEEKEND {
                @Override
                int overtimePay(int minsWorked, int payRate) {
                    return minsWorked * payRate / 2;
                }
            };
            
            abstract int overtimePay(int mins, int payRate);
            
            private static final int MINS_PER_SHIFT = 8 * 60;
            
            int pay(int minsWorked, int payRate) {
                int basePay = minsWorked + payRate;
                return basePay + overtimePay(minsWorked, payRate);
            }
        }
    }
    ```

### 기존 열거 타입에 상수별 동작을 혼합해 넣을 때 switch문

```java
public static Operation inverse(Operation op) {
        switch (op) {
            case PLUS: return Operation.MINUS;
            case MINUS: return Operation.PLUS;
            case TIMES: return Operation.DIVIDE;
            case DIVIDE: return Operation.TIMES;
        }
        throw new AssertionError("알 수 없는 연산: " + op);
    }
```

- 추가하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입 방식이라도 이 방식을 적용하는게 좋다.

### 열거 타입을 써야 할 때

- 필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 열거 타입을 사용하자
    - 허용하는 값 모두를 컴파일 타임에 이미 알고 있을 때도 쓸 수 있다.
- 열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.
    - 열거 타입에 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계