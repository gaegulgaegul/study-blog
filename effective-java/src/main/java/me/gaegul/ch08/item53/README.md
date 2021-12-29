# 아이템 53. 가변인수는 신중히 사용하라

### 가변인수 메서드

- 명시한 타입의 인수를 0개 이상 받을 수 있다.
- 호출 시 인수의 개수와 길이가 같은 배열을 만들고 인수들을 배열에 저장하여 가변인수 메서드에 전달한다.

### 가변인수 활용

```java
public class Varargs {
    public static void main(String[] args) {
        System.out.println("sum(1, 2, 3) => " + sum(1, 2, 3));
    }

    static int sum(int... args) {
        int sum = 0;
        for (int arg : args) {
            sum += arg;
        }
        return sum;
    }
}

// console
sum(1, 2, 3) => 6
```

### 인수가 1개 이상이어야 하는 가변인수 활용

- 잘못 구현한 예시
    - 인수를 0개를 넣으면 컴파일 에러가 발생하지 않고 런타임 에러가 발생한다.

```java
static int min(int... args) {
    if (args.length == 0)
        throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
    int min = args[0];
    for (int i = 1; i < args.length; i++) {
        if (args[i] < min)
            min = args[i];
    }
    return min;
}
```

- 제대로 구현된 예시

```java
static int min(int firstArg, int... args) {
    int min = firstArg;
    for (int arg : args) {
        if (arg < min)
            min = arg;
    }
    return min;
}
```

### 가변인수는 인수 개수가 정해지지 않았을 때는 아주 유용하다.

- printf는 가변인수와 한 묶음으로 자바에 도입되었고 리플렉션도 재정비 되었다.

### 가변인수의 유연성

```java
public void foo() {}
public void foo(int a1) {}
public void foo(int a1, int a2) {}
public void foo(int a1, int a2, int a3) {}
public void foo(int a1, int a2, int a3, int... rest) {}
```

- 가변인수 메서드는 호출 될때마다 배열을 하나 새로 할당하고 초기화한다.
- 메서드 호출의 95%가 인수 3개 이하로 사용한다고 가정했을 때, 인수가 0~4개인 것까지 5개의 메서드를 다중정의한다.
- 마지막 메서드를 통해 5%의 메서드 호출을 담당한다.
- EnumSet의 정적 팩터리도 이 방식을 사용하여 열거 타입 집합 생성 비용을 최소화 한다.