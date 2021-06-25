# 9. 리펙터링, 테스팅, 디버깅

- 가독성과 유연성을 개선하는 리팩터링
    - 익명 클래스를 람다 표현식으로 리팩터링하기

    ```java
    Runnable r1 = new Runnable() {
    	public void run() {
    		System.out.println("Hello");
    	}
    };

    Runnable r2 = () -> System.out.println("Hello");
    ```

    - 람다 표현식을 참조 메서드로 리팩터링하기

    ```java
    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
            groupingBy(dish -> {
                if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                else return CaloricLevel.FAT;
            }));

    public class Dish {
    	...
    	public CaloricLevel getCaloricLevel() {
    		if (dish.getCalories() <= 400) return CaloricLevel.DIET;
        else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
    	}
    }

    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(groupingBy(Dish::getCaloricLevel));

    ```

    - 명령형 데이터 처리를 스트림으로 리팩터링

    ```java
    List<String> dishNames = new ArrayList<>();
    for (Dish dish : menu) {
    	if (dish.getCalories() > 300) {
    		dishNames.add(dish.getName());
    	]
    }

    List<String> dishNames = menu.parallelStream()
                                 .filter(dish -> dish.getCalories() > 300)
                                 .map(Dish::getName)
                                 .collect(toList());
    ```

    - 코드 유연성 개선
        - 함수형 인터페이스 적용
            - 조건부 연기 실행
                - 특정 조건에서만 작동되도록 과정을 연기할 수 있어야 한다.
                - 객체의 일부 메서드를 호출하는 상황이라면 내부적으로 객체의 상태를 확인하고 메서드를 호출하도록 구현하는 것이 좋다.
            - 실행 어라운드
                - 매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드가 있다면 람다로 구현할수 있다.
                - 람다를 통해 객체의 동작을 결정하는 함수형 인터페이스를 간결하게 사용한다.
- 람다로 객체지향 디자인 패턴 리팩터링하기
    - 전략(Strategy)
        - 알고리즘을 나타내는 인터페이스

        ```java
        public interface ValidationStrategy {
        	boolean execute(String s);
        }
        ```

        - 다양한 알고리즘을 나타내는 한 개 이상의 인터페이스 구현

        ```java
        public class isAllLowerCase implements ValidationStrategy {
            @Override
            public boolean execute(String s) {
                return s.matches("[a-z]+");
            }
        }

        public class IsNumeric implements ValidationStrategy {
            @Override
            public boolean execute(String s) {
                return s.matches("\\d+");
            }
        }
        ```

        - 전략 객체를 사용하는 한 개 이상의 클라이언트

        ```java
        @AllArgsConstructor
        public class Validator {

            private final ValidationStrategy strategy;

            public boolean validate(String s) {
                return strategy.execute(s);
            }
        }
        ```

        - 데모 어플리케이션 및 람다 표현식 사용하기

        ```java
        Validator numericValidator = new Validator(new IsNumeric());
        boolean b1 = numericValidator.validate("aaaa");
        Validator lowerCaseValidator = new Validator(new isAllLowerCase());
        boolean b2 = lowerCaseValidator.validate("bbbb");

        Validator numericValidator = new Validator(s -> s.matches("\\d+"));
        boolean b1 = numericValidator.validate("aaaa");
        Validator lowerCaseValidator = new Validator(s -> s.matches("[a-z]+"));
        boolean b2 = lowerCaseValidator.validate("bbbb");
        ```

    - 탬플릿 메서드(template method)
        - 알고리즘의 개요를 제시한 다음에 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 사용한다.
        - 알고리즘을 사용하고 싶은데 그대로는 안되고 조금 고쳐야 되는 상황에 적합
        - 동작을 정의하는 추상 클래스

        ```java
        public abstract class OnlineBanking {
            public void processCustomer(int id) {
                Customer c = DataBase.getCustomerWithId(id);
            }

            abstract void makeCustomerHappy(Customer c);
        }
        ```

        - 실행 함수를 가질 클래스

        ```java
        public class OnlineBankingLambda {

            public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
                Customer c = DataBase.getCustomerWithId(id);
                makeCustomerHappy.accept(c);
            }
        }
        ```

        - 람다 표현식 사용

        ```java
        new OnlineBankingLambda().processCustomer(1337, c -> System.out.println("Hello " + c.getName()));
        ```

    - 옵저버(observer)
        - 어떤 이벤트가 발생했을 때 한 객체가 다른 객체 리스트에 자동으로 알림을 보내야하는 상황에서 사용한다.
        - GUI 애플리케이션에 자주 사용한다.
        - 다양한 옵저버를 그룹화할 인터페이스

        ```java
        public interface Observer {
            void notify(String tweet);
        }
        ```

        - 다양한 키워드에 다른 동작을 수행할 수 있는 옵저버 정의

        ```java
        public class NYTimes implements Observer {
            @Override
            public void notify(String tweet) {
                if (tweet != null && tweet.contains("money")) {
                    System.out.println("Breaking news In NY! " + tweet);
                }
            }
        }

        public class Guardian implements Observer {
            @Override
            public void notify(String tweet) {
                if (tweet != null && tweet.contains("queen")) {
                    System.out.println("Yet more new from Londun... " + tweet);
                }
            }
        }

        public class LeMonde implements Observer {
            @Override
            public void notify(String tweet) {
                if (tweet != null && tweet.contains("wine")) {
                    System.out.println("Today cheese, wine and news! " + tweet);
                }
            }
        }
        ```

        - 주제를 그룹화할 인터페이스 정의

        ```java
        public interface Subject {
            void registerObserver(Observer observer);
            void notifyObserver(String tweet);
        }
        ```

        - Subject 구현체 정의

        ```java
        public class Feed implements Subject {

            private final List<Observer> observers = new ArrayList<>();

            @Override
            public void registerObserver(Observer observer) {
                this.observers.add(observer);
            }

            @Override
            public void notifyObserver(String tweet) {
                observers.forEach(o -> o.notify(tweet));
            }
        }
        ```

        - 주제와 옵저버를 연결하는 데모 애플리케이션 및 람다 표현식 사용

        ```java
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObserver("The queen said get favourite book Modern Java In Action!");

        Feed f = new Feed();
        f.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news In NY! " + tweet);
            }
        });
        f.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet more new from Londun... " + tweet);
            }
        });
        f.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("wine")) {
                System.out.println("Today cheese, wine and news! " + tweet);
            }
        });
        f.notifyObserver("The queen said get favourite book Modern Java In Action!");
        ```

    - 의무 체인(chain of responsibility)
        - 한 객체가 어떤 작업을 처리한 다음에 다른 객체로 결과를 전달하고, 다른 객체도 해야할 작업을 처리한 다음에 또 다른 객체로 전달하는 식이다.
        - 다음 처리할 객체 정보를 유지하는 필드를 포함하는 작업 처리 추상 클래스로 의무 체인 패턴 구성
        - 작업 처리 체인 패턴 클래스

        ```java
        @Setter
        public abstract class ProcessingObject<T> {

            protected ProcessingObject<T> successor;

            public T handle(T input) {
                T r = handleWork(input);
                if (successor != null) {
                    return successor.handle(r);
                }
                return r;
            }

            protected abstract T handleWork(T input);
        }
        ```

        - 작업 처리 객체

        ```java
        public class HeaderTextProcessing extends ProcessingObject<String> {
            @Override
            protected String handleWork(String text) {
                return "Form Raoul, Mario and Alan: " + text;
            }
        }

        public class SpellCheckerProcessing extends ProcessingObject<String> {
            @Override
            protected String handleWork(String text) {
                return text.replace("labda", "lambda");
            }
        }
        ```

        - 데모 어플리케이션 및 람다 표현식 사용하기

        ```java
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        SpellCheckerProcessing p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);
        String result = p1.handle("Aren't labda really sexy?!!");
        System.out.println(result);

        UnaryOperator<String> headerProcessing = text -> "Form Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellCheckerProcessing = text -> text.replace("labda", "lambda");
        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        String result = pipeline.apply("Aren't labda really sexy?!!");
        System.out.println(result);
        ```

    - 팩토리(factory)
        - 인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 사용한다.
        - 다형성을 이용한 팩토리 메서드 및 람다 표현식 사용

        ```java
        public class ProductFactory {

            public static Product createProduct(String name) {
                switch (name) {
                    case "loan": return new Loan();
                    case "stock": return new Stock();
                    case "bond": return new Bond();
                    default: throw new RuntimeException(" No such product " + name);
                }
            }
        }

        public class ProductFactory {

            final static Map<String, Supplier<Product>> map = new HashMap<>();

            static {
                map.put("loan", Loan::new);
                map.put("stock", Stock::new);
                map.put("bond", Bond::new);
            }

            public static Product createProductWithLambda(String name) {
                Supplier<Product> p = map.get(name);
                if (p != null) return p.get();
                throw new RuntimeException(" No such product " + name);
            }
        }
        ```

- 람다 테스팅
    - 단위 테스트

    ```java
    @AllArgsConstructor
    @Data
    public class Point {

        private final int x;
        private final int y;

        public Point moveRightBy(int x) {
            return new Point(this.x + x, this.y);
        }
    }

    @Test
    public void testMoveRightBy() throws Exception {
        Point p1 = new Point(5, 5);
        Point p2 = p1.moveRightBy(10);
        assertEquals(15, p2.getX());
        assertEquals(5, p2.getY());
    }
    ```

    - 보이는 람다 표현식의 동작 테스트
        - 람다를 필드에 저장해서 재사용할 수 있으며 람다 로직을 테스트 할 수 있다.

    ```java
    @AllArgsConstructor
    @Data
    public class Point {
    	...

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        public final static Comparator<Point> compareByXAndThenY = comparing(Point::getX).thenComparing(Point::getY);
    }

    @Test
    public void testComparingTwoPoints() throws Exception {
        Point p1 = new Point(10, 15);
        Point p2 = new Point(10, 20);
        int result = Point.compareByXAndThenY.compare(p1, p2);
        assertTrue(result < 0);
    }
    ```

    - 람다를 사용하는 메서드의 동작에 집중하라
        - 람다의 목표는 정해진 동작을 다른 메서드에서 사용할 수 있도록 하나의 조각으로 캡슐화하는 것이다.
        - 메서드를 테스트하면서 람다 표현식을 공개하지 않으면서 람다 표현식을 검증할 수 있다.

    ```java
    @AllArgsConstructor
    @Data
    public class Point {
    		...

        public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
            return points.stream()
                    .map(p -> new Point(p.getX() + x, p.getY()))
                    .collect(toList());
        }
    }

    @Test
    public void testMoveAllPointsRightBy() throws Exception {
        List<Point> points = Arrays.asList(new Point(5, 5), new Point(10, 5));
        List<Point> expectedPoints = Arrays.asList(new Point(15, 5), new Point(20, 5));
        List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
        assertEquals(expectedPoints, newPoints);
    }
    ```

- 디버깅
    - 스택 트레이스
        - 람다 표현식은 이름이 없으므로 컴파일러가 람다를 참조하는 이름을 만든다.
        - 메서드 참보를 사용하는 클래스와 같은 곳에 선언되어 있는 메서드를 참보할 때는 메서드 참조 이름이 스택 트레이스에 나타난다.
    - 로깅
        - 스트림 중간 연산에서 진행되는 값을 확인하기 위해 peek을 사용한다.

        ```java
        numbers.stream()
              .peek(x -> System.out.println("from stream: " + x))
              .map(x -> x + 17)
              .peek(x -> System.out.println("after map: " + x))
              .filter(x -> x % 2 == 0)
              .peek(x -> System.out.println("after filter: " + x))
              .limit(3)
              .peek(x -> System.out.println("after limit: " + x))
              .collect(toList());
        ```