# 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라

- hashCode 일반 규약을 어기게 되어 발생하는 문제 - Object 명세에서 발췌한 규약

    > *equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야 한다. 단, 애플리케이션을 다시 실행한다면 이 값이 달라져도 상관없다.*

    > ***equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.***

    > *equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.*

- 논리적으로 같은 객체는 같은 해시코드를 반환해야 한다.
    - equals는 물리적으로 다른 두 객체를 논리적으로 같다고 할 수 있다.

        ```java
        Map<PhoneNumber, String> m = new HashMap<>();
        m.put(new PhoneNumber(070, 867, 5309), "제니");

        m.get(new PhoneNumber(070, 867, 5309)); // null
        ```

        - hashCode를 재정의 하지 않아 논리적 동치인 두 객체가 서로 다른 해시코드를 반환한다.
    - 동치인 모든 객체에서 똑같은 해시코드를 반환

        ```java
        @Override
        public int hashCode() {
        		return 42; // 적법하지만 절대 사용하면 안되는 방법
        }
        ```

        - 모든 객체에 같은 값을 내어주므로 모든 객체가 해시테이블의 버킷 하나에 담겨 연결리스트 처럼 동작
            - 성능 : O(1) → O(n)으로 느려진다.
- 좋은 hashCode를 작성하는 간단한 요령
    - int 변수 result를 선언한 후 값 c로 초기화한다. 이때 c는 해당 객체의 첫번째 핵심 필드(equals 비교에 사용되는 필드)를 해당 필드의 해시코드로 계산한다.
    - 해당 객체의 나머지 핵심 필드 f 각각에 대해 다음 작업 수행
        - 해당 필드의 해시코드 c를 계산
            - 기본 타입 필드
                - Type.hashCode(f) 수행(Type은 wrapper 클래스)
            - 참조 타입 필드
                - equals 메서드가 이 필드의 equals를 재귀적으로 호출해 비교한다면, 이 필드의 표준형을 만들어 표준형의 hashCode를 호출한다.
            - 배열 필드
                - 핵심 원소 각각을 별도 필드처럼 다룬다.
                - 모든 원소가 핵심 원소라면 Array.hashCode 사용
        - 계산된 해시코드 c로 result를 갱신한다.

            ```java
            result = 31 * result + c;
            ```

    - result 반환
- hashCode 구현
    - 전형적인 hashCode 메서드

        ```java
        @Override
        public int hashCode() {
            int result = Short.hashCode(areaCode);
            result += 31 * result + Short.hashCode(prefix);
            result += 31 * result + Short.hashCode(lineNum);
            return result;
        }
        ```

    - Objects 클래스의 hash 정적 메서드

        ```java
        @Override
        public int hashCode() {
            return Objects.hash(areaCode, prefix, lineNum);
        }
        ```

    - 해시코드를 지연 초기화 - 스레드 안정성까지 고려해야 한다.

        ```java
        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result += 31 * result + Short.hashCode(areaCode);
                result += 31 * result + Short.hashCode(prefix);
                result += 31 * result + Short.hashCode(lineNum);
                hashCode = result;
            }
            return result;
        }
        ```

- hashCode 재정의 시 주의사항
    - 성능을 높인다고 해시코드를 계산할 때 핵심 필드를 생략하면 안 된다.
        - 속도는 빨라지지만 해시 품질이 떨어져 해시 테이블 성능이 심각하게 떨어진다.
    - hashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말자
    - 클라이언트가 이 값이 의지하지 않게 되고, 추후에 계산 방식을 바꿀 수 있다.