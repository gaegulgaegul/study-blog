# 아이템 29. 이왕이면 제네릭 타입으로 만들라

- Object 기반 Stack

    ```java
    public class Stack {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public Stack() {
            elements = new Object[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(Object e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public Object pop() {
            if (size == 0)
                throw new EmptyStackException();
            // 꺼낸 객체를 형변환하면서 런타임 시 에러가 발생할 위험이 있다.
            Object result = elements[--size];
            elements[size] = null; // 다 쓴 객체 참조 해제
            return result;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }

    }
    ```

- Object → 제네릭으로 변환한 Stack

    ```java
    public class Stack<E> {
        private E[] elements;
        private int size = 0;
        private static final int DEFAULT_INITIAL_CAPACITY = 16;

        public Stack() {
            // 컴파일 오류가 발생한다.
            // 실체화 불가 타입으로 배열을 만들 수 없다.
            elements = new E[DEFAULT_INITIAL_CAPACITY];
        }

        public void push(E e) {
            ensureCapacity();
            elements[size++] = e;
        }

        public E pop() {
            if (size == 0)
                throw new EmptyStackException();
            E result = elements[--size];
            elements[size] = null; // 다 쓴 객체 참조 해제
            return result;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }

    }
    ```

    - 위 문제의 해결책
        - 제네릭 배열 생성 금지를 우회한다.
            - Object 배열을 생성하고 제네릭 배열로 형변환한다.
            - `elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];`
                - 컴파일 오류가 나진 않지만 type safe하지 않다.
                    - 비검사 형변환
                        - 프로그램의 타입 안전성을 해치지 않음을 스스로 확인해야 한다.
                        - 배열 elements는 private에 저장되고, 클라이언트로 반환되거나 다른 메서드에 전달되는 일이 없다.
                        → **비검사 형변환은 안전하다.**
                - 경고가 발생하지만 @SuppressWarnings("unchecked") 어노테이션을 통해 숨긴다.

                ```java
                // 배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
                // 따라서 타입 안전성을 보장하지만,
                // 이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
                @SuppressWarnings("unchecked")
                public Stack() {
                    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
                }
                ```

            - 배열의 런타임 타입이 컴파일 타임 타입과 달라 힙 오염을 일으킨다.
        - elements 필드의 타입을 E[] → Object[]로 바꾼다.
            - E는 실체화 불가 타입이므로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 수 없다.
            → 비검사 형변환을 통해 직접 증명해야 한다.

            ```java
            public E pop() {
                if (size == 0)
                    throw new EmptyStackException();
                // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
                @SuppressWarnings("unchecked") E result = (E) elements[--size];
                elements[size] = null; // 다 쓴 객체 참조 해제
                return result;
            }
            ```

- 배열보다는 리스트를 우선하라
    - 항상 제네릭 타입 안에서 리스트를 사용하는게 가능하지도 좋지도 않다.
        - ArrayList는 배열을 사용해 구현된다.
        - HashMap의 성능을 높일 때도 배열을 사용한다.
    - 제네릭 타입은 타입 매개변수에 제약이 없다.
        - `Stack<Object>, Stack<int[]>, Stack<List<String>>, Stack`
        - 기본 타입은 제네릭 타입으로 사용할 수 없다.
        → 박싱된 기본 타입을 사용한다.
    - 타입 매개변수에 제약을 두는 제네릭 타입
        - `class DelayQueue<E extends Delayed> implements BlockingQueue<E>`
            - `java.util.concurrent.Delayed` 타입의 하위 타입만 받을 수 있다.
            - 위와 같은 타입 매개변수 E를 한정적 타입 매개변수라 한다.
            - 모든 타입은 자기 자신의 하위 타입