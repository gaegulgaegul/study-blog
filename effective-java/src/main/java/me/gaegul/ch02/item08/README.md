# 아이템 8. finalizer와 cleaner 사용을 피하라

- 객체 소멸자
    - finalizer
        - 예측할  수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요
        - 오동작, 낮은 성능, 이식성 문제의 원인
    - cleaner
        - finalizer보다 덜 위험하지만 예측할 수 없고 느리고 일반적으로 불필요
    - finalizer와 cleaner는 즉시 수행된다는 보장이 없다.
        - 객체에 접근한 후 finalizer와 cleaner가 실행되기까지 얼마나 걸리는지 모른다.
    - 자바 언어 명세는 finalizer나 cleaner의 수행 여부조차 보장하지 않는다.
        - 상태를 영구적으로 수정하는 작업에서는 절대 finalizer나 cleaner에 의존하면 안 된다.
    - finalizer를 사용하는 클래스는 **finalizer 공격**에 노출된다.
        - 생성자나 직렬화 과정에서 예외가 발생하면 생성되다 만 객체에서 악의적인 하위 클래스의 finalizer가 수행된다.
    - 사용 방법
        - 자원의 소유자가 close메서드를 호출하지 않는 것에 대비한 안전망 역할
        - 네이티브 피어와 연결된 객체
            - 일반 자바 객체가 아니라 가비지 컬렉터가 네이티브 피어와 연결된 객체를 회수하지 못 한다.
        - cleaner 사용하기

            ```java
            public class Room implements AutoCloseable {

                private static final Cleaner cleaner = Cleaner.create();

                // 청소가 필요한 자원, 정대 Room을 참조해서는 안 된다.
                private static class State implements Runnable {
                    int numJunkPiles; // 방(Room) 안의 쓰레기 수

                    public State(int numJunkPiles) {
                        this.numJunkPiles = numJunkPiles;
                    }

                    // close 메서드나 cleaner가 호출된다.
                    @Override
                    public void run() {
                        System.out.println("방 청소");
                        numJunkPiles = 0;
                    }
                }

                // 방의 상태, cleanable과 공유한다.
                private final State state;

                // cleanable 객체. 수거 대상이 되면 방을 청소한다.
                private final Cleaner.Cleanable cleanable;

                public Room(int numJunkPiles) {
                    this.state = new State(numJunkPiles);
                    this.cleanable = cleaner.register(this, state);
                }

                @Override
                public void close() throws Exception {
                    cleanable.clean();
                }
            }
            ```

            - 올바른 클라이언트 코드 예
                - try-with-resources

            ```java
            public class Adult {
                public static void main(String[] args) throws Exception {
                    try (Room myRoom = new Room(7)) {
                        System.out.println("안녕~");
                    }
                }
            }
            ```

            - 예측 할 수 없는 상황

            ```java
            public class Teenager {
                public static void main(String[] args) {
                    new Room(99);
                    System.out.println("아무렴");
                }
            }
            ```