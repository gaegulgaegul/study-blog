# 아이템 14. Comparable을 구현할지 고려하라

- CompareTo
    - 단순 동치성 비교, 순서 비교, 제네릭하다.
    - Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서가 있음을 의미
    - 일반규약

        > 객체 비교(a: 기준 객체, b: 비교 객체)  
        `a < b` → 음의 정수 반환  
        `a == b` →  0 반환  
        `a > b` → 양의 정수 반환  
        >
        > 모든 x, y에 대해 `sgn(x.compareTo(y)) == -sgn(y.compareTo(x))`  
        → 항상 참이다.
        >
        > `(x.compareTo(y)) > 0 && y.compareTo(z) > 0`  
        → `x.compareTo(z)`도 참이다.  
        → 추이성을 보장해야 한다.
        >
        > 모든 z에 대해 `x.compareTo(y) == 0`이면 `sgn(x.compareTo(z)) == sgn(y.compareTo(z))`  
        > 
        > `(x.compareTo(y) == 0) == (x.equals(y))`  
        → 필수는 아니지만 지키는게 좋다. 지키지 않는다면 그 이유를 명시해야 한다.

        - 모든 객체에 전역 동치관계를 부여하는 equals와 달리 compareTo는 타입이 다른 객체를 신경쓰지 않는다. 다른 객체가 주어지면 `ClassCastException` 발생
- compareTo 메서드 작성 요령
    - Comparable은 제네릭 인터페이스
        - compareTo 메서드의 인수 타입은 컴파일타임에 정해진다.
        - **입력 인수의 타입을 확인하거나 형변환이 필요 없다.**

        ```java
        public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString> {

            private String s;

        		... 중략

            @Override
            public int compareTo(CaseInsensitiveString cis) {
                return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
            }
        }
        ```

    - 동치를 비교하는게 아니라 순서를 비교한다. 객체 참조 필드를 비교하려면 compareTo를 재귀적으로 호출한다.
    - 클래스의 가장 핵심적인 필드부터 비교해나간다. 비교결과가 0이 아니라면 순서가 결정되니 바로 반환한다.

        ```java
        public final class PhoneNumber implements Comparable<PhoneNumber>{

            private final short areaCode, prefix, lineNum;

        		... 중략

        		@Override
            public int compareTo(PhoneNumber pn) {
                int result = Short.compare(areaCode, pn.areaCode);
                if (result == 0) {
                    result = Short.compare(prefix, pn.prefix);
                    if (result == 0) {
                        result = Short.compare(lineNum, pn.lineNum);
                    }
                }
                return result;
            }
        }
        ```

        - 성능 개선

        ```java
        public final class PhoneNumber implements Comparable<PhoneNumber>{

            private static final Comparator<PhoneNumber> COMPARATOR =
                    comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);

        		... 중략

        		@Override
            public int compareTo(PhoneNumber pn) {
                return COMPARATOR.compare(this, pn);
            }
        }
        ```

- 객체 참조용 비교자 생성 메서드
    - 해시코드 값의 차를 기준으로 하는 비교자 - 추이성을 위배한다.

        ```java
        static Comparator<Object> hashCodeOrder = new Comparator<>() {
        		public int compare(Object o1, Object o2) {
        				return o1.hashCode() - o2.hashCode();
        		}
        };
        ```

        - 위와 같은 방식은 정수 오버플로우를 일으키거나 부동소수점 계산 방식에 따른 오류가 발생할 수 있다.
    - 정적 compare 메서드를 활용한 비교자

        ```java
        static Comparator<Object> hashCodeOrder = new Comparator<>() {
        		public int compare(Object o1, Object o2) {
        				return Integer.compare(o1.hashCode(), o2.hashCode());
        		}
        };
        ```

    - 비교자 생성 메서드를 활용한 비교자

        ```java
        static Comparator<Object> hashCodeOrder =
        				Comparator.comparingInt(o -> o.hashCode());
        ```