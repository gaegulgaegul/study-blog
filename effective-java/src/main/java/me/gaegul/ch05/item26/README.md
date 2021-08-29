# 아이템 26. 로 타입은 사용하지 말라

- 제네릭 타입
    - 클래스와 인터페이스 선언에 타입 매개변수가 쓰이면 제네릭 클래스 또는 제네릭 인터페이스라 하고 통틀어 제네릭 타입이라 한다.
    - 매개변수화 타입
        - 클래스 이름이 나오고 이어서 <> 안에 실제 타입 매개변수들을 나열한다.
    - 제네릭 타입 하나 정의하면 로 타입(raw type)도 함께 정의된다.
        - 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때
        → `List<E> - List`

            ```java
            // Stamp 인스턴스만 취급한다.
            private static final Collection stamps = new ArrayList<>();

            public static void main(String[] args) {
                stamps.add(new Coin()); // "unchecked call" 경고 발생

                for (Iterator i = stamps.iterator(); i.hasNext(); ) {
                    Stamp stamp = (Stamp) i.next(); // ClassCastException 발생
                    stamp.cancel();
                }

            }

            ===

            private static final Collection<Stamp> stamps = new ArrayList<>();

            public static void main(String[] args) {
                stamps.add(new Coin()); // "unchecked call" 경고 발생

                for (Iterator i = stamps.iterator(); i.hasNext(); ) {
                    Stamp stamp = (Stamp) i.next(); // ClassCastException 발생
                    stamp.cancel();
                }

            }
            ```

- 로 타입을 쓰면 제네릭의 안전성과 표현력을 잃게 된다.
    - List와 List<Object>의 차이
        - List는 제네릭 타입을 아예 선언하지 않은 상태
        - List<Object>는 모든 타입을 허용한다.
        - 매개변수로 List를 받는 매서드에 List<String>을 넘길 수 있지만 List<Object>는 넘길 수 없다.
        → List<String>은 로 타입인 List의 하위 타입, List<Object>의 하위타입은 아니다.
            - 형 변환 시 ClassCastException 발생

            ```java
            public class LawTypeMain {
                public static void main(String[] args) {
                    List<String> strings = new ArrayList<>();
                    unsafeAdd(strings, Integer.valueOf(42));
                    String s = strings.get(0); // 컴파일러가 자동으로 형변환 코드를 넣어준다.
                }

                private static void unsafeAdd(List list, Object o) {
                    list.add(o);
                }
            }
            ```

- 비한정적 와일드카드 타입
    - 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 모를 때 사용한다.
    - `?`를 타입 매개변수로 지정한다.

        ```java
        static int numElementsInCommon(Set<?> s1 , Set<?> s2) {
            int result = 0;
            for (Object o1 : s1) {
                if (s2.contains(o1)) result++;
            }
            return result;
        }
        ```

    - Set<?>와 Set의 차이
        - 와일드카드는 type safe, null외의 어떤 원소도 넣을 수 없다.
        - 로 타입은 non type safe, 아무 타입의 원소나 넣을 수 있어 타입 불변식이 깨진다.

- 로 타입을 써야하는 예외
    - class 리터럴에는 로 타입을 써야한다.
        - `List.class, String[].class, int.class` → 허용
        - `List<String>.class, List<?>.class` → 허용하지 않는다.
    - instanceof 연산자
        - 로 타입이든 비한정적 와일드카드 타입이든 instanceof는 완전 똑같이 동작한다.

        ```java
        if (o instanceof Set) {
            Set<?> s = (Set<?>) o;
        }
        ```