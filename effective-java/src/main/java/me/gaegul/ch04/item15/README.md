# 아이템 15. 클래스와 멤버의 접근 권한을 최소화하라

- 정보은닉, 캡슐화 장점
    - 여러 컴포넌트를 병렬로 개발할 수 있기 때문에 개발 속도를 높인다.
    - 각 컴포넌트를 더 빨리 파악하여 디버깅할 수 있고, 다른 컴포넌트로 교체하는 부담도 적다.
    - 정보 은닉은 성능 최적화에 도움을 준다. 완성된 시스템을 프로파일링해 최적화할 컴포넌트를 정한 다음 다른 컴포넌트에 영향을 주지 않고 해당 컴포넌트만 최적화할 수 있다.
    - 외부에 거의 의존하지 않고 독자적으로 동작 할 수 있는 컴포넌트라면 재사용성을 높여준다.
    - 시스템 전체가 아직 완성되지 않은 상태에서 개별 컴포넌트의 동작을 검증할 수 있다.
- 접근 제한자
    - 모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 한다.
        - 패키지 외부에서 사용하지 않으면 `package-private` 선언
        - API가 아닌 내부 구현이 되어 언제든 수정할 수 있다.
    - 한 클래스에서만 사용하는 `package-private` 톱레벨 클래스나 인터페이스는 이를 사용하는 클래스 안에 `private static`으로 중첩시키자
        - 외부에서 클래스 하나만 접근한다.
        - 톱레벨 클래스 또는 인터페이스는 `package-private`로 선언하여 내부로 구현한다.

        ```java
        public class Foo {
        		static final String NAME = "foo";
        }

        public class Bar {
        		static final String NAME = "bar";
        }

        ===

        public class Main {

            public static void main(String[] args) {
                System.out.println(Foo.NAME + Bar.NAME);
            }

            private static class Foo {
                static final String NAME = "foo";
            }

            public static class Bar {
                static final String NAME = "bar";
            }
        }
        ```

- 멤버(필드, 메서드, 중첩 클래스, 중첩 인터페이스)에서 부여할 수 있는 접근 수준
    - `private`
        - 멤버를 선언한 톱레벨 클래스에서만 접근할 수 있다.
    - `package-private`
        - 멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다.
        - 접근 제한자를 명시하지 않았을 때 적용되는 패키지 접근 수준
        - 인터페이스의 멤버는 기본적으로 public이 적용
    - `protected`
        - package-private의 겁근 범위를 포함하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근할 수 있다.
        - public 클래스에서 `package-private`에서 `protected`로 변경되면 해당 맴버는 관리해야 한다. → protected 멤버는 적을수록 좋다.
    - `public`
        - 모든 곳에서 접근할 수 있다.
- 멤버 접근성 제어
    - 상위 클래스를 구현하는 메서드는 상위 클래스에서보다 좁게 설정할 수 없다.
    - 인터페이스를 구현할 때 모든 메서드를 `public`으로 선언해야 한다.
    - 테스트를 목적으로 클래스, 인터페이스, 멤버의 접근 범위는 `public` 클래스의 `private` 멤버를 `package-private`까지 풀어주는 것은 허용할 수 있지만 그 이상은 안 된다.
- public 클래스의 인스턴스 필드는 되도록 public이 아니여야 한다.
    - final이 아닌 인스턴스 필드를 public으로 선언하면 불변성을 가질 수 없다.
    - 길이가 0이 아닌 내열은 모두 변경 가능하니 주의해야 한다.
    - public static final 배열을 제공하면 클라이언트에서 그 배열의 내용을 수정할 수 있게 된다.

        ```java
        // 보안 허점이 존재
        public static final Thing[] PRIVATE_VALUES = new Thing[]{...};

        ===

        // public -> private 변경
        private static final Thing[] PRIVATE_VALUES = new Thing[]{};

        // public 불변 리스트 추가
        public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

        // PRIVATE_VALUES 복사본을 반환하는 public 메서드
        public static final Thing[] values() {
            return PRIVATE_VALUES.clone();
        }
        ```