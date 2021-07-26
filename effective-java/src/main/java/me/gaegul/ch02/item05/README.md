# 아이템 5. 자원을 직접 명시하지 않고 의존 객체 주입을 사용하라

- 정적 유틸리티를 잘못 사용한 예

    ```java
    public class SpellChecker {
        private static final Lexicon dictionary = ...
        
        private SpellChecker() {} // 객체 생성 방지
        
        private static boolean isValid(String word) {
            ...
        }
        
        private static List<String> suggestions(String typo) {
            ...
        }
    }
    ```

- 싱글턴을 잘못 사용한 예

    ```java
    public class SpellChecker {
        private final Lexicon dictionary = ...
        
        private SpellChecker(...) {}
        
        public static SpellChecker INSTANCE = new SpellChecker(...);
        
        public boolean isValid(String word) {
            ...
        }
        
        public List<String> suggestions(String typo) {
            ...
        }
    }
    ```

    - 두 방식 모두 사전을 단 하나만 사용한다고 가정한다는 점이 유연하지 않다.
    → 여러 사전을 사용할 수 있음
    - 유연하지 않고 테스트하기 어렵다.
- SpellChecker가 여러 사전을 이용할 수 있도록 만든다.
    - 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸 클래스나 싱글턴 방식이 적합하지 않다.
    - ***클래스가 여러 자원 인스턴스를 지원해야 하며 클라이언트가 원하는 자원을 사용해야 한다.
    → 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식***
    - 객체 의존 주입
        - 생성자, 정적 팩터리, 빌더 모두 똑같이 응용할 수 있다.
        - 유연성과 테스트 용이성을 개선하지만 의존성이 많은 프로젝트에서는 코드를 어지럽게 만든다.

    ```java
    public class SpellChecker {
        
        private final Lexicon dictionary;

        public SpellChecker(Lexicon dictionary) {
            this.dictionary = Objects.requireNonNull(dictionary);
        }
        
        public boolean isValid(String word) {
            ...
        }
        
        public List<String> suggestions(String typo) {
            ...
        }
    }
    ```

- 핵심 정리
    - 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자월이 클래스 동작에 영향을 주는 경우 싱글턴과 정적 팩터리 메서드는 사용하지 않는다. 또한 자원을 클래스가 만들게 하지 않는다.
    - 객체 의존 주입을 통해 필요한 자원을 생성자에 넘겨준다.