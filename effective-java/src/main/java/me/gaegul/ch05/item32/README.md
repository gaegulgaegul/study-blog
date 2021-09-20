# 아이템 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라

- 가변인수 메서드
    - 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해준다.
    - 가변인수 메서드를 호출하면 가변인수를 담기 위한 자동으로 배열이 만들어진다.
- 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.

    ```java
    static void dangerous(List<String>... stringLists) {
        List<Integer> intList = List.of(42);
        Object[] objects = stringLists;
        objects[0] = intList; // 힙 오염 발생
        // ClassCacstException;
        // 컴파일러가 생성한 보이지 않는 형변환이 숨어 있다.
        String s = stringLists[0].get(0);
    }
    ```

    - 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.
- 자바 라이브러리에서 가변인수 메서드와 제네릭을 같이 쓴 메서드
    - `Arrays.asList(T... a), Collections.addAll(Collection<? super T>c, T... elements), EnumSet.of(E first, E...rest)`
    - 위 메서드는 Type Safe
    - 자바 7이전에는 @SuppressWarnings("unchecked") 어노테이션을 추가해 경고를 숨겼다.
    → 진짜 문제가 있어 발생하는 경고까지 숨긴다.
    - 자바7 이후 @SafeVarargs 어노테이션을 추가해 경고를 숨긴다.
- @SafeVarargs
    - 메서드 작성자가 해당 메서드가 Type Safe를 보장하는 장치
    - 메서드가 안전하지 않다면 절대 추가하면 안된다.
    - 안전함을 보장하는 방법
        - 가변인수 메서드를 호출할 때 매개변수를 담은 제네릭 배열이 생성된다.
        - 제네릭 배열에 아무것도 저장하지 않고 배열의 참조가 밖으로 노출되지 않는다면 Type Safe
        - 자신의 제네릭 매개변수 배열의 참조를 노출한다. - 안전하지 않다.

            ```java
            static <T> T[] toArray(T... args) {
                return args;
            }
            ```

        - 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다.

            ```java
            // ClassCastException 발생
            static <T> T[] pickTwo(T a, T b, T c) {
                switch (ThreadLocalRandom.current().nextInt(3)) {
                    case 0: return toArray(a, b);
                    case 1: return toArray(a, c);
                    case 2: return toArray(b, c);
                }
                throw new AssertionError(); // 도달할 수 없다.
            }

            public static void main(String[] args) {
                String[] strings = pickTwo("좋은", "빠른", "저렴한");
                System.out.println(strings);
            }
            ```

            - 예외
                - @SafeVarargs로 제대로 애노테이트된 또 다른 varargs 메서드에 넘기는 것은 안전하다.
                - 배열 내용의 일부 함수를 호출만 하는 일반 메서드에 넘기는 것도 안전하다.
        - 제네릭 varargs 매개변수를 안전하게 사용하는 예

            ```java
            @SafeVarargs
            static <T> List<T> flatten(List<? extends T>... lists) {
                List<T> result = new ArrayList<>();
                for (List<? extends T> list : lists) {
                    result.addAll(list);
                }
                return result;
            }
            ```

            - @SafeVarargs 어노테이션을 사용해야 할 때를 정하는 규칙
                - 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 @SafeVarargs를 달아라.
                    - 컴파일 경고를 제거할 수 있다.
                - 제네릭 varargs 매개변수를 사용하며 힙 오염 경고가 나오는 메서드가 있다면 메서드의 안전성을 점검해야 한다.
                    - varargs 매개변수 배열에 아무것도 저장하지 않는다.
                    - 배열을 신뢰할 수 없는 코드에 노출하지 않는다.
        - 제네릭 varargs 매개변수를 List로 대체 - Type Safe

            ```java
            static <T> List<T> flatten(List<List<? extends T>> lists) {
                List<T> result = new ArrayList<>();
                for (List<? extends T> list : lists) {
                    result.addAll(list);
                }
                return result;
            }
            ```