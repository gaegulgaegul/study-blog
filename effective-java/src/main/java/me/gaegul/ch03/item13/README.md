# 아이템 13. clone 재정의는 주의해서 진행하라

- Cloneable 인터페이스
    - Object의 clone 메서드 동작방식을 결정한다.
    - 인스턴스에서 clone 메서드 호출 시
        - 해당 인터페이스를 구현한 클래스 → 객체 필드들 모두 복사한 객체 생성
        - 해당 인터페이스를 구현하지 않은 클래스 → `CloneNotSupportedException` 발생
    - 상위 클래스(Object)에 정의된 protected 메서드의 동작 방식을 변경한 것
    - 실무에서 Cloneable을 구현한 클래스는 clone 메서드를 public 으로 제공하면 사용자는 당연히 복제가 제대로 이뤄지리라 기대한다.
- clone 메서드 일반 규약

    > `x.clone() != x`  
    → 참이다. 원본 객체와 복사된 객체를 서로 다르다.
    >
    >`x.clone().getClass() == x.getClass()`  
    → 참이다. 하지만 반드시 만족해야 하는 것은 아니다.
    >
    >`x.clone().equals(x)`  
    → 참이지만 필수는 아니다.
    >
    >`x.clone().getClass() == x.getClass()`  
    → 반환하는 객체는 super.clone을 호출해야 참이다.  
    → 반환된 객체와 원본 객체는 독립적이여야 한다. 이를 만족하려면 super.clone으로 얻은 객체의 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.

    - clone 메서드가 super.clone이 아닌 생성자를 호출해 얻은 인스턴스를 반환해도 컴파일러는 문제가 되지 않지만 하위 클래스에서 super.clone을 호출한다면 상위 클래스 타입 인스턴스를 반환하여 문제가 된다.
- clone 메서드 재정의
    - 자바가 공변 반환 타이핑을 지원하여 재정의한  메서드의 반환 타입은 상위 클래스의 메서드가 반환하는 타입일 수 있다.

        > *공변 반환 타이핑  
        → 자신이 상속받은 부모 객체로 타입을 변화시킬 수 있다라는 것*

    - Cloneable을 상속받아 구현한다.
    - Object의 clone 메서드는 ClonNotSupportedException(Check-Exception)을 던지도록 선언되어 있다.
    → try-catch로 감싸 예외 처리해야 한다.

    ```java
    public class Foo implements Cloneable {
        private int number;

        public Foo() {
            System.out.println("Foo Constructor");
        }

        public Foo(int number) {
            System.out.println("Foo Constructor");
            this.number = number;
        }

        @Override
        protected Object clone() {
            try {
                System.out.println("Foo clone");
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException();
            }
        }

        public static void main(String[] args) {
            Foo originalFoo = new Foo(1);
            Object cloneFoo = originalFoo.clone();
        }
    }
    ```

- Stack 클래스 복제

    ```java
    public class Stack implements Cloneable {

        ... 중략

        @Override
        protected Stack clone() {
            try {
                System.out.println("Stack clone");
                Stack result = (Stack) super.clone();
                result.elements = elements.clone();
                return result;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        public static void main(String[] args) {
            Stack stack = new Stack();
            IntStream.rangeClosed(1,10).forEach(i -> stack.push(i));

            Stack cloneStack = stack.clone();
        }
    }
    ```

    - 배열의 clone은 런타임 타입과 컴파일타임 타입 모두가 원본 배열과 똑같은 배열을 반환, 배열 복사 시에 clone 권장
    - 배열 필드가 final이라면 clone은 동작하지 않는다. clone 메서드를 사용하려면 일부 필드는 final 제거
- Cloneable을 구현하는 클래스는 clone을 재정의 해야 한다.
    - 접근 제한자는 public, 반환 타입은 클래스 자신으로 변경
    - super.clone을 호출한 후 필요한 필드를 수정
        - 깊은 구조에 숨어 있는 모든 가변 객체를 복사
        - 복제본이 가진 객체 참조 모두가 복사된 객체를 가리키게 한다.
    - Cloneable을 구현하지 않은 상황에서는 복사 생성자와 복사 팩터리를 통해 객체 복사 방식을 제공한다.