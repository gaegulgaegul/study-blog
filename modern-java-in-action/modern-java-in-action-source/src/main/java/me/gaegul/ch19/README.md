# 19. 함수형 프로그래밍 기법

- 함수는 모든 곳에 존재한다
    - 고차원 함수
        - 하나 이상의 함수를 인수로 받음
        - 함수를 결과로 반환
        - 동작 파라미터화, 정적 메서드 (ex. Comparator.comparing 등)
    - 커링
        - 함수를 모듈화하고 코드를 재사용하는데 도움을 준다.
        - 기존 로직을 활용해 변환 함수를 생산하는 팩토리 개념
            - 기존 변환 로직을 재활용하는 유연한 코드

            ```java
            public static void main(String[] args) {
                DoubleUnaryOperator convertCtoF = curriedConverter(9.0 / 5, 32);
                DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
                DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214, 0);

                System.out.printf("24 °C = %.2f °F%n", convertCtoF.applyAsDouble(24));
                System.out.printf("US$100 = £%.2f%n", convertUSDtoGBP.applyAsDouble(100));
                System.out.printf("20 km = %.2f miles%n", convertKmtoMi.applyAsDouble(20));
            }

            public static double converter(double x, double f, double b) {
                return x * f + b;
            }

            public static DoubleUnaryOperator curriedConverter(double f, double b) {
                return x -> x * f + b;
            }
            ```

- 영속 자료구조
    - 파괴적인 갱신과 함수형
        - 자료구조 갱신할 때 발생할 수 있는 문제
            - 기차여행 예제

        ```java
        public static void main(String[] args) {
            TrainJourney tj1 = new TrainJourney(40, new TrainJourney(30, null));
            TrainJourney tj2 = new TrainJourney(20, new TrainJourney(50, null));

            TrainJourney appended = append(tj1, tj2);
            visit(appended, tj -> System.out.print(tj.price + " - "));
            System.out.println();

            // tj1, tj2를 바꾸지 않고 새 TrainJourney를 생성
            TrainJourney appended2 = append(tj1, tj2);
            visit(appended2, tj -> System.out.print(tj.price + " - "));
            System.out.println();

            // tj1은 바뀌었지만 여전히 결과에서 확인할 수 없음
            TrainJourney linked = link(tj1, tj2);
            visit(linked, tj -> System.out.print(tj.price + " - "));
            System.out.println();

            // ... 여기서 이 코드의 주석을 해제하면 tj2가 이미 바뀐 tj1의 끝에 추가된다. 끝없는 visit() 재귀 호출이 일어나면서 StackOverflowError가 발생함.
            /*TrainJourney linked2 = link(tj1, tj2);
            visit(linked2, tj -> System.out.print(tj.price + " - "));
            System.out.println();*/
        }

        public static class TrainJourney {
            public int price;
            public TrainJourney onward;
            public TrainJourney(int p, TrainJourney t) {
                price = p;
                onward = t;
            }
        }

        public static TrainJourney link(TrainJourney a, TrainJourney b) {
            if (a == null) {
                return b;
            }

            TrainJourney t = a;
            while (t.onward != null) {
                t = t.onward;
            }
            t.onward = b;

            return a;
        }

        public static TrainJourney append(TrainJourney a, TrainJourney b) {
            return a == null ? b : new TrainJourney(a.price, append(a.onward, b));
        }

        public static void visit(TrainJourney journey, Consumer<TrainJourney> c) {
            if (journey != null) {
                c.accept(journey);
                visit(journey.onward, c);
            }
        }
        ```

        - TrainJourney의 onward는 직통열차, 마지막 구간의 경우 null
        - link 메서드를 호출하면 a가 b를 포함하면서 a가 변경되는 파괴적인 갱신이 일어난다.
            - X → Y의 여정이 아니라 X → Z가 된다.
        - 함수형 해결방법을 사용하는 것이 좋다.
            - append 메서드
    - 트리를 사용한 다른 예제
        - 이진 탐색 트리를 이용한다.
        - 새로운 노드를 추가할 때 탐색한 트리 자체를 반환한다.
            - 기존 트리가 반환되면서 update 되어 파괴적인 갱신이 일어난다.

        ```java
        public static void main(String[] args) {
            Tree t = new Tree("Mary", 22,
                    new Tree("Emily", 20,
                            new Tree("Alan", 50, null, null),
                            new Tree("Georgie", 23, null, null)
                    ),
                    new Tree("Tian", 29,
                            new Tree("Raoul", 23, null, null),
                            null
                    )
            );

            // 발견 = 23
            System.out.printf("Raoul: %d%n", lookup("Raoul", -1, t));
            // 발견되지 않음 = -1
            System.out.printf("Jeff: %d%n", lookup("Jeff", -1, t));

            Tree u = update("Jim", 40, t);
            // fupdate로 t가 바뀌지 않았으므로 Jeff는 발견되지 않음 = -1
            System.out.printf("Jeff: %d%n", lookup("Jeff", -1, u));
            // 발견 = 40
            System.out.printf("Jim: %d%n", lookup("Jim", -1, u));
        }

        public static class Tree {
            private String key;
            private int val;
            private Tree left;
            private Tree right;

            public Tree(String key, int val, Tree left, Tree right) {
                this.key = key;
                this.val = val;
                this.left = left;
                this.right = right;
            }
        }

        public static int lookup(String key, int defaultVal, Tree tree) {
            if (tree == null) return defaultVal;
            if (key.equals(tree.key)) return tree.val;
            return lookup(key, defaultVal, key.compareTo(tree.key) < 0 ? tree.left : tree.right);
        }

        public static Tree update(String key, int newVal, Tree tree) {
            if (tree == null) tree = new Tree(key, newVal, null, null);
            else if (key.equals(tree.key)) tree.val = newVal;
            else update(key, newVal, key.compareTo(tree.key) < 0 ? tree.left : tree.right);
            return tree;
        }
        ```

    - 함수형 접근법 사용
        - 새로운 키/값 쌍을 저장할 새로운 노드 생성
        - 트리의 루트에서 새로 생성한 노드의 경로에 있는 노드들도 새로 생성

        ```java
        public static Tree fupdate(String key, int newVal, Tree tree) {
                return (tree == null) ?
                        new Tree(key, newVal, null, null) :
                            key.equals(tree.key) ?
                                    new Tree(key, newVal, tree.left, tree.right) :
                            key.compareTo(tree.key) < 0 ?
                                    new Tree(tree.key, tree.val, fupdate(key, newVal, tree.left), tree.right) :
                                    new Tree(tree.key, tree.val, tree.left, fupdate(key, newVal, tree.right));
            }
        ```

- 스트림과 게으른 평가
    - 자기 정의 스트림
        - 게으른 평가
            - 스트림은 재귀적 정의는 허용하지 않는다.
            - DB같은 질의를 표현하고 병렬화 할 수 있는 능력을 얻을 수 있다.
    - 게으른 리스트 만들기
        - 스트림에 최종 연상을 적용해서 실제 계산을 해야하는 상황에서만 실제 연산이 이루어진다.
        - 기본적인 연결 리스트

            ```java
            public static void main(String[] args) {
                MyList<Integer> l = new MyLinkedList<>(5, new MyLinkedList<>(10, new Empty<>()));

            		System.out.println(l.head());
            }

            interface MyList<T> {
                T head();
                
                MyList<T> tail();
                
                default boolean isEmpty() {
                    return true;
                }
            }

            static class MyLinkedList<T> implements MyList<T> {
                private final T head;
                private final MyList<T> tail;
                
                public MyLinkedList(T head, MyList<T> tail) {
                    this.head = head;
                    this.tail = tail;
                }

                @Override
                public T head() {
                    return head;
                }

                @Override
                public MyList<T> tail() {
                    return tail;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }
            }

            static class Empty<T> implements MyList<T> {

                @Override
                public T head() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public MyList<T> tail() {
                    throw new UnsupportedOperationException();
                }
            }
            ```

        - 기본적인 게으른 리스트
            - Supplier<T>를 통해 게으른 리스트를 만들면 꼬리가 모두 메모리에 존재하지 않게 할 수 있다.

            ```java
            public static void main(String[] args) {
            		...

                LazyList<Integer> numbers = from(2);
                int two = numbers.head();
                int three = numbers.tail().head();
                int four = numbers.tail().tail().head();
                System.out.println(two + " " + three + " " + four);
            }

            static class LazyList<T> implements MyList<T> {
                final T head;
                final Supplier<MyList<T>> tail;

                public LazyList(T head, Supplier<MyList<T>> tail) {
                    this.head = head;
                    this.tail = tail;
                }

                @Override
                public T head() {
                    return head;
                }

                @Override
                public MyList<T> tail() {
                    return tail.get();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }
            }

            public static LazyList<Integer> from(int n) {
                return new LazyList<Integer>(n, () -> from(n + 1));
            }
            ```

- 패턴 매칭
    - 방문자 디자인 패턴
        - 특정 데이터 형식을 방문하는 알고리즘을 캡슐화하는 클래스를 따로 만들 수 있다.
    - 패턴 매칭의 힘
        - 자바에서 패턴 매칭은 지원하지 않는다.
        - 스칼라의 패턴 매칭은 다수준, 예제로 사용되는 자바는 단일 수준

        ```java
        public static void main(String[] args) {
            simplify();

            Expr e = new BinOp("+", new Number(5), new BinOp("*", new Number(3), new Number(4)));
            Integer result = evaluate(e);
            System.out.println(e + " = " + result);
        }

        public static void simplify() {
            TriFunction<String, Expr, Expr, Expr> binOpCase = (opName, left, right) -> {
                if ("+".equals(opName)) {
                    if (left instanceof Number && ((Number) left).val == 0) {
                        return right;
                    }
                    if (right instanceof Number && ((Number) right).val == 0) {
                        return left;
                    }
                }
                if ("*".equals(opName)) {
                    if (left instanceof Number && ((Number) left).val == 1) {
                        return right;
                    }
                    if (right instanceof Number && ((Number) right).val == 1) {
                        return left;
                    }

                }
                return new BinOp(opName, left, right);
            };
            Function<Integer, Expr> numCase = val -> new Number(val);
            Supplier<Expr> defaultCase = () -> new Number(0);

            Expr e = new BinOp("+", new Number(5), new Number(0));
            Expr match = patternMatchExpr(e, binOpCase, numCase, defaultCase);
            if (match instanceof Number) {
                System.out.println("Number: " + match);
            }
            else if (match instanceof BinOp) {
                System.out.println("BinOp: " + match);
            }
        }

        private static Integer evaluate(Expr e) {
            Function<Integer, Integer> numCase = val -> val;
            Supplier<Integer> defaultCase = () -> 0;
            TriFunction<String, Expr, Expr, Integer> binOpCase = (opName, left, right) -> {
                if ("+".equals(opName)) {
                    if (left instanceof Number && right instanceof Number) {
                        return ((Number) left).val + ((Number) right).val;
                    }
                    if (right instanceof Number && left instanceof BinOp) {
                        return ((Number) right).val + evaluate(left);
                    }
                    if (left instanceof Number && right instanceof BinOp) {
                        return ((Number) left).val + evaluate(right);
                    }
                    if (left instanceof BinOp && right instanceof BinOp) {
                        return evaluate(left) + evaluate(right);
                    }
                }
                if ("*".equals(opName)) {
                    if (left instanceof Number && right instanceof Number) {
                        return ((Number) left).val * ((Number) right).val;
                    }
                    if (right instanceof Number && left instanceof BinOp) {
                        return ((Number) right).val * evaluate(left);
                    }
                    if (left instanceof Number && right instanceof BinOp) {
                        return ((Number) left).val * evaluate(right);
                    }
                    if (left instanceof BinOp && right instanceof BinOp) {
                        return evaluate(left) * evaluate(right);
                    }
                }
                return defaultCase.get();
            };

            return patternMatchExpr(e, binOpCase, numCase, defaultCase);
        }

        static class Expr {}

        @ToString
        @AllArgsConstructor
        static class Number extends Expr {
            int val;
        }

        @ToString
        @AllArgsConstructor
        static class BinOp extends Expr {
            String opName;
            Expr left, right;
        }

        static <T> T myIf(boolean b, Supplier<T> trueCase, Supplier<T> falseCase) {
            return b ? trueCase.get() : falseCase.get();
        }

        interface TriFunction<S, T, U, R> {
            R apply(S s, T t, U u);
        }

        static <T> T patternMatchExpr(Expr e, TriFunction<String, Expr, Expr, T> binOpCase, Function<Integer, T> numCase, Supplier<T> defaultCase) {
            return (e instanceof BinOp) ? binOpCase.apply(((BinOp) e).opName, ((BinOp) e).left, ((BinOp) e).right) :
                    (e instanceof  Number) ? numCase.apply(((Number) e).val) : defaultCase.get();
        }
        ```

- 기타 정보
    - 캐싱 또는 기억화
        - 재귀적으로 탐색할 때 참조 투명성이 유지되는 상황에서 추가 오버헤드를 피하는 방법 → 기억화
        - 캐시에 값을 저장하여 관리 → 캐싱

        ```java
        private static final Map<Range, Integer> numberOfNodes = new HashMap<>();

        public static void main(String[] args) {
            Range range = new Range(0,1,1);
            numberOfNodes.put(range, 1234);
            computeNumberOfNodesUsingCache(range);
            System.out.println(numberOfNodes);
        }

        public static Integer computeNumberOfNodes(Range range) {
            Integer result = numberOfNodes.get(range);
            if (result != null) {
                return result;
            }

            result = computeNumberOfNodes(range);
            numberOfNodes.put(range, result);
            return result;
        }

        public static Integer computeNumberOfNodesUsingCache(Range range) {
            return numberOfNodes.computeIfAbsent(range, Caching::computeNumberOfNodes);
        }
        ```

        - computeNumberOfNodes 메서드는 공유된 가변 상태이다. → computeNumberOfNodesUsingCache 메서드는 참조 투명성을 가진다.
        - HashMap은 동기화 되지 않아 thread safe하지 않다. → ConcurrentHashMap을 사용하여 동시성을 가진다.
    - 콤비네이터
        - 두 함수 또는 자료구조를 인수로 받아 다른 함수를 반환하는 등 함수를 조합하는 고차원 함수

        ```java
        public static void main(String[] args) {
            System.out.println(repeat(3, (Integer x) -> 2 * x).apply(10));
        }

        static <A,B,C> Function<A,C> compose(Function<B,C> g, Function<A,B> f) {
            return x -> g.apply(f.apply(x));
        }

        static <A> Function<A,A> repeat(int n, Function<A,A> f) {
            return n == 0 ? x -> x : compose(f, repeat(n - 1, f));
        }
        ```