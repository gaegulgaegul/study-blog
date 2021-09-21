# 아이템 36. 비트 필드 대신 EnumSet을 사용하라

### 비트 필드 열거 상수

```java
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 3
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 4
    
    // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
    public void applyStyles(int styles) {}
}
```

- 비트 필드를 사용하면 비트별 연산을 사용해 집합 연산(합집합, 교집합)을 효율적으로 수행할 수 있다.
- 비트 필드는 정수 열거 상수의 단점을 그대로 가지고 있다.
- 비트 필드 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기 더 어렵다.

### EnumSet

- 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현한다.
- EnumSet 내부는 비트 벡터로 구성되어 있다.
- 원소가 총 64개 이하라면 EnumSet 전체를 long 변수 하나로 표현하여 비트 필드와 비슷한 성능을 보인다.

```java
public class Text {

    public enum Style {
        BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
    }
    
    public void applyStyles(Set<Style> styles) {}
}
```

- applyStyles 매개변수가 Set<Style>인 이유
    - 인터페이스를 받는 게 일반적으로 좋은 습관
    - 클라이언트에서 넘기는 다른 Set 구현체를 넘기더라도 처리할 수 있다.