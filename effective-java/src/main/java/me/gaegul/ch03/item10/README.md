# 아이템 10. equals는 일반 규약을 지켜 재정의하라

- equals
    - 재정의 하지 않으면 해당 클래스의 인스턴스는 오직 자기 자신만 같게 된다.
- 재정의 하지 않는 것이 좋은 상황
    - 각 인스턴스가 본질적으로 고유하다.
        - 값을 표현하지 않고 동작하는 개체를 표현하는 클래스
        → ex) Thread
    - 인스턴스의 '논리적 동치성(logical equality)'을 검사할 일이 없다.
        - Pattern의 인스턴스가 같은 정규 표현식을 나타내는 지 검사
        - 두 Random 인스턴스가 같은 난수를 생성하는지 확인하지 않아도 된다.
    - 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.
        - 같은 특징을 가지는 클래스인 경우 권장된다.
    - 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.
        - 실수로 equals가 호출되는 것을 막고 싶을 때

        ```java
        @Override
        public boolean equals(Object o) {
        		throw new AssertionError(); // 호출 금지!
        }
        ```

- 재정의해야 할 때
    - 객체 식별성이 아니라 논리적 동치성을 확인해야 하는 경우에 재정의가 필요하다.

        ```text
        객체 식별성(두 객체가 물리적으로 같은가, object identity) → X
        논리적 동치성(logical equality) → O
        ```

        - 두 값 객체가 같은 값인지 알고 싶어할 때
            - Map, Set의 원소로 사용할 수 있다.
        - 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스 통제 클래스라면 equals 재정의하지 않아도 된다.
- equals 재정의할 때 일반 규약
    - Object 명세에 적힌 규약

        > *equals 메서드는 동치관계(equivalence relation)를 구현하고 다음을 만족한다.*

        - 반사성(reflexivity)
            - 객체는 자기 자신과 같아야 한다.
            - null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true

            ```java
            public class Fruit {
                private String name;

                public Fruit(String name) {
                    this.name = name;
                }

                public static void main(String[] args) {
                    Fruit apple = new Fruit("Apple");
                    List<Fruit> fruits = new ArrayList<>();
                    fruits.add(apple);
                    boolean contains = fruits.contains(apple);
                    System.out.println(contains);
                }
            }
            ```

        - 대칭성(symmetry)
            - 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다.
            - null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true

            ```java
            public final class CaseInsensitiveString {

                private final String s;

                public CaseInsensitiveString(String s) {
                    this.s = Objects.requireNonNull(s);
                }

            		// 대칭성이 잘못 정의된 예제
                @Override
                public boolean equals(Object o) {
                    if (o instanceof CaseInsensitiveString)
                        return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
                    if (o instanceof String) // 한 방향으로만 동작한다.
                        return s.equalsIgnoreCase((String) o);
                    return false;
                }

            		// 대칭성을 보장하는 equals
            		@Override
                public boolean equals(Object o) {
                    return o instanceof CaseInsensitiveString &&
                            (((CaseInsensitiveString) o).s.equalsIgnoreCase(s));
                }

                @Override
                public int hashCode() {
                    return Objects.hash(s);
                }

                public static void main(String[] args) {
                    // 대칭성 위배 예제
                    CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
                    String s = "Polish";

                    System.out.println("cis.equals(s) : " + cis.equals(s)); // true
                    System.out.println("s.equals(cis) : " + s.equals(cis)); // false

            				// 컬렉션에서 대칭성 위배 예제
                    List<CaseInsensitiveString> list = new ArrayList<>();
                    list.add(cis);
                    System.out.println("list.contains(s) : " + list.contains(s)); // false
                }
            }
            ```

        - 추이성(transitivity)
            - 삼단논법이 적용되어야 한다.
            - null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고 y.equals(z)도 true면 x.equals(z)도 true
            - 문제점
                - 대칭성 위배 문제

                    ```java
                    // ColorPoint.java
                    @Override
                    public boolean equals(Object o) {
                        if (!(o instanceof ColorPoint)) return false;
                        return super.equals(o) && ((ColorPoint) o).color == color;
                    }

                    Point point = new Point(1, 2);
                    ColorPoint colorPoint = new ColorPoint(1, 2, Color.RED);

                    point.equals(colorPoint); // true (Point의 equals로 계산)
                    colorPoint.equals(point); // false (ColorPoint의 equals로 계산: color 필드 부분에서 false)
                    ```

                - 추이성 위배 문제

                    ```java
                    // ColorPoint.java
                    @Override
                    public boolean equals(Object o) {
                        if (!(o instanceof Point)) return false;

                        // o가 일반 Point면 색상을 무시하고 비교한다.
                        if (!(o instanceof ColorPoint)) return o.equals(this);

                        // o가 ColorPoint면 색상까지 비교한다.
                        return super.equals(o) && ((ColorPoint) o).color == color;
                    }

                    ColorPoint point1 = new ColorPoint(1, 2, Color.RED);
                    Point point2 = new Point(1, 2);
                    ColorPoint point3 = new ColorPoint(1, 2, Color.BLUE);

                    point1.equals(point2); // true (ColorPoint의 equals 비교 //2번째 if문에서 Point의 equals로 변환)
                    point2.equals(point3); // true (Point의 equals 비교 // x,y 같으니 true)
                    point1.equals(point3); // false (ColorPoint의 equals 비교)
                    ```

                - 무한재귀 위험 문제

                    ```java
                    // SmellPoint.java
                    // Point의 또 다른 하위 클래스
                    @Override
                    public boolean equals(Object o) {
                        if (!(o instanceof Point)) return false;
                        if (!(o instanceof SmellPoint)) return o.equals(this);
                        return super.equals(o) && ((SmellPoint) o).color == color;
                    }

                    ColorPoint colorPoint = new ColorPoint(1, 2, Color.BLUE);
                    SmellPoint smellPoint = new SmellPoint(1, 2, Color.RED);

                    colorPoint.equals(smellPoint); // 무한 재귀가 일어난다.
                    // 처음에 ColorPoint의 equals로 비교 : 2번째 if문 때문에 SmellPoint의 equals로 비교
                    // 이후 SmellPoint의 equals로 비교 : 2번째 if문 때문에 ColorPoint의 equals로 비교
                    ```

                - 리스코프 치환 원칙 위배
                    - Point의 하위클래스는 정의상 여전히 Point이므로 어디서든 Point로 활용가능해야한다.
                    - 리스코프 치환원칙 (Liskov substitution principle) : 어떤 타입에 있어 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다. 따라서 그 타입의 모든 메서드가 하위 타입에서도 똑같이 잘 작동해야한다.

                        ```java
                        // Point.java
                        @Override
                        public boolean equals(Object o) {
                          if (o == null || o.getClass() != getClass()) return false;
                          Point point = (Point) o;
                          return point.x == x && point.y == y;
                        }
                        ```

            - 문제 해결
                - 상속 대신 컴포지션을 사용하라
                    - Point를 상속하지 않고 private 필드에 선언하고 반환하는 view 메서드를 구현한다.
                    - ColorPoint는 Point 비교 시 view 메서드를 사용한다.

                    ```java
                    public class ColorPoint {
                        private final Point point;
                        private final Color color;

                        public ColorPoint(int x, int y, Color color) {
                            this.point = new Point(x, y);
                            this.color = Objects.requireNonNull(color);
                        }

                        /*
                         * 이 ColorPoint의 Point 뷰를 반환한다.
                         */
                        public Point asPoint() {
                            return point;
                        }

                        @Override
                        public boolean equals(Object o) {
                            if (!(o instanceof ColorPoint)) return false;
                            ColorPoint cp = (ColorPoint) o;
                            return cp.point.equals(point) && cp.color.equals(color);
                        }
                    }

                    point1.asPoint().equals(point2); // true
                    point2.equals(point3.asPoint()); // true
                    point1.asPoint().equals(point3.asPoint()); // true
                    ```

        - 일관성(conststency)
            - null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환
            - 두 객체가 같다면 수정되지 않는 한 영원히 같아야 한다.
            - equals 판단에 신뢰할 수 없는 자원이 끼어들게 해서는 안된다.
        - null 아님
            - 모든 객체가 null 과 같지 않아야 한다.
            - null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false
            - 명시적 null 검사보다 묵시적 null 검사가 더 낫다.
            - 잘못된 타입이 주어진 경우 ClassCastException을 발생시키도록 유도한다.
- equals 구현 방법
    - == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.
        - float와 double은 부동소수 값 등을 다루어야 하기 때문에 compare를 통해 비교한다.
            - `Float.compare, Double.compare`
        - 기본 타입 필드는 `== 연산자`
        - 참조 타입 필드는 `equals`
        - 배열은 `Arrays.equals`
        - null도 정상 값으로 취급하는 참조 필드는 `Objects.equals(object1, object2)`
    - instanceof 연산자로 입력이 올바른 타입인지 확인한다.
    - 입력을 올바른 타입으로 형변환한다.
    - 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.
        - 어떤 필드가 먼저 비교되느냐가 equals의 성능을 좌우한다.
            - 다를 가능성이 더 크거나 비용이 싼 필드를 먼저 비교
            - 핵심필드 / 파생필드 구분
    - equals 구현 후 대칭성, 추이성, 일관성 검증
- 전형적인 equals 구현

    ```java
    public final class PhoneNumber {

        private final short areaCode, prefix, lineNum;

        public PhoneNumber(int areaCode, int prefix, int lineNum) {
            this.areaCode = rangeCheck(areaCode, 999, "지역코드");
            this.prefix = rangeCheck(prefix, 999, "프리픽스");
            this.lineNum = rangeCheck(lineNum, 9999, "가입자 번호");
        }

        private static short rangeCheck(int val, int max, String arg) {
            if (val < 0 || val > max) throw new IllegalArgumentException(arg + ": " + val);
            return (short) val;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof PhoneNumber)) return false;
            PhoneNumber that = (PhoneNumber) o;
            return areaCode == that.areaCode &&
                    prefix == that.prefix &&
                    lineNum == that.lineNum;
        }

        public static void main(String[] args) {
            PhoneNumber phoneNumber1 = new PhoneNumber(02, 123, 4567);
            PhoneNumber phoneNumber2 = new PhoneNumber(02, 123, 4567);

            phoneNumber1.equals(phoneNumber2);
            phoneNumber2.equals(phoneNumber1);
        }
    }
    ```

- 마지막 주의사항
    - equals를 재정의할 땐 hashCode도 반드시 재정의하자
    - 너무 복잡하게 해결하려 들지 말자
    - Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자
    
        ```java
        // 다중 정의가 된다. 입력 타입은 반드시 Object여야 한다.
        public boolean equals(MyClass o) {
                ...
        }
        ```