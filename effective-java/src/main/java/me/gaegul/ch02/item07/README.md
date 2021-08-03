# 아이템 7. 다 쓴 객체 참조를 해제하라

- 메모리 누수가 일어나는 위치는 어디인가?

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
            if (size == 0) {
                throw new EmptyStackException();
            }
            return elements[--size];
        }

        /*
         * 원소를 위한 공간을 적어도 하나 이상 확보한다.
         * 배열 크기를 늘려야 할 때마다 대략 두 배씩 늘린다.
         */
        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }
    }
    ```

    - 스택이 커졌다가 줄어들었을 때 스택에서 꺼내진 객체들을 가비지 컬렉터가 회수하지 않는다.
    - 회수되지 않은 객체는 잠재적으로 성능에 악영향을 준다.
- 해법
    - 해당 참조를 다 썻을 때 null 처리(참조 해제)하면 된다.

    ```java
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
    ```

- null을 통한 참조 해제
    - NullPointerException이 발생하여 조기에 프로그램 오류 처리할 수 있다.
    - 객체가 비활성 영역이 되는 순간 프로그래머가 null 처리를 해야 한다.
- 메모리 누수 요인
    - 자기 메모리를 직접 관리하는 클래스
        - 원소를 다 사용한 즉시 원소가 참조한 객체들을 다 null 처리
    - 캐시
        - 캐시 외부에서 key를 참조하는 동안만 엔트리가 살아 있는 캐시가 필요한 상황
            - WeakHashMap을 통해 캐시 생성
            - 캐시 엔트리의 유효 기간은 시간이 지날수록 가치를 떨어뜨리는 방식을 주로 사용
                - 백그라운드 스레드 활용
                - 새 엔트리 추가 시 부수작업 진행
                    - LinkedHashMap은 removeEldestEntry 메서드 사용
            - 더 복잡한 캐시는 java.lang.ref 패키지를 통해 직접 활용
    - 리스너 또는 콜백
        - 콜백을 등록하고 해지하지 않는다면 콜백은 계속 쌓인다.
        - 약한 참조로 저장하면 가비지 컬렉터가 즉시 수거한다.
            - WeakHashMap의 키로 저장