# 아이템 63. 문자열 연결은 느리니 주의하라

### 문자열 연결 연산자(+)

- 여러 문자열을 하나로 합쳐주는 편리한 수단
- 문자열 n개를 잇는 시간은 n^2에 비례한다.

```java
private static String statement(List<String> items) {
    String result = "";
    for (int i = 0; i < items.size(); i++) {
        result += items.get(i);
    }
    return result;
}
```

### StringBuilder

- 성능을 포기하고 싶지 않다면 StringBuilder를 사용하자

    ```java
    private static String statement2(List<String> items) {
        final int lineWidth = 100;
        StringBuilder b = new StringBuilder(items.size() * lineWidth);
        for (int i = 0; i < items.size(); i++) {
            b.append(items.get(i));
        }
        return b.toString();
    }
    ```

- String의 수행 시간은 제곱에 비례해 늘어나고 StringBuilder는 선형으로 늘어난다.