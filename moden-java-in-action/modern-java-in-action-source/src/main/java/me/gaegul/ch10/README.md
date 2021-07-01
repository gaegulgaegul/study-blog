# 10. 람다를 이용한 도메인 전용 언어

- 도메인 전용 언어
    - 코드의 의도가 명확히 전달되어야 하며 프로그래머가 아닌 사람도 이해할 수 있어야 한다.
    - 가독성이 좋아야 한다.
    - DSL의 장점과 단점
        - 장점
            - 간결함 → API는 비즈니스 로직을 간편하게 캡슐화하므로 반복을 피한다.
            - 가독성 → 도메인 영역의 용어를 사용하므로 비 도메인 전문가도 코드르 쉽게 이해할 수 있다.
            - 유지보수 → 잘 설계된 DSL로 구현하는 코드는 쉽게 유지보수 할 수 있다.
            - 높은 수준의 추상화 → DSL은 도메인과 같은 추상화 수준에서 동작하므로 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨긴다.
            - 집중 → 비즈니스 도메인의 규칙을 표현할 목적으로 설계된 언어이므로 프로그래머가 특정 코드에 집중할 수 있다.
            - 관심사분리 → 지정된 언어로 비즈니스 로직을 표현함으로 어플리케이션의 인프라 구조와 관련된 문제와 독립적인 비즈니스 관련된 코드에서 집중하기 용이하다.
        - 단점
            - DSL 설계의 어려움 → 간결하게 제한적인 언어에 도메인 지식을 담는 것이 쉽지 않다.
            - 개발 비용 → 초기 프로젝트에 많은 비용과 시간이 소모되는 작업이다.
            - 추가 우회 계층 → 추가적인 계층으로 도메인 모델을 감싸며 이 때 계층을 작게 만들어 성능 문제를 회피한다.
            - 새로 배워야 하는 언어 → 프로젝트에 추가되면 팀 내에서 배워야 한다.
            - 호스팅 언어 한계 → 사용자 친화적 DSL을 만들기 힘들다.
    - JVM에서 이용할 수 있는 다른 DSL해결책
        - 내부 DSL → 자바 같은 기존 호스팅 언어를 기반으로 구현한다.
        - 외부 DSL → 호스팅언어와는 독립적으로 자체 문법을 가진다.
        - 다중 DSL → JVM과  호환성을 유지하면서 단순하고 쉽게 배울수 있다
            - 코틀린, 스칼라 등
- 자바로 DSL을 만드는 패턴과 기법
    - 메서드 체인
        - 메서드 체인 한 개의 메서드 호출 체인으로 DSL을 사용한다.
        - DSL에서 가장 흔한 방식
        - 빌더 패턴

        ```java
        public class MethodChainingOrderBuilder {

            public final Order order = new Order();

            private MethodChainingOrderBuilder(String customer) {
                order.setCustomer(customer);
            }

            public static MethodChainingOrderBuilder forCustomer(String customer) {
                return new MethodChainingOrderBuilder(customer);
            }

            public TradeBuilder buy(int quantity) {
                return new TradeBuilder(this, Trade.Type.BUY, quantity);
            }

            public TradeBuilder sell(int quantity) {
                return new TradeBuilder(this, Trade.Type.SELL, quantity);
            }

            public MethodChainingOrderBuilder addTrade(Trade trade) {
                order.addTrade(trade);
                return this;
            }

            public Order end() {
                return order;
            }

            public class StockBuilder {

                private final MethodChainingOrderBuilder builder;
                private final Trade trade;
                private final Stock stock = new Stock();

                public StockBuilder(MethodChainingOrderBuilder builder, Trade trade, String symbol) {
                    this.builder = builder;
                    this.trade = trade;
                    stock.setSymbol(symbol);
                }

                public TradeBuilderWithStock on(String market) {
                    stock.setMarket(market);
                    trade.setStock(stock);
                    return new TradeBuilderWithStock(builder, trade);
                }
            }

            public class TradeBuilder {
                private final MethodChainingOrderBuilder builder;
                public final Trade trade = new Trade();

                public TradeBuilder(MethodChainingOrderBuilder builder, Trade.Type type, int quantity) {
                    this.builder = builder;
                    trade.setType(type);
                    trade.setQuantity(quantity);
                }

                public StockBuilder stock(String symbol) {
                    return new StockBuilder(builder, trade, symbol);
                }
            }

            public class TradeBuilderWithStock {
                private final MethodChainingOrderBuilder builder;
                private final Trade trade;

                public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
                    this.builder = builder;
                    this.trade = trade;
                }

                public MethodChainingOrderBuilder at(double price) {
                    trade.setPrice(price);
                    return builder.addTrade(trade);
                }
            }
        }
        ```

    - 중첩된 함수 이용
        - 다른 함수 안에 함수를 이용해 도메인 모델을 만든다.

        ```java
        public class NestedFunctionOrderBuilder {

            public static Order order(String customer, Trade... trades) {
                Order order = new Order();
                order.setCustomer(customer);
                Stream.of(trades).forEach(order::addTrade);
                return order;
            }

            public static Trade buy(int quantity, Stock stock, double price) {
                return buildTrade(quantity, stock, price, Trade.Type.BUY);
            }

            public static Trade sell(int quantity, Stock stock, double price) {
                return buildTrade(quantity, stock,price,Trade.Type.SELL);
            }

            private static Trade buildTrade(int quantity, Stock stock, double price, Trade.Type type) {
                Trade trade = new Trade();
                trade.setQuantity(quantity);
                trade.setType(type);
                trade.setStock(stock);
                trade.setPrice(price);
                return trade;
            }

            public static double at(double price) {
                return price;
            }

            public static Stock stock(String symbol, String market) {
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                stock.setMarket(market);
                return stock;
            }

            public static String on(String market) {
                return market;
            }
        }
        ```

    - 람다 표현식을 이용한 함수 시퀀싱
        - 람다 표현식으로 정의한 함수 시퀀스를 사용한다.

        ```java
        public class LambdaOrderBuilder {

            private Order order = new Order();

            public static Order order(Consumer<LambdaOrderBuilder> consumer) {
                LambdaOrderBuilder builder = new LambdaOrderBuilder();
                consumer.accept(builder);
                return builder.order;
            }

            public void forCustomer(String customer) {
                order.setCustomer(customer);
            }

            public void buy(Consumer<TradeBuilder> consumer) {
                trade(consumer, Trade.Type.BUY);
            }

            public void sell(Consumer<TradeBuilder> consumer) {
                trade(consumer, Trade.Type.SELL);
            }

            public void trade(Consumer<TradeBuilder> consumer, Trade.Type type) {
                TradeBuilder builder = new TradeBuilder();
                builder.trade.setType(type);
                consumer.accept(builder);
                order.addTrade(builder.trade);
            }

            public class StockBuilder {
                private Stock stock = new Stock();

                public void symbol(String symbol) {
                    stock.setSymbol(symbol);
                }

                public void market(String market) {
                    stock.setMarket(market);
                }
            }

            public class TradeBuilder {
                private Trade trade = new Trade();

                public void quantity(int quantity) {
                    trade.setQuantity(quantity);
                }

                public void price(double price) {
                    trade.setPrice(price);
                }

                public void stock(Consumer<StockBuilder> consumer) {
                    StockBuilder builder = new StockBuilder();
                    consumer.accept(builder);
                    trade.setStock(builder.stock);
                }
            }
        }
        ```

    - 조합하기
        - 세가지 DSL 패턴 각자가 장단점을 가지고 있다.
        - 중첩된 함수 패턴을 람다 기법과 혼용한다.

        ```java
        public class MixedBuilder {

            public static Order forCustomer(String customer, TradeBuilder... builders) {
                Order order = new Order();
                order.setCustomer(customer);
                Stream.of(builders).forEach(b -> order.addTrade(b.trade));
                return order;
            }

            public static TradeBuilder buy(Consumer<TradeBuilder> consumer) {
                return buildTrade(consumer, Trade.Type.BUY);
            }

            public static TradeBuilder sell(Consumer<TradeBuilder> consumer) {
                return buildTrade(consumer, Trade.Type.SELL);
            }

            private static TradeBuilder buildTrade(Consumer<TradeBuilder> consumer, Trade.Type type) {
                TradeBuilder builder = new TradeBuilder();
                builder.trade.setType(type);
                consumer.accept(builder);
                return builder;
            }

            public static class TradeBuilder {
                private Trade trade = new Trade();

                public TradeBuilder quantity(int quantity) {
                    trade.setQuantity(quantity);
                    return this;
                }

                public TradeBuilder at(double price) {
                    trade.setPrice(price);
                    return this;
                }

                public StockBuilder stock(String symbol) {
                    return new StockBuilder(this, trade, symbol);
                }
            }

            public static class StockBuilder {
                private final TradeBuilder builder;
                private final Trade trade;
                private final Stock stock = new Stock();

                private StockBuilder(TradeBuilder builder, Trade trade, String symbol) {
                    this.builder = builder;
                    this.trade = trade;
                    stock.setSymbol(symbol);
                }

                public TradeBuilder on(String market) {
                    stock.setMarket(market);
                    trade.setStock(stock);
                    return builder;
                }
            }
        }
        ```

    - DSL에 메서드 참조 사용하기

        ```java
        public class TaxCalculatorWithLambda {

            public DoubleUnaryOperator taxFunction = d -> d;

            public TaxCalculatorWithLambda with(DoubleUnaryOperator f) {
                taxFunction = taxFunction.andThen(f);
                return this;
            }

            public double calculate(Order order) {
                return taxFunction.applyAsDouble(order.getValue());
            }

        }

        new TaxCalculatorWithLambda().with(Tax::regional).with(Tax::surcharge).calculate(order);
        ```

    - DSL 패턴의 장점과 단점
        - 메서드 체인
            - 장점
                - 메서드의 이름이 키워드 인수 역할
                - 선택형 파라미터와 잘 동작한다.
                - DSL 사용자가 정해진 순서로 메서드를 호출하거나 강제할 수 있다.
                - 정적 메서드를 최소화하거나 없앨 수 있다.
                - 문법적 잡음을 최소화한다.
            - 단점
                - 구현이 장황하다.
                - 빌드를 연결하는 접착코드가 필요하다.
                - 들여쓰기 규칙으로만 도메인 객체 계층을 정의한다.
        - 중첩 함수
            - 장점
                - 구현의 장황함을 줄일 수 있다.
                - 함수 중첩으로 도메인 객체 계층을 반영한다.
            - 단점
                - 정적 메서드의 사용이 빈번하다.
                - 이름이 아닌 위치로 인수를 정의한다.
                - 선택형 파라미터를 처리할 메서드 오버로딩이 필요하다.
        - 람다를 이용한 함수 시퀀싱
            - 장점
                - 선택형 파라미터와 잘 동작한다.
                - 정적 메서드를 최소화하거나 없앨 수 있다.
                - 람다 중첩으로 도메인 객체 계층을 반영한다.
                - 빌더 접착 코드가 없다.
            - 단점
                - 구현이 장황하다.
                - 람다 표현식으로 인한 문법적 잡음이 DSL에 존재한다.