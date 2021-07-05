# 15. CompletableFuture와 리액티브 프로그래밍 컨셉의 기초

- 동시성을 구현하는 자바 지원의 진화
    - 스레드와 높은 수준의 추상화
        - 병렬 스트림의 반복은 명시적으로 스레드를 사용하는 것에 비해 높은 수준의 개념, 코드 복잡성을 줄인다.
    - Executor와 스레드 풀
        - 자바 5의 Executor 프레임워크는 태스크 제출과 실행 을 분리할 수 있는 기능 제공
        - 스레드의 문제
            - 운영체제 스레드의 숫자는 제한되어 있다. 자바에서 운영체제가 지원하는 스레드 수 이상을 사용하면 크래시가 발생할 수 있다.
        - 스레드 풀 그리고 스레드 풀이 더 좋은 이유
            - ExecutorService는 태스크를 제출하고 나중에 결과를 수집할 수 있는 인터페이스 제공
            - 스레드 풀에서 사용하지 않은 스레드로 제출된 태스크를 먼저 온 순서대로 실행
            - 하드웨어에 맞는 수의 태스크를 유지하고 수 천개의 태스크를 스레드 풀에 오버헤드 없이 제출할 수 있다.
        - 스레드 풀 그리고 스레드 풀이 더 나쁜 이유
            - 스레드가 잠을 자거나 I/O를 기다리거나 네트워크 연결을 기다리는 태스크가 있다면 데드락 상황이 발생될 수 있다.
            - 중요한 코드를 실행하는 스레드가 죽지 않도록 프로그램을 종료하기 전 스레드 풀을 종료해야 한다.
    - 스레드의 다른 추상화 : 중첩되지 않은 메서드 호출
        - 스레드 생성과 join()이 한 쌍으로 중첩된 메서드 호출 내에 추가되는 엄격한 포크/조인
        - 스레드 실행은 메서드를 호출한 다음의 코드와 동시에 실행되므로 데이터 경쟁 문제를 주의해야 한다.
        - 실행 중이던 스레드가 종료되지 않은 상황에 main() 메서드가 반환되면 안전하지 못 하다.
    - 스레드에 무엇을 바라는가?
        - 병렬성을 극대화 하는 것, 프로그램을 작은 태스크 단위로 구조화하는 것이 목표
- 동기 API와 비동기 API
    - 동기 API
        - Thread

        ```java
        public class ThreadExample {
            public static void main(String[] args) throws InterruptedException {
                int x = 1337;
                Result result = new Result();

                Thread t1 = new Thread(() -> { result.left = f(x); });
                Thread t2 = new Thread(() -> { result.right = g(x); });
                t1.start();
                t2.start();
                t1.join();
                t2.join();
                System.out.println(result.left + result.right);
            }

            private static class Result {
                private int left;
                private int right;
            }
        }
        ```

        - ExecutorService

        ```java
        public static int f(int x) {
            return x * 1;
        }

        public static int g(int x) {
            return x * 2;
        }

        public class ExecutorServiceExample {
            public static void main(String[] args) throws ExecutionException, InterruptedException {
                int x = 1337;

                ExecutorService executorService = Executors.newFixedThreadPool(2);
                Future<Integer> y = executorService.submit(() -> f(x));
                Future<Integer> z = executorService.submit(() -> g(x));
                System.out.println(y.get() + z.get());

                executorService.shutdown();
            }
        }
        ```

        - 위 두 가지 방식에는 코드를 실행하는데 문제는 없지만 코드가 복잡하다는 문제가 있다.
    - Future 형식 API
        - 

        ```java
        public static Future<Integer> ff(int x) {
            return new CompletableFuture<Integer>().completeAsync(() -> Integer.valueOf(f(x)));
        }

        public static Future<Integer> gg(int x) {
            return new CompletableFuture<Integer>().completeAsync(() -> Integer.valueOf(g(x)));
        }
        ```

        - 결과를 Future로 반환한다.
        - 작지만 적당한 단위의 태스크로 나누는 것이 좋다.
    - 리액티브 형식 API

        ```java
        public class CallbackStyleExample {
            public static void main(String[] args) {
                int x = 1337;
                Result result = new Result();
                
                fr(x, y -> {
                    result.left = y;
                    System.out.println(result.left + result.right);
                });

                gr(x, z -> {
                    result.right = z;
                    System.out.println(result.left + result.right);
                });

            }
            
            private static class Result {
                private int left;
                private int right;
            }
            
            private static void fr(int x, IntConsumer dealWithResult) {
                dealWithResult.accept(f(x));
            }

            private static void gr(int x, IntConsumer dealWithResult) {
                dealWithResult.accept(g(x));
            }
        }
        ```

        - 콜백 형식의 프로그래밍을 한다.
    - 잠자기(그리고 기타 블로킹 동작)는 해로운 것으로 간주
        - sleep 메서드를 사용하거나 태스크가 기다리는 일을 만들지 않는 것이 중요하다.

        ```java
        public class ScheduledExecutorServiceExample {
            public static void main(String[] args) {
                ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                work1();
                scheduledExecutorService.schedule(ScheduledExecutorServiceExample::work2, 10, TimeUnit.SECONDS);
                scheduledExecutorService.shutdown();
            }

            public static void work1() {
                System.out.println("Hello from Work1!");
            }

            public static void work2() {
                System.out.println("Hello from Work2!");
            }
        }
        ```

        - 스레드 풀을 통해 태스크 앞뒤 두 부분으로 나누고 블록되지 않을 때만 뒷부분을 자바가 스케줄링 하도록 요청한다.
        - 스레드 자원을 점유하지 않고 work1이 실행되는 동안 다른 작업이 실행될 수 있도록 허용한다.
    - 비동기 API에서 예외는 어떻게 처리하는가?
        - 리액티브 형식의 비동기 API에서는 return 대신 기존 콜백이 호출되므로 예외가 발생할 때 실행될 추가 콜백을 만들어 인터페이스를 바꿔야 한다.
        - 자바 9 플로 API에서는 여러 콜백을 한 객체로 감싼다 → Subscriber<T>

        ```java
        void onComplete(); // 더 이상 처리할 데이터가 없을 때
        void onError(Throwable throwable); // 도중에 에러가 발생했을 때
        void onNext(); // 값이 있을 떄
        ```

- 박스와 채널 모델
    - 생각과 코드를 구조화 할 수 있다. 대규모 시스템 구현의 추상화 수준을 높일 수 있다.
    - 대규모 시스템에서 많은 수의 Future의 get()을 감당할 수 없을 때 CompletableFuture와 콤비네이터(combinators)를 이용한다.
    - 두 Function이 있을 때 `compose(), andThen()` 등을 이용해 다른 Function을 얻는다.
- CompletableFuture와 콤비네이터를 이용한 동시성
    - CompletableFuture는 실행할 코드 없이 Future를 만들 수 있도록 허용한다.
    - complete() 메서드를 이용해 나중에 어떤 값을 다른 스레드가 완료하도록 허용한다.
    - get()으로 값을 얻을 수 있도록 허용한다.

    ```java
    public class CFComplete {
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            int x = 1337;

            CompletableFuture<Integer> a = new CompletableFuture<>();
            executorService.submit(() -> a.complete(f(x)));
            int b = g(x);
            System.out.println(a.get() + b);

            executorService.shutdown();
        }
    }
    ```

    - CompletableFuture<T>에 thenCombine 메서드를 사용해 두 연산 결과를 더 효과적으로 한다.

    ```java
    public class CFCombine {
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            int x = 1337;

            CompletableFuture<Integer> a = new CompletableFuture<>();
            CompletableFuture<Integer> b = new CompletableFuture<>();
            CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);
            executorService.submit(() -> a.complete(f(x)));
            executorService.submit(() -> b.complete(g(x)));

            System.out.println(c.get());
            executorService.shutdown();
        }
    }
    ```

    - Future a와 Future b의 결과를 알지 못한 상태에서 then Combine은 두 연산이 끝났을 때 스레드 풀에서 실행된 연산을 만든다.
    - 결과를 추가하는 c 연산은 다른 두 작업이 끝날 때까지 스레드에서 실행되지 않는다.
- 발행 - 구독 그리고 리액티브 프로그래밍
    - 플로 API
        - **구독자**가 구독할 수 있는 **발행자**
        - 이 연결을 **구독(subscription)**이라 한다.
        - 이 연결을 이용해 **메시지(또는 이벤트)**를 전송한다.
    - 두 플로를 합치는 예제
        - 두 정보 소스로 부터 발생하는 이벤트를 합쳐서 다른 구독자가 볼 수 있도록 발행
        - Publisher<T> → 통신할 구독자를 만든다.
        - Subscriber<T> → onNext 메서드를 통해 정보를 전달한다.

        ```java
        public class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {

            private int value = 0;
            private String name;
            private List<Subscriber> subscribers = new ArrayList<>();

            public static void main(String[] args) {
                SimpleCell c3 = new SimpleCell("C3");
                SimpleCell c2 = new SimpleCell("C2");
                SimpleCell c1 = new SimpleCell("C1");

                c1.subscribe(c3);

                c1.onNext(10);
                c2.onNext(20);
            }

            public SimpleCell(String name) {
                this.name = name;
            }

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscribers.add(subscriber);
            }

            private void notifyAllSubscribers() {
                subscribers.forEach(subscriber -> subscriber.onNext(this.value));
            }

            @Override
            public void onSubscribe(Subscription subscription) {

            }

            @Override
            public void onNext(Integer newValue) {
                this.value = newValue;
                System.out.println(this.name + ":" + this.value);
                notifyAllSubscribers();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        }
        ```

    - 조금 더 실용적인 예제

        ```java
        public class ArithmeticCell extends SimpleCell {
            private int left;
            private int right;

            public static void main(String[] args) {
                test1();
                test2();
            }

            private static void test1() {
                ArithmeticCell c3 = new ArithmeticCell("C3");
                SimpleCell c2 = new SimpleCell("C2");
                SimpleCell c1 = new SimpleCell("C1");

                c1.subscribe(c3::setLeft);
                c2.subscribe(c3::setRight);

                c1.onNext(10);
                c2.onNext(20);
                c1.onNext(15);
            }

            private static void test2() {
                ArithmeticCell c5 = new ArithmeticCell("C5");
                ArithmeticCell c3 = new ArithmeticCell("C3");

                SimpleCell c4 = new SimpleCell("C4");
                SimpleCell c2 = new SimpleCell("C2");
                SimpleCell c1 = new SimpleCell("C1");

                c1.subscribe(c3::setLeft);
                c2.subscribe(c3::setRight);

                c3.subscribe(c5::setLeft);
                c4.subscribe(c5::setRight);

                c1.onNext(10);
                c2.onNext(20);
                c1.onNext(15);
                c4.onNext(1);
                c4.onNext(3);
            }

            public ArithmeticCell(String name) {
                super(name);
            }

            public void setLeft(int left) {
                this.left = left;
                onNext(left + this.right);
            }

            public void setRight(int right) {
                this.right = right;
                onNext(right + this.left);
            }
        }
        ```

    - 압력, 역압력
        - 압력
            - 시간이 지날수록 많은 데이터가 onNext로 전달되는 경우를 의미한다.
        - 역압력
            - 압력 상황에서 데이터를 제한하는 역압력이 필요한다.

            ```java
            interface SubScription {
            	void cancel();
            	void request(long n);
            }
            ```

            - SubScription는 Subscriber와 Publisher와 통신할 수 있는 메서드를 포함한다.
            - 콜백을 통한 역방향 소통
            - Publisher는 Subscription 객체를 만들어 Subscriber로 전달하면 Subscriber는 이를 이용해 Publisher로 정보를 보낼 수 있다.