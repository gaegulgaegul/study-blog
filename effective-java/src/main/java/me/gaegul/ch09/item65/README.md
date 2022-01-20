# 아이템 65. 리플렉션보다는 인터페이스를 사용하라

### 리플렉션

- 프로그램에서 임의의 클래스에 접근할 수 있다.
- Class 객체가 주어지면 생성자, 메서드, 필드에 해당하는 인스턴스를 가져올 수 있고 동작 수행 및 코드 조작이 가능하다.

### 리플렉션의 단점

- 컴파일타임 타입 검사가 주는 이점을 하나도 누릴 수 없다.
    - 프로그램이 리플렉션의 기능으로 존재하지 않는 혹은 접근할 수 없는 메서드를 호출하려 한다면 런타임 오류가 발생한다.
- 리플렉션을 이용하면 코드가 지저분하고 장황해진다.
- 성능이 떨어진다.
    - 리플렉션을 통한 메서드 호출은 일반 메서드 호출보다 훨씬 느리다.

→ 리플렉션은 아주 제한된 형태로만 사용해야 그 단점을 피하고 이점만 취할 수 있다.

### 리플렉션으로 생성하고 인터페이스로 참조

```java
public static void main(String[] args) {
    // 클래스 이름을 Class 객체로 변환
    Class<? extends Set<String>> cl = null;
    try {
        cl = (Class<? extends Set<String>>) Class.forName(args[0]);
    } catch (ClassNotFoundException e) {
        fatalError("클래스를 찾을 수 없습니다.");
    }

    // 생성자를 얻는다.
    Constructor<? extends Set<String>> cons = null;
    try {
        cons = cl.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
        fatalError("매개변수 없는 생성자를 찾을 수 없습니다.");
    }

    // 집합의 인스턴스를 만든다.
    Set<String> s = null;
    try {
        s = cons.newInstance();
    } catch (InstantiationException e) {
        fatalError("클래스를 인스턴스화할 수 없습니다.");
    } catch (IllegalAccessException e) {
        fatalError("생성자에 접근할 수 없습니다.");
    } catch (InvocationTargetException e) {
        fatalError("생성자가 예외를 던졌습니다: " + e.getCause());
    } catch (ClassCastException e) {
        fatalError("Set을 구현하지 않은 클래스 입니다.");
    }

    // 생성한 집합을 사용한다.
    s.addAll(Arrays.asList(args).subList(1, args.length));
    System.out.println(s);
}

private static void fatalError(String msg) {
    System.err.println(msg);
    System.exit(1);
}
```

- 리플렉션의 단점
    - 런타임에 총 여섯 가지 예외를 던질 수 있다.
    - 클래스 이름만으로 인스턴스를 생성하기 위해 25줄의 코드를 작성했다.
    - 컴파일하면 비검사 형변환 경고가 발생한다.
        - Class<? extends Set<String>>으로의 형변환은 명시한 클래스가 Set을 구현하지 않더라고 성공할 것이다.
        - 실제 문제는 인스턴스를 생성하려 할 때 ClassCastException을 던진다.
- 리플렉션이 필요한 곳
    - 런타임에 존재하지 않을 수 있는 다른 클래스, 메서드, 필드와의 의존성을 관리할 때 적합하다.
      → 버전이 여러 개 존재하는 외부 패키지를 다룰 때 유용하다.