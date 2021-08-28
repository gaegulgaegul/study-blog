# 아이템 24. 멤버 클래스는 되도록 static으로 만들라

- 중첩 클래스
    - 다른 클래스 안에 정의된 클래스
    - 자신을 감싼 바깥 클래스에서만 쓰여야 한다.
    - 중첩 클래스의 종류
        - 정적 멤버 클래스
        - (비정적) 멤버 클래스
        - 익명 클래스
        - 지역 클래스

- 정적 멤버 클래스
    - 바깥 클래스의 private 멤버에도 접근할 수 있는 점만 일반 클래스와 같다.
    - 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스
    - 개념상 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재한다.

    ```java
    public class Calculator {

        public enum Operation {
            PLUS("+", (x, y) -> x + y),
            MINUS("-", (x, y) -> x - y);

            private final String token;
            private final Strategy strategy;

            Operation(String token, Strategy strategy) {
                this.token = token;
                this.strategy = strategy;
            }

            public double operate(double x, double y) {
                return strategy.operate(x, y);
            }

            public interface Strategy {
                double operate(double x, double y);
            }
        }
    }
    ```

- 비정적 멤버 클래스
    - 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.
    - 비정적 멤버 클래스의 인스턴스 메서드에서 정규화된 this를 사용해 바깥 인스턴스의 메서드를 호출하거나 바깥 인스턴스의 참조를 가져올 수 있다.
    → 정규화된 this : *`클래스명*.this`

        ```java
        public class NestedNonStaticClass {

            private final String name;

            public NestedNonStaticClass(String name) {
                this.name = name;
            }

            public String getName() {
                // 비정적 멤버 클래스와 바깥 클래스의 관계가 확립되는 부분
                NonStaticClass nonStaticClass = new NonStaticClass("nonStatic : ");
                return nonStaticClass.getNameWithOuter();
            }

            private class NonStaticClass {
                private final String nonStaticName;

                public NonStaticClass(String nonStaticName) {
                    this.nonStaticName = nonStaticName;
                }

                public String getNameWithOuter() {
                    // 정규화된 this를 이용해서 바깥 클래스의 인스턴스 메서드를 사용한다.
                    return nonStaticName + NestedNonStaticClass.this.getName();
                }
            }
        }
        ```

    - 비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 인스턴스화될 떄 확립되고 변경할 수 없다.
        - 보통 바깥 클래스의 인스턴스 메서드에서 비정적 멤버 클래스의 생성자를 호출할 때 자동으로 만들어진다.
        - 드물게 *`바깥_인스턴스의_클래스*.new MemberClass(args)` 수동 호출
        - 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하고 시간도 더 걸린다.

        ```java
        public class NestedNonStaticClass {

            private final String name;

            public NestedNonStaticClass(String name) {
                this.name = name;
            }

        ... 중략

            public class NonStaticPublicClass {

            }

            public static void main(String[] args) {
                NestedNonStaticClass nestedNonStaticClass = new NestedNonStaticClass("name");
                nestedNonStaticClass.new NonStaticPublicClass();
            }
        }
        ```

    - 비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다.
        - 어떤 클래스의 인스턴스를 감싸 다른 클래스의 인스턴스처럼 보이제 하는 뷰로 사용
        → `Map`의 `keySet, entrySet, values` 메서드가 반환하는 자신의 컬렉션 뷰를 구현할 때 사용

        ```java
        public class HashMap<K,V> extends AbstractMap<K,V>
            implements Map<K,V>, Cloneable, Serializable {

        .. 중략

        		public Set<K> keySet() {
                Set<K> ks = keySet;
                if (ks == null) {
                    ks = new KeySet();
                    keySet = ks;
                }
                return ks;
            }

            final class KeySet extends AbstractSet<K> {
                public final int size()                 { return size; }
                public final void clear()               { HashMap.this.clear(); }
                public final Iterator<K> iterator()     { return new KeyIterator(); }
                public final boolean contains(Object o) { return containsKey(o); }
                public final boolean remove(Object key) {
                    return removeNode(hash(key), key, null, false, true) != null;
                }
                public final Spliterator<K> spliterator() {
                    return new KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
                }
                public final void forEach(Consumer<? super K> action) {
                    Node<K,V>[] tab;
                    if (action == null)
                        throw new NullPointerException();
                    if (size > 0 && (tab = table) != null) {
                        int mc = modCount;
                        for (Node<K,V> e : tab) {
                            for (; e != null; e = e.next)
                                action.accept(e.key);
                        }
                        if (modCount != mc)
                            throw new ConcurrentModificationException();
                    }
                }
            }

        ...

        }
        ```

- 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여서 정적 멤버 클래스로 만들자
    - 바깥 인스턴스로 숨은 외부 참조를 갖게 된다.
    - 가비지 컬렉터가 바깥 인스턴스를 수거하지 못하는 경우도 발생한다. → 메모리 누수

- 익명 클래스
    - 바깥 클래스의 멤버가 아니다.
    - 사용되는 시점에 인스턴스화 되고 어디서든 만들 수 있다.
    - 비정적인 문맥에서만 사용될 때 바깥 클래스의 인스턴스를 참조할 수 있다.
    - `static final` 외 정적 변수를 가질 수 없다.

    ```java
    public class AnonymousClass {
        private double x;
        private double y;

        public double operate() {
            Operator operator = new Operator() {
                @Override
                public double plus() {
                    System.out.printf("%f + %f = %f\n", x, y, x + y);
                    return x + y;
                }

                @Override
                public double minus() {
                    System.out.printf("%f - %f = %f\n", x, y, x - y);
                    return x - y;
                }
            };
            return operator.plus();
        }
    }

    interface Operator {
        double plus();

        double minus();
    }
    ```

    - 익명 클래스 제약 사항
        - 선언한 지점에서만 인스턴스를 만들 수 있다.
        - instanceof 검사나 클래스의 이름이 필요한 작업은 수행할 수 없다.
        - 여러 인터페이스를 구현할 수 없다.
        - 인터페이스를 구현하는 동시에 다른 클래스를 상속할 수 없다.
        - 익명 클래스를 사용하는 클라이언트는 사용하는 익명 클래스가 상위 타입에서 상속한 멤버 외에는 호출할 수 없다.
        - 표현식 중간에 등장하여 10줄이 넘어가면 가독성이 나빠진다.
    - 익명 클래스 사용
        - 람다를 사용하자.
        - 정적 팩토리 메서드를 구현할 때 사용되기도 한다.

- 지역 클래스
    - 가장 드물게 사용
    - 지역변수를 선언할 수 있는 곳 어디든지 선언할 수 있다.
    - 멤버 클래스처럼 이름이 있고 반복해서 사용 가능
    - 익명 클래스처럼 비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조할 수 있다.
    - 정적 멤버는 가질 수 없고 가독성을 위해 짧게 작성해야 한다.