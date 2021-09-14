# 아이템 31. 한정적 와일드카드를 사용해 API 유연성을 높이라

- 불공변 방식보다 유연한 무언가
    - List<String>은 List<Object>의 하위 타입은 아니다.
        - List<Object>에는 어떤 객체든 넣을 수 있다.
        - List<String>에는 문자열만 넣을 수 있다.

        → List<String>은 List<Object>가 하는 일을 제대로 수핸하지 못하니 하위 타입이 될 수 없다.

    - 한정적 와일드카드
        - 생산자 매개변수에 와일드카드 타입 적용
            - `Collection<? extends E>`
                - E의 하위 타입의 Collection을 의미
                - Stakc - pushAll()
                    - 와일드카드를 사용하지 않은 Stack - pushAll()

                        ```java
                        public class Stack<E> {
                            ...

                            public void pushAll(Iterable<E> src) {
                                for (E e : src)
                                    push(e);
                            }

                            public static void main(String[] args) {
                                Stack<Number> numberStack = new Stack<>();
                                Iterable<Integer> integers = Arrays.asList(1,2,3,4,5);

                                // Integer는 Number의 하위 타입이니 논리적으로 동작해야 될 것 같다.
                                // 매개변수화 타입이 불공변이라 컴파일 에러 발생
                                numberStack.pushAll(integers);
                            }

                        }
                        ```

                    - 와일드카드 타입 적용

                        ```java
                        // 매개변수화 티입 E의 Iterable이 아니라 E의 하위 타입의 Iterable을 지원한다.
                        public void pushAll(Iterable<? extends E> src) {
                            for (E e : src)
                                push(e);
                        }
                        ```

        - 소비자 매개변수에 와일드카드 적용
            - `Collection<? super E>`
                - E의 상위 타입의 Collection을 의미
                - Stack - popAll()
                    - 와일드카드를 사용하지 않은 Stack - popAll()

                        ```java
                        public class Stack<E> {
                            ...

                            public void popAll(Collection<E> dst) {
                                while (!isEmpty())
                                    dst.add(pop());
                            }

                            public static void main(String[] args) {
                                Stack<Number> numberStack = new Stack<>();
                                Collection<Object> objects = Arrays.asList(1,2,3,4,5);
                                // 'Collection<Object>는 Collection<Number>의 하위 타입이 아니다.'
                                // 매개변수화 타입이 불공변이라 컴파일 에러 발생
                                numberStack.popAll(objects);
                            }
                        }
                        ```

                    - 와일드카드 타입 적용

                        ```java
                        // 매개변수화 타입 E의 Collection이 아니라 E의 상위 타입의 Collection를 지원한다.
                        public void popAll(Collection<? super E> dst) {
                            while (!isEmpty())
                                dst.add(pop());
                        }
                        ```

- 유연성을 극대화 하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라
    - 생산자, 소비자 역할을 동시에 한다면 와일드카드를 써도 소용없다.
    → 타입을 정확하게 지정해줘야 하는 상황
    - Chooser 생성자 와일드카드 적용

        ```java
        public class Chooser<T> {

            private final List<T> choiceList;

            // T를 확장하는 와일드카드 타입을 사용해 선언
            public Chooser(Collection<? extends T> choices) {
                this.choiceList = new ArrayList<>(choices);
            }

            public T choose() {
                Random rnd = ThreadLocalRandom.current();
                return choiceList.get(rnd.nextInt(choiceList.size()));
            }

            public static void main(String[] args) {
                List<Integer> integers = Arrays.asList(1,2,3,4,5);

                // Chooser 생성자에 Number 하위 타입인 Integer List 전달
                Chooser<Number> numberChooser = new Chooser<>(integers);
                System.out.println(numberChooser.choose());
            }
        }
        ```

    - union 메서드 생성자 와일드카드 적용

        ```java
        public class Union {

            // 반환 타입에는 한정적 와일드카드 타입을 사용하면 안된다.
            public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) {
                Set<E> result = new HashSet(s1);
                result.addAll(s2);
                return result;
            }

            public static void main(String[] args) {
                Set<Integer> integers = Set.of(1,3,5);
                Set<Double> doubles = Set.of(2.0,4.0,6.0);
                Set<Number> numbers = union(integers, doubles);
                System.out.println(numbers);
            }

        }
        ```

        - 자바 7까지는 명시적 타입 인수를 사용해야 한다.
        → `Set<Number> numbers = Union.<Number>union(integers, doubles);`
    - max 메서드 와일드카드 적용

        ```java
        // <E extends Comparable<? super E>>
        //     - Comparable<E>는 E 인스턴스를 소비한다.
        //     - Comparable은 언제나 소비자이므로 Comparable<? super E>로 사용한다.
        // List<? extends E>
        //     - 입력 매개변수에서 E 인스턴스를 생성한다.
        public static <E extends Comparable<? super E>> Optional<E> max(List<? extends E> list) {
            E result = null;
            for (E e : list) {
                if (result == null || e.compareTo(result) > 0) {
                    result = Objects.requireNonNull(e);
                }
            }
            return Optional.ofNullable(result);
        }
        ```

    - 리스트의 아이템을 교환하는 정적 메서드

        ```java
        // 비한정적 매개변수화 타입 사용
        public static <E> void swap(List<E> list, int i, int j) {}
        // 비한정적 와일드카드 사용
        public static void swap(List<?> list, int i, int j) {}
        ```

        - 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하라

            ```java
            public static void swap(List<?> list, int i, int j) {
                // list.get(i)으로 자기 자신의 원소를 꺼내 다시 넣어주는데 오류가 발생한다.
            		// 리스트의 타입이 List<?>인데, List<?>에는 null 외에는 어떤 값도 넣을 수 없다.
                list.set(i, list.set(j, list.get(i)));
            }
            ```

        - private 도우미 메서드

            ```java
            // 외부 API는 와일드카드 타입 유지, 내부에서 매개변수화 타입 사용
            public static void swap(List<?> list, int i, int j) {
                swapHelper(list, i, j);
            }

            // 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
            private static <E> void swapHelper(List<E> list, int i, int j) {
                // 컴파일러는 리스트가 List<E>인 것을 알고 있다.
                list.set(i, list.set(j, list.get(i)));
            }
            ```