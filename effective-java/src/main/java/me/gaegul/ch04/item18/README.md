# 아이템 18. 상속보다는 컴포지션을 사용하라

> *이 주제는 클래스가 다른 클래스를 확장하는 구현 상속에 대해 말한다.(인터페이스와 무관)*

- 상속은 캡슐화를 깨뜨린다.
    - 상위 클래스가 어떻게 구현되는냐에 따라 하위 클래스의 동작 이상이 생길 수 있다.
    - 상위 클래스 설계자는 확장을 출분히 고려하고 문서화도 제대로 해야한다.
    - 상속의 잘못된 예 - HashSet 구현

        ```java
        public class InstrumentedHashSet<E> extends HashSet<E> {

            private int addCount = 0;

            public InstrumentedHashSet() {}

            public InstrumentedHashSet(int initCap, float loadFactor) {
                super(initCap, loadFactor);
            }

            @Override
            public boolean add(E e) {
                addCount++;
                System.out.println("add~");
                return super.add(e);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                addCount += c.size();
                
                // 3을 더한 후 상위 HashSet의 addAll 호출
                System.out.println("addAll : " + addCount);

                // 상위 HashSet의 add가 호출되지 않고 현재 재정의 된 InstrumentedHashSet의 add가 호출되었다.
                return super.addAll(c);
            }

            public int getAddCount() {
                return addCount;
            }

            public static void main(String[] args) {
                InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
                s.addAll(Arrays.asList("하나", "둘", "셋"));
                System.out.println(s.getAddCount()); // 6
            }
        }
        ```

- 컴포지션
    - 기존 클래스를 확장하는 대신 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조한다.
    - 새 클래스의 인스턴스 메서드들은 기존 클래스의 메서드를 반환한다. → 전달 메서드(forwarding method)
        - guava는 모든 컬렉션 인터페이스용 전달 메서드를 전부 구현되어 있다.
        - Set 구현체 전달 클래스

        ```java
        public class ForwardingSet<E> implements Set<E> {
            private final Set<E> s;

            public ForwardingSet(Set<E> s) { this.s = s; }

            @Override
            public int size() { return s.size(); }

            @Override
            public boolean isEmpty() { return s.isEmpty(); }

            @Override
            public boolean contains(Object o) { return s.contains(o); }

            @Override
            public Iterator<E> iterator() { return s.iterator(); }

            @Override
            public Object[] toArray() { return s.toArray(); }

            @Override
            public <T> T[] toArray(T[] a) { return s.toArray(a); }

            @Override
            public boolean add(E e) { return s.add(e); }

            @Override
            public boolean remove(Object o) { return s.remove(o); }

            @Override
            public boolean containsAll(Collection<?> c) { return s.containsAll(c); }

            @Override
            public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }

            @Override
            public boolean retainAll(Collection<?> c) { return s.retainAll(c); }

            @Override
            public boolean removeAll(Collection<?> c) { return s.removeAll(c); }

            @Override
            public void clear() { s.clear(); }

            @Override
            public int hashCode() { return s.hashCode(); }

            @Override
            public boolean equals(Object obj) { return s.equals(obj); }

            @Override
            public String toString() { return s.toString(); }
        }
        ```

    - 컴포지션을 사용한 Wrapper 클래스

        ```java
        public class InstrumentedSet<E> extends ForwardingSet<E> {

            private int addCount = 0;

            public InstrumentedSet(Set<E> s) {
                super(s);
            }

            @Override
            public boolean add(E e) {
                System.out.println("add~"); // 호출 안됨
                addCount++;
                return super.add(e);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                addCount += c.size();
                return super.addAll(c);
            }

            public int getAddCount() {
                return addCount;
            }

            public static void main(String[] args) {
        				// InstrumentedSet -> 다른 Set 인스턴스를 감싸고 있다.
        				// 데코레이터 패턴
                InstrumentedSet<String> s = new InstrumentedSet<>(new TreeSet<>());
                s.addAll(Arrays.asList("하나", "둘", "셋"));
                System.out.println(s.getAddCount()); // 3
            }
        }
        ```

        - 래퍼 클래스의 단점
            - 콜백 프레임워크와 어울리지 않는다.
                - 자기 자신의 참조를 다른 객체에 넘겨 콜백으로 사용하게 되는데 내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 this를 넘기고 콜백에 래퍼가 아닌 내부 객체를 호출하게 된다.
- 상속을 사용하기 전 컴포지션을 고려해야한다.
    - 확장하려는 클래스의 결함이 있다면 상속 클래스도 같은 결함을 가진다.