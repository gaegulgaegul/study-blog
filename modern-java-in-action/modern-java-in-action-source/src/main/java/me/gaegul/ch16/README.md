# 16. CompletableFuture : 안정적 비동기 프로그래밍

- Future의 단순 활용
    - 다른 작업을 처리하다가 시간이 오래 걸리는 작업의 결과가 필요한 시점이 되었을 때 Future의 get 메서드로 결과를 가져온다.
    - get 메서드를 호출했을 때 이미 계산이 완료되어 결과가 준비되었다면 즉시 반환하지만 준비되어 있지 않다면 스레드를 블록시킨다.
    - Future의 제한
        - 두개의 비동기 계산 결과를 하나로 합친다. 두 개 계산의 결과는 서로 독립적이며 또는 두번째 결과가 첫번째 결과에 의존할 수 있다.
        - Future 집합이 실행하는 모든 태스크의 완료를 기다린다.
        - Future 집합에서 가장 빨리 완료되는 태스크를 기다렸다가 결과를 얻는다.
        - 프로그램적으로 Future를 완료시킨다.
        - Future 완료 동작에 반응한다.
    - CompletableFuture로 비동기 어플리케이션 만들기
        - 고객에게 비동기 API를 제공하는 방법
        - 동기 API를 사용해야할 때 코드를 비블록으로 만드는 방법
            - 두개의 비동기 동작을 파이프라인으로 만드는 방법
            - 두개의 동작 결과를 하나의 비동기 계산으로 합치는 방법
        - 비동기 동작의 완료에 대응하는 방법
- 비동기 API 구현
    - 동기 메서드를 비동기 메서드로 전환

        ```java
        public double getPrice(String product) {
            return calculatePrice(product);
        }

        public Future<Double> getPriceAsync(String product) {
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();
            new Thread(() -> {
                double price = calculatePrice(product);
                futurePrice.complete(price);
            }).start();
            return futurePrice;
        }
        ```

        - 비동기 계산 결과를 포함하는 CompletableFuture 인스턴스를 만든다.
        - 다른 스레드를 통해 계산 결과를 만든다.
    - 에러 처리 방법
        - 블록 문제가 발생할 수 있는 상황에서는 타임아웃을 하는게 좋다.

        ```java
        public Future<Double> getPriceAsync(String product) {
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();
            new Thread(() -> {
                try {
                    double price = calculatePrice(product);
                    futurePrice.complete(price);
                } catch (Exception ex) {
                    futurePrice.completeExceptionally(ex);
                }
            }).start();
            return futurePrice;
        }
        ```

        - 팩토리 메서드 supplyAsync로 CompletableFuture 만들기

        ```java
        public Future<Double> getPriceAsync(String product) {
            return CompletableFuture.supplyAsync(() -> calculatePrice(product));
        }
        ```

- 비블록 코드 만들기
    - 원하는 제품의 가격을 검색하는 메서드 → findPrices

        ```java
        public class BestPriceFinder {    
            private final List<Shop> shops = Arrays.asList(
                    new Shop("BestPrice"),
                    new Shop("LetsSaveBig"),
                    new Shop("MyFavoriteShop"),
                    new Shop("BuyItAll")
            );
            
            public List<String> findPricesSequential(String product) {
                return shops.stream()
                        .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                        .collect(toList());
            }
        }

        >> sequential done in 4019 msecs
        ```

    - 병렬 스트림으로 요청 병렬화하기

        ```java
        public List<String> findPricesParallel(String product) {
            return shops.parallelStream()
                    .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                    .collect(toList());
        }

        >> parallel done in 1007 msecs
        ```

    - CompletableFuture로 비동기 호출 구현하기

        ```java
        public List<String> findPricesFuture(String product) {
            List<CompletableFuture<String>> priceFuture = shops.stream()
                    .map(shop -> CompletableFuture.supplyAsync(
                            () -> shop.getName() + "price is " + shop.getPrice(product)
                    ))
                    .collect(toList());

            return priceFuture.stream()
                    .map(CompletableFuture::join)
                    .collect(toList());
        }

        >> completableFuture done in 1007 msecs
        ```

        - CompletableFuture를 통해 각각 가격을 비동기적으로 계산하고 join 메서드를 통해 모든 동작이 끝날 때까지 기다린다.
    - 커스텀 Executor 사용하기
        - 어플리케이션에 실제 필요한 작업량을 고려한 풀에서 관리하는 스레드 수에 맞는 Executor
        - 데몬 스레드 생성
            - 자바에서 일반 스레드가 실행 중이면 자바 프로그램은 종료되지 않는다.

            ```java
            private final Executor executor = Executors.newFixedThreadPool(
                        Math.min(shops.size(), 100),
                        r -> {
                            Thread t = new Thread(r);
                            t.setDaemon(true);
                            return t;
                        });

            public List<String> findPricesFutureWithDaemon(String product) {
                List<CompletableFuture<String>> priceFuture = shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(() -> shop.getName() + "price is " + shop.getPrice(product), executor))
                        .collect(toList());

                return priceFuture.stream()
                        .map(CompletableFuture::join)
                        .collect(toList());
            }

            >> composed CompletableFuture with Daemon done in 1005 msecs
            ```

            - 어플리케이션의 특성에 맞는 Executor를 만들어 CompletableFuture를 활용하는 것이 좋다.
- 비동기 작업 파이프라인 만들기
    - 할인 서비스에서 서로 다른 할인율을 제공하는 코드

        ```java
        public class Discount {
            public enum Code {
                NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
                
                private final int percentage;
                
                Code(int percentage) {
                    this.percentage = percentage;
                }
            }
        }
        ```

    - 할인 서비스 구현
        - 상점에서 제공한 문자열 파싱

        ```java
        @AllArgsConstructor
        @Getter
        public class Quote {
            private final String shopName;
            private final double price;
            private final Discount.Code discountCode;

            public static Quote parse(String s) {
                String[] split = s.split(":");
                String shopName = split[0];
                double price = Double.parseDouble(split[1]);
                Discount.Code discountCode = Discount.Code.valueOf(split[2]);
                return new Quote(shopName, price, discountCode);
            }
        }
        ```

        - 할인 정보 포함

        ```java
        public class BestPriceFinder {
        	...

            private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

            public List<String> findPricesSequential(String product) {
                return shops.stream()
                        .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                        .collect(toList());
            }
        }
        ```

    - 할인 서비스 사용
        - 순차적 동기방식 할인 서비스

        ```java
        public List<String> findPricesSequential(String product) {
            return shops.stream()
                    .map(shop -> shop.getPrice(product))
                    .map(Quote::parse)
                    .map(Discount::applyDiscount)
                    .collect(toList());
        }
        ```

    - 동기 작업과 비동기 작업 조합하기

        ```java
        public List<String> findPricesFuture(String product) {
            List<CompletableFuture<String>> priceFutures = shops.stream()
                    .map(shop -> CompletableFuture.supplyAsync(
                            () -> shop.getPrice(product), executor
                    ))
                    .map(future -> future.thenApply(Quote::parse))
                    .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(
                            () -> Discount.applyDiscount(quote), executor
                    )))
                    .collect(toList());
            return priceFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(toList());
        }
        ```

        - `supplyAsync` 에 람다 표현식을 전달해서 비동기적으로 상점 조회 → `Stream<CompletableFuture<String>>` 반환
        - `thenApply` 메서드를 호출해서 문자열을 Quote 인스턴스로 변환 → `Stream<CompletableFuture<Quote>>` 반환
            - thenApply → CompletableFuture가 끝날 때까지 블록하지 않는다.
        - `thenCompose`를 통해 할인된 최종 가격을 알 수 있다. → `Stream<CompletableFuture<String>>` 반환
    - 독립 CompletableFuture와 비독립 CompletableFuture 합치기
        - `thenCombine` → 두번째 인수로 받는 BiFunction을 통해 CompletableFuture 결과를 어떻게 합치는지 결정한다.

        ```java
        public List<String> findPricesInUSD(String product) {
            List<CompletableFuture<Double>> priceFutures = new ArrayList<>();
            for (Shop shop : shops) {
                CompletableFuture<Double> priceFutureInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                        .thenCombine(
                                CompletableFuture.supplyAsync(() -> getRate(Money.EUR, Money.USD)),
                                (price, rate) -> price * rate
                        );
                priceFutures.add(priceFutureInUSD);
            }

            return priceFutures.stream()
                    .map(CompletableFuture::join)
                    .map(price -> " price is " + price)
                    .collect(toList());
        }
        ```

    - Future의 리플렉션과 CompletableFuture의 리플렉션
        - CompletableFuture는 람다 표현식을 사용해 코드 가독성의 이점을 보여준다.
    - 타임아웃 효과적으로 사용하기
        - 자바 9에서 제공하는 CompletableFuture
            - orTimeout → 지정된 시간이 지난 후에 CompletableFuture를 TimeoutException으로 완료하면서 또 다른 CompletableFuture를 반환할 수 있도록 ScheduledThreadExecutor를 활용한다.
            - completeOnTimeout → 지정된 시간이 지났을 때 지정된 값을 사용한다.

        ```java
        CompletableFuture<Double> priceFutureInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                        .thenCombine(
                                CompletableFuture.supplyAsync(() -> getRate(Money.EUR, Money.USD))
                                    .completeOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS),
                                (price, rate) -> price * rate
                        )
                        .orTimeout(3, TimeUnit.SECONDS);
        ```

- CompletableFuture의 종료에 대응하는 방법
    - 최저가격 검색 어플리케이션 리팩터링

    ```java
    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
    }

    public void printPricesStream(String product) {
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream(product)
                .map(f -> f.thenAccept(s -> System.out.println(s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responded in " + ((System.nanoTime() - start) / 1_000_000) + " msecs");
    }
    ```

    - thenAccept → CompletableFuture가 생성한 결과를 어떻게 소비할지 지정, CompletableFuture<Void> 반환
    - CompletableFuture.allOf → CompletableFuture 배열을 입력받아 CompletableFuture<Void>를 반환한다.