# 아이템 30. 이왕이면 제네릭 메서드로 만들라

- 두 집합의 합집합을 반환하는 메서드

    ```java
    // lawtype 경고 발생
    public static Set union(Set s1, Set s2) {
        Set result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }
    ```

- 제네릭 메서드

    ```java
    // 타입 매개변수 목록은 메서드 제한자와 반환 타입 사이에 선언
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }
    ```

    - 한정적 와일드카드로 더 유연하게 개선할 수 있다.
- 제네릭 싱글턴 팩터리
    - 요청한 타입에 맞게 매번 객체의 타입을 바꿔주는 정적 팩터리
    - `Collections.reverseOrder, Collections.emptySet 등`
- 항등함수
    - 입력 값을 수정 없이 그대로 반환하는 함수
    - 제네릭 싱글턴 팩터리 패턴

        ```java
        private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

        @SuppressWarnings("unchecked")
        public static <T> UnaryOperator<T> identityFunction() {
            return (UnaryOperator) IDENTITY_FN;
        }

        // 형변환을 하지 않아도 컴파일 오류나 경고가 발생하지 않는다.
        public static void main(String[] args) {
            String[] strings = { "삼베", "대마", "나일론" };
            UnaryOperator<String> sameString = identityFunction();
            for (String s : strings) {
                System.out.println(sameString.apply(s));
            }

            Number[] numbers = { 1, 2.0, 3L };
            UnaryOperator<Number> sameNumber = identityFunction();
            for (Number n : numbers) {
                System.out.println(sameNumber.apply(n));
            }
        }
        ```

- 재귀적 타입 한정
    - 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정할 수 있다.
    - Comparable 인터페이스
        - 자신과 같은 타입의 원소만 비교할 수 있다.
        - Comparable을 구현한 원소의 컬렉션을 입력받는 메서드들은 주로 그 원소를 정렬, 검색, 최솟값, 최댓값을 구하는 식으로 사용한다.
        - 재귀적 타입 한정을 이용해 상호 비교할 수 있음을 표현

            ```java
            // <E extends Comparable<E>> - 모든 타입 E는 자신과 비교할 수 있다.
            public static <E extends Comparable<E>> E max(Collection<E> c);
            ```

        - max 메서드 구현

            ```java
            public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
                E result = null;
                for (E e : c) {
                    if (result == null || e.compareTo(result) > 0) {
                        result = Objects.requireNonNull(e);
                    }
                }
                return Optional.ofNullable(result);
            }
            ```