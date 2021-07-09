# 17. 리액티브 프로그래밍

- 리액티브 매니패스토
    - 리액티브 어플리케이션과 시스템 개발의 핵심 원칙을 공식적으로 정의
        - 반응성(responsive)
            - 빠르고 일정한 예상할 수 있는 반응 시간을 제공
            - 사용자는 기대치를 통해 확신이 증가하면서 사용할 수 있는 어플리케이션을 제공할 수 있다.
        - 회복성(resilient)
            - 장애가 발생해도 시스템은 반응한다.
            - 회복성을 달성하는 다양한 기법
                - 컴포넌트 실행 복제
                - 여러 컴포넌트의 시간과 공간 분리
                    - 시간 → 발송자와 수신자가 독립적인 생명주기를 가짐
                    - 공간 → 발송자와 수신자가 다른 프로세스에서 실행됨
                - 각 컴포넌트가 비동기적으로 작업을 다른 컴포넌트에 위임
        - 탄력성(elastic)
            - 작업 부하가 발생하면 자동으로 관련 컴포넌트에 할당된 자원 수를 늘린다.
        - 메시지 주도(Message-driven)
            - 시스템을 구성하는 컴포넌트의 경계를 명확하게 정의해야 한다.
            - 비동기 메시지를 전달해 컴포넌트 끼리의 통신이 이루어진다.
                - 회복성 → 장애를 메시지로 처리
                - 탄력성 → 주고 받은 메시지의 수를 감시하고 메시지의 양에 따라 적절하게 리소스를 할당
    - 애플리케이션 수준의 리액티브
        - 주요 기능은 비동기로 작업을 수행
        - 동시, 비동기 어플리케이션 구현의 추상 수준을 높일 수 있다. 동기 블록, 경쟁 조건, 데드락 같은 저 수준의 멀티스레드 문제를 직접 처리할 필요가 없다.
        - 메인 이벤트 루프 안에는 절대 동작을 I/O 관련 블럭하지 않아야 한다.
    - 시스템 수준의 리액티브
        - 리액티브 아키텍처에서는 컴포넌트에서 발생한 장애를 고립시킴으로 문제가 다른 컴포넌트로 전파되면서 전체 시스템 장애로 이어지는 것을 막는다.
        - 회복성은 결함 허용 능력과 같은 의미
        - 시스템에 장애가 발생했을 때 문제를 격리함으로 장애에서 완전 복구되어 건강한 상태로 시스템이 돌아온다.
- 리액티브 스트림과 플로 API
    - 리액티브 스트림
        - 잠재적으로 무한의 비동기 데이터를 순서대로 그리고 블록하지 않는 역압력을 전제해 처리하는 표준 기술
        - 역압력 → 발행-구독 프로토콜에서 이벤트 스트림의 구독자가 발행자가 이벤트를 제공하는 속도보다 느린 속도로 이벤트를 소비하면서 문제가 발생하지 않도록 보장하는 장치
        - 비동기 작업을 실행하는 동안에는 완료될 때까지 다른 작업을 실행할 수 없다.
        - 비동기 API를 이용하면 하드웨어 사용률을 극대화할 수 있지만 다른 느임 다운스트림 컴포넌트에 너무 큰 부하를 줄 수 있다.
    - Flow 클래스 소개
        - Publisher
            - 항목 발행

            ```java
            @FunctionalInterface
            public interface Publisher<T> {
            	void subscribe(Subscriber<? super T> s);
            }
            ```

        - Subscriber
            - 한 개씩 또는 한 번에 여러 항목 소비

            ```java
            public interface Subscriber<T> {
            	void onSubscribe(Subscription s);
            	void onNext(T t);
            	void onError(Throwable t);
            	void onComPlete();
            }
            ```

            - 지정한 순서로 지정된 메서드 호출을 통해 발행
                - onSubscribe → onNext* → (onError | onComplete)?
                - onSubscribe: 항상 처음에 호출
                - onNext: 여러 번 호출될 수 있음
                - onComplete: 콜백을 통해 더 이상의 데이터가 없고 종료됨
                - onError: Publisher에 장애가 발생했을 때 호출
        - Subscription
            - Publisher로 항목을 발행하고 Subscriber를 통해 소비하는 과정을 관리한다.

            ```java
            public interface Subscription {
            	void request(long n);
            	void cancel();
            }
            ```

        - Processor
            - 리액티브 스트림에서 처리하는 이벤트의 변환 단계를 나타낸다.

            ```java
            public interface Processor<T, R> extends Subscriber<T>, Publisher<R> { }
            ```

    - 첫 번째 리액티브 어플리케이션 만들기
        - 리액티브 원칙을 적용해 온도를 보고하는 프로그램
        - TempInfo → 원격 온도계를 흉내낸다.

        ```java
        @AllArgsConstructor
        @ToString
        @Getter
        public class TempInfo {

            public static final Random random = new Random();

            private final String town;
            private final int temp;

            public static TempInfo fetch(String town) {
                if (random.nextInt(10) == 0 ) throw new RuntimeException("Error!");
                return new TempInfo(town, random.nextInt(100));
            }
        }
        ```

        - TempSubscription → TempInfo 스트림을 전송하는 Subscription
            - ExecutorService → 스택 오버플로가 발생하는 문제해결

        ```java
        @AllArgsConstructor
        public class TempSubscription implements Flow.Subscription {
            private final Flow.Subscriber<? super TempInfo> subscriber;
            private final String town;
            
            private static final ExecutorService executor = Executors.newSingleThreadExecutor();

            @Override
            public void request(long n) {
                executor.submit(() -> {
                    for (long i = 0L; i < n; i++) {
                        try {
                            subscriber.onNext(TempInfo.fetch(town));
                        } catch (Exception e) {
                            subscriber.onError(e);
                            break;
                        }
                    }
                });
            }

            @Override
            public void cancel() {
                subscriber.onComplete();
            }
        }
        ```

        - TempSubscriber → 레포트를 관찰하면서 각 도시에 설치된 센서에서 보고한 온도 스트림을 출력

        ```java
        public class TempSubscriber implements Flow.Subscriber<TempInfo> {

            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(TempInfo tempInfo) {
                System.out.println(tempInfo);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
        }
        ```

        ```java
        public class Main {
            public static void main(String[] args) {
                getTemperatures("New York").subscribe(new TempSubscriber());
            }

            public static Flow.Publisher<TempInfo> getTemperatures(String town) {
                return subscriber -> subscriber.onSubscribe(
                        new TempSubscription(subscriber, town)
                );
            }

        }
        ```

    - Processor로 데이터 변환하기
        - 화씨로 제공된 데이터를 섭씨로 변환해 다시 방출

        ```java
        public class TempProcessor implements Flow.Processor<TempInfo, TempInfo> {

            private Flow.Subscriber<? super TempInfo> subscriber;

            @Override
            public void subscribe(Flow.Subscriber<? super TempInfo> subscriber) {
                this.subscriber = subscriber;
            }

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(TempInfo temp) {
                subscriber.onNext(new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
            }

            @Override
            public void onError(Throwable throwable) {
                subscriber.onError(throwable);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }
        }

        public class Main {
            ...

            public static Flow.Publisher<TempInfo> getCelsiusTemperatures(String town) {
                return subscriber -> {
                    TempProcessor processor = new TempProcessor();
                    processor.subscribe(subscriber);
                    processor.onSubscribe(new TempSubscription(processor, town));
                };
            }
        }
        ```

- 리액티브 라이브러리 RxJava 사용하기
    - 넷플릭스의 Reactive Extensions 프로젝트
    - Observable 만들고 사용하기
        - Observable.just
            - 한 개 이상의 요소를 이용해 이를 방출하는 Observable로 변환

            ```java
            Observable<String> strings = Observable.just("first","second");
            // onNext("first"), onNext("second"), onComplete() 순서로 메시지를 받는다.
            ```

        - Observable.interval
            - 0에서 1초 간격으로 long형식의 값을 무한으로 증가시키며 값을 방출한다.

            ```java
            Observable<Long> onePerSec = Observable.interval(1, TimeUnit.SECOND);
            ```

        - Observer 인터페이스

            ```java
            public interface Observer<T> {
            	void onSubscribe(Disposable d);
            	void onNext(T t);
            	void onError(Throwable t)
            	void onComplete();
            }
            ```

        - TempObservable → 1초마다 한 개의 온도를 방출 Observable

            ```java
            public class TempObservable {

                public static Observable<TempInfo> getTemperature(String town) {
                    return Observable.create(emitter ->
                            Observable.interval(1, TimeUnit.SECONDS)
                                .subscribe(i -> {
                                    if (!emitter.isDisposed()) {
                                        emitter.onComplete();
                                    } else {
                                        try {
                                            emitter.onNext(TempInfo.fetch(town));
                                        } catch (Exception e) {
                                            emitter.onError(e);
                                        }
                                    }
                                }));
                }
            }
            ```

        - TempObserver → 수신한 온도를 출력하는 Observer

            ```java
            public class TempObserver implements Observer<TempInfo> {
                @Override
                public void onSubscribe(@NonNull Disposable disposable) {

                }

                @Override
                public void onNext(@NonNull TempInfo tempInfo) {
                    System.out.println(tempInfo);
                }

                @Override
                public void onError(@NonNull Throwable throwable) {
                    System.out.println("Got problem: " + throwable.getMessage());
                }

                @Override
                public void onComplete() {
                    System.out.println("Done!");
                }
            }
            ```

        - 뉴욕의 온도 출력

            ```java
            public class Main {
                public static void main(String[] args) {
                    Observable<TempInfo> observable = getTemperature("New York");
                    observable.blockingSubscribe(new TempObserver());
                }
            }
            ```

- Observable을 변환하고 합치기
    - Observable에 map을 이용해 화씨를 섭씨로 변환

    ```java
    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town).map(temp -> new TempInfo(temp.getTown(), (temp.getTemp() - 32) * 5 / 9));
    }
    ```

    - 영하 온도만 거르기

    ```java
    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getTemperature(town).filter(temp -> temp.getTemp() < 0);
    }
    ```

    - 한 개 이상 도시의 온도 보고를 합친다.

    ```java
    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(Arrays.stream(towns)
                                    .map(TempObservable::getCelsiusTemperature)
                                    .collect(toList()));
    }
    ```