# 아이템 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

### 타입 안전 열거 패턴은 확장할 수 있으나 열거 타입은 확장 할 수 없다.

- 열거 타입 자체는 확장 할 수 없다. 인터페이스를 통해 확장하는 효과를 낼 수 있다.
- 대부분 열거 타입을 확장하는 것은 좋지 않다.
- 확장성을 높이면 고려할 요소가 늘어나 설계와 구현이 복잡해진다.

### 연산 코드

- 연산 코드용 인터페이스 정의

    ```java
    public interface Operation {
        double apply(double x, double y);
    }
    ```

- 연산 열거 타입 구현

    ```java
    public enum BasicOperation implements Operation {
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

        BasicOperation(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }

    }
    ```

    - BasicOperation은 확장할 수 없지만 Operation은 확장할 수 있다.
    - Operation을 구현할 또 다른 열거 타입을 정의하여 BasicOperation을 대체할 수 있다.
- Operation 확장 열거 타입

    ```java
    public enum ExtendedOperation implements Operation {
        EXP("^") {
            @Override
            public double apply(double x, double y) {
                return Math.pow(x, y);
            }
        },
        REMAINDER("%") {
            @Override
            public double apply(double x, double y) {
                return x % y;
            }
        };

        private final String symbol;

        ExtendedOperation(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }
    ```

    - Operation 인터페이스를 사용하는 곳이면 BasicOperation을 대신해 사용할 수 있다.
    - apply 메서드가 인터페이스에 선언되어 열거 타입에 따로 추상 메서드로 선언하지 않아도 된다.
- Operation 테스트
    - 한정적 타입 토큰

        ```java
        public class OperationMain {
            public static void main(String[] args) {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                // BasicOperation
                test(BasicOperation.class, x, y);
                // ExtendedOperation
                test(ExtendedOperation.class, x, y);
            }

            // class 리터럴을 받아 한정적 타입 토큰 역할
            private static <T extends Enum<T> & Operation> void test(
                        Class<T> opEnumType, double x, double y) {
                for (Operation op : opEnumType.getEnumConstants()) {
                    System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
                }
            }
        }

        // BasicOperation console
        90.000000 + 5.000000 = 95.000000
        90.000000 - 5.000000 = 85.000000
        90.000000 * 5.000000 = 450.000000
        90.000000 / 5.000000 = 18.000000

        // ExtendedOperation console
        90.000000 ^ 5.000000 = 5904900000.000000
        90.000000 % 5.000000 = 0.000000
        ```

        - `<T extends Enum<T> & Operation>` → Class 객체가 열거 타입인 동시에 Operation의 하위타입
        - 열거 타입이어야 원소를 순회할 수 있고, Operation이어야 원소가 연산을 수행 할 수 있다.
    - 한정적 와일드카드 타입
        - Collection<? extends Operation>

        ```java
        public class OperationMain {
            public static void main(String[] args) {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                test(Arrays.asList(ExtendedOperation.values()), x, y);
            }

            private static void test(Collection<? extends Operation> opSet, double x, double y) {
                for (Operation op : opSet) {
                    System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
                }
            }
        }

        // BasicOperation console
        90.000000 + 5.000000 = 95.000000
        90.000000 - 5.000000 = 85.000000
        90.000000 * 5.000000 = 450.000000
        90.000000 / 5.000000 = 18.000000

        // ExtendedOperation console
        90.000000 ^ 5.000000 = 5904900000.000000
        90.000000 % 5.000000 = 0.000000
        ```

        - 여러 구현 타입의 연산을 조합해 호출할 수 있게 되었다.
        - 특정 연산에서는 EnumSet, EnumMap을 사용할 수 없다.

### 인터페이스를 이용해 열거 타입을 확장하는 방식에는 열거 타입끼리 구현을 상속할 수 없다.

- 아무 상태에도 의존하지 않는 경우 **디폴트 구현을 이용해 인터페이스에 추가**
- 구현 시 코드 중복량이 많다면 별도 도우미 클래스나 정적 도우미 메서드로 분리하는 것이 좋다.