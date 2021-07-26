# 아이템 4. 인스턴스화를 막으려거든 private 생성자를 사용하라

- 정적 메서드와 정적 필드만 담는 클래스
    - 기본 타입  값이나 배열 관련 메서드
        - java.lang.Math
        - java.util.Arrays
    - 특정 인터페이스를 구현하는 객체를 생성해주는 정적 메서드
        - java.util.collections
    - final 클래스와 관련한 메서드를 모아놓을 때 사용한다.
- 유틸리티 클래스 인스턴스화 막는 방법
    - private 생성자를 추가해 클래스의 인스턴스화를 막는다.
    - 명시적 생성자가 private이니 클래스 바깥에서 접근할 수 없다.
    - 모든 생성자는 상위 클래스의 생성자를 호출하는데 private이니 접근할 수 없다. → 상속불가

    ```java
    public class UtilityClass {
        // 기본 생성자가 만들어지는 것을 막는다.(인스턴스화 방지용)
        private UtilityClass() {
            throw new AssertionError();
        }
    		...
    }
    ```