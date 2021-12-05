# 아이템 47. 반환 타입으로는 스트림보다 컬렉션이 낫다

### 스트림은 반복은 지원하지 않는다.

- 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다.
- Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함하고 Iterable 인터페이스가 정의한 방식대로 동작한다.
    - for-each로 스트림을 반복할 수 없는 것은 Stream이 Iterable을 확장하지 않았기 때문이다.


### Stream을  for-each로 사용하고 싶은 경우

- 자바 타입 추론의 한계로 컴파일 되지 않는다.

    ```java
    for (ProceessHandle ph : ProcessHandle.allProcesses()::iterator) {
        // 프로세스를 처리한다.
    }
    ```

- Iterable로 형변환

    ```java
    for (ProceessHandle ph : (Iterable<ProcessHandle>)
                             ProcessHandle.allProcesses()::iterator) {
        // 프로세스를 처리한다.
    }
    ```

    - 작동은 하지만 코드가 가독성이 떨어진다.
- 어댑터 메서드

    ```java
    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }
    
    for (ProceessHandle p : iterableOf(ProcessHandle.allProcesses())) {
        // 프로세스를 처리한다.
    }
    ```

    - 어떤 스트림도 for-each문으로 반복할 수 있다.
- Iterable을 Stream으로 반환하는 경우

    ```java
    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
    ```


### 객체 시퀀스를 반환하는 메서드 작성

- 스트림 파이프라인 안에서만 사용하는 경우 스트림으로 반환
- 반복문에서만 사용된다면 Iterable 반환
- 공개 API를 작성하는 경우 두 가지 모두 사용

### Collection 인터페이스

- Iterable의 하위 타입이고 stream 메서드도 제공한다.
- 원소 시퀀스를 반환하는 공개 API의 반환타입에는 Collection이나 하위 타입을 사용하는 것이 좋다.
- 반환할 컬렉션의 크기가 크지만 표현을 간결하게 할 수 있다면 전용 컬렉션 구현
    - 멱집합
        - 입력 집합의 역 집합을 전용 컬렉션에 담아 반환

        ```java
        public class PowerSet {
        
            public static final <E> Collection<Set<E>> of(Set<E> s) {
                List<E> src = new ArrayList<>();
                if (src.size() > 30)
                    throw new IllegalArgumentException("집합에 원소가 너무 많습니다(최대 30개). :" + s);
        
                return new AbstractList<Set<E>>() {
                    @Override
                    public Set<E> get(int index) {
                        Set<E> result = new HashSet<>();
                        for (int i = 0; index != 0; i++, index >>= 1)
                            if ((index & 1) == 1)
                                result.add(src.get(i));
                        return result;
                    }
        
                    @Override
                    public boolean contains(Object o) {
                        return o instanceof Set && src.containsAll((Set) o);
                    }
        
                    @Override
                    public int size() {
                        // 멱집합의 크기는 2를 원래 집합의 원소 수만큼 거듭제곱 것과 같다.
                        return 1 << src.size();
                    }
                };
            }
        }
        ```

        - AbstractCollection을 활용해서 Collection 구현체를 작성할 때는 Iterable용 메서드 외 contains, size 메서드를 구현해야 한다.
        - 불가능 할 때는 컬렉션보다는 스트림이나 Iterable을 반환하는게 더 좋다.
    - 입력 리스트의 모든 부분리스트를 스트림으로 반환

        ```java
        public class SubLists {
            public static <E> Stream<List<E>> of(List<E> list) {
                return Stream.concat(Stream.of(Collections.emptyList()), prefixes(list).flatMap(SubLists::suffixes));
            }
        
            private static <E> Stream<List<E>> suffixes(List<E> list) {
                return IntStream.range(0, list.size())
                        .mapToObj(start -> list.subList(start, list.size()));
            }
        
            private static <E> Stream<List<E>> prefixes(List<E> list) {
                return IntStream.rangeClosed(1, list.size())
                        .mapToObj(end -> list.subList(0, end));
            }
        }
        ```