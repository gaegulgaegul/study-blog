# 아이템 28. 배열보다는 리스트를 사용하라

- 배열과 제네릭 타입의 차이
    - 배열은 공변, 제네릭은 불공변
    → 공변: 함께 변한다.

        ```java
        Object[] objectArray = new Long[1]; // ArrayStoreException 발생
        objectArray[0] = "타입이 달라 넣을 수 없다.";

        List<Object> ol = new ArrayList<Long>(); // 호환되지 않는 타입
        ol.add("타입이 달라 넣을 수 없다.");
        ```

    - 배열은 실체화된다.
        - 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다.
        - 제네릭은 런타임에 타입 정보가 소거된다.
        - 제네릭 타입의 배열을 생성하면 컴파일 시 오류가 발생한다.
            - `new List<E>[], new List<String>[], new E[]`
            - 타입이 안전하지 않다.
                - 만약 제네릭 배열을 생성하도록 허용한다면 런타임 시 ClassCastException이 발생한다.
- 실체화 불가 타입
    - 런타임 시 컴파일타임보다 타입 정보를 적게 가지는 타입
    - `E, List<E>, List<String>`
- 제네릭 컬렉션
    - 제네릭 컬렉션에서 자신의 원소 타입을 담은 배열을 반환하는 것이 불가능
    - 제네릭 타입과 가변인수 메서드를 함께 쓰면 해석하기 어려운 경고 메시지가 출력된다.
        - 가변인수 메서드를 호출할 때마다 가변인수 매개변수를 담을 배열이 하나 만들어진다.
        - 해당 배열의 원소가 실체화 불가 타입이라면 경고가 발생한다.
        - `@SafeVarargs`으로 대처할 수 있다.
    - 배열로 형변환할 때 제네릭 배열 생성 오류나 비검사 형변환 경고가 나면 List<E> 컬렉션은 이용한다.
- 생성자에서 컬렉션을 받는 Chooser

    ```java
    public class Chooser {
        
        private final Object[] choiceArray;

        public Chooser(Collection choices) {
            this.choiceArray = choices.toArray();
        }
        
        // 이 메서드를 호출할 때마다 Object를 형변환 해줘야 한다.
        public Object choose() {
            Random rnd = ThreadLocalRandom.current();
            return choiceArray[rnd.nextInt(choiceArray.length)];
        }
    }
    ```

- 제네릭을 적용한 chooser

    ```java
    public class Chooser<T> {

        private final T[] choiceArray;

        public Chooser(Collection<T> choices) {
            this.choiceArray = (T[]) choices.toArray();
            // 배열을 제네릭 타입으로 형변환 했지만 T가 어떤 타입인지 알 수 없어
            // 컴파일러는 이 형변환이 런타임에 보장될 수 없다고 판단한다.
            // 런타임 시에 제네릭 타입은 소거된다.
        }

        // choose 메서드는 그대로
    }
    ```

- 리스트 기반 Chooser

    ```java
    public class Chooser<T> {

        private final List<T> choiceList;

        public Chooser(Collection<T> choices) {
            this.choiceList = new ArrayList<>(choices);
        }

        public T choose() {
            Random rnd = ThreadLocalRandom.current();
            return choiceList.get(rnd.nextInt(choiceList.size()));
        }
    }
    ```