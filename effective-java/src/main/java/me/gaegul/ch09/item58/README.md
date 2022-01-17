# 아이템 58. 전통적인 for 문보다는 for-each 문을 사용하라

### 반복문을 사용하는 방법

- 반복자와 인덱스 변수는 코드를 지저분하게 할뿐이고 우리에게 필요한건 원소뿐이다.
- 전통적인 for문의 문제
    - 1회 반복에 반복자는 세 번 등장하고 인덱스는 네 번 등장하여 변수를 잘못 사용할 수 있다.
    - 잘못된 변수를 사용했을 때 컴파일러가 잡아주지 못할 수도 있다.
    - 컬렉션이냐 배열이냐에 따라 코드 형태가 달라진다.

→ for-each 문을 사용하면 해결된다.

### 향상된 for 문

- 반복자와 인덱스를 사용하지 않아 코드가 깔끔해지고 오류가 일어날 일도 없다.
- 하나의 관용구로 컬렉션과 배열 모두 처리할 수 있다.

    ```java
    for (Element e : elements) {
        ... // e로 무언가를 한다.
    }
    ```

- 컬렉션을 중첩해서 순회한다면 for-each 문이 좋다.
    - iterator의 next()는 원소 하나의 한번만 호출해야 된다.

        ```java
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();)
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();)
                deck.add(new Card(i.next(), j.next()));
        
        deck.forEach(System.out::println);
        
        // console
        java.util.NoSuchElementException 발생
        ```


### for-each를 사용할 수 없는 상황

- 파괴적인 필터링
    - 컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 반복자의 remove를 호출해야 된다.
    - 자바 8부터 Collection의 removeIf 메서드로 대체 한다.
- 변형
    - 리스트나 배열을 순회하면서 원소의 값 일부 또는 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다.
- 병렬 반복
    - 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다.