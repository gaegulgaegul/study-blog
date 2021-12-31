# 아이템 54. null이 아닌, 빈 컬렉션이나 배열을 반환하라

### null 반환

```java
private final List<Cheese> cheesesInStock = Collections.emptyList();
    
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);
}

// 클라이언트 코드
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null && cheeses.contains(Cheese.STILTON))
    System.out.println("좋았어 바로 그거야.");
```

- 클라이언트에서 null을 반드시 처리해야 한다. 처리하지 않으면 오류가 발생할 것이다.

### 빈 컬렉션 또는 배열 반환

```java
// 컬렉션 반환
public List<Cheese> getCheesesInStock() {
    return cheesesInStock.isEmpty()
            ? Collections.emptyList()
            : new ArrayList<>(cheesesInStock);
}

// 배열 반환
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeseArray() {
    return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

- 빈 컬렉션을 반환하는 경우 Collections에서 제공하는 빈 컬렉션 반환 메서드를 사용하는 것이 좋다.
- 빈 배열을 반환할 때 길이 0짜리 배열을 만들어 전달하는 방식을 사용한다.
- 만약 0짜리 배열이 아니라 컬렉션 사이즈의 배열을 생성하여 반환한다면 성능상 문제가 발생할 것이다.

    ```java
    cheesesInStock.toArray(new Cheese[cheesesInStock.size()]);
    ```