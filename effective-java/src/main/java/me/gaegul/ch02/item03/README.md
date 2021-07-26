# 아이템 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라

- 싱글턴
    - 인스턴스를 오직 하나만 생성할 수 있는 클래스
    - 클래스를 싱글턴으로 만들면 클라이언트에서 사용할 때 테스트하기가 어렵다.
    → 타입을 인터페이스로 정의하여 인터페이스로 구현한 싱글턴이 아니면 mock 구현으로 대체할 수 없음
    - 만드는 방식
        - public static final 필드 방식의 싱글턴
            - Elvis.INSTANCE가 초기화 될 때 private 생성자는 한번만 호출된다.
            - protected or public 생성자가 없으므로 인스턴스가 전체 시스템에서 하나임을 보장
            - 리플렉션 API를 이용해 생성자를 호출하려 할 경우 생성자를 수정하여 두번째 객체가 생성되려 할 때 예외를 던지게 한다.
            - 장점
                - 해당 클래스가 싱글턴임이 API에 명확하게 들어난다.
                - 간결하다.

            ```java
            public class Elvis {
                public static final Elvis INSTANCE = new Elvis();

                private Elvis() {...}
                
                public void leaveTheBuilding() {
                    ...
                }
            }
            ```

        - 정적 팩터리 방식의 싱글턴
            - `Elvis.getInstance()`는 항상 같은 객체의 참조를 반환한다.
            - 장점
                - API를 바꾸지 않고 싱글턴이 아니게 변경할 수 있다.
                - 정적 팩러리를 제네릭 싱글턴 팩터리로 만들 수 있다.
                - 정적 팩터리의 메서드 참조를 Supplier<T>로 사용할 수 있다.

            ```java
            public class Elvis {
                private static final Elvis INSTANCE = new Elvis();

                private Elvis() {
                }
                
                public static Elvis getInstance() {
                    return INSTANCE;
                }
                
                public void leaveTheBuilding() {
                    
                }
            }
            ```

    - 싱글턴 클래스를 직렬화
        - Serializable을 선언한다.
        - 모든 인스턴스 필드를 transient(일시적)으로 선언한다.
        - readResolve 메서드를 제공한다.
        → 직렬화된 인스턴스를 역직렬화할 때마다 새로운 클래스가 만들어지는 것을 방지한다.
- 열거 타입 방식의 싱글턴
    - public 필드 방식과 비슷하지만 더 간결하다.
    - 추가 노력 없이 직렬화할 수 있다.
    - 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 완전히 제어할 수 있다.
    - 대부분 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.
    - 싱글턴이 Enum 외 클래스를 상속하려 한다면 사용할 수 없다.

    ```java
    public enum Elvis {
        INSTANCE;
        
        public void leaveTheBuilding() {}
    }
    ```