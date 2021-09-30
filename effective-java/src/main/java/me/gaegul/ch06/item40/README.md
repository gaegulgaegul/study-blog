# 아이템 40. @Override 애너테이션을 일관되게 사용하라

### @Override 애너테이션을 일관되게 사용하면 여러 가지 버그들을 예방

- 영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
    
    ```java
    public class Bigram {
        private final char first;
        private final char second;
    
        public Bigram(char first, char second) {
            this.first = first;
            this.second = second;
        }
    
        // 다중 정의 되었다.
        public boolean equals(Bigram b) {
            return b.first == first && b.second == second;
        }
    
        public int hashCode() {
            return 31 * first + second;
        }
    
        public static void main(String[] args) {
            Set<Bigram> s = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                for (char ch = 'a'; ch <= 'z'; ch++) {
                    s.add(new Bigram(ch, ch));
                }
            }
            System.out.println(s.size());
        }
    }
    
    // console
    260
    ```
    
    - equals 재정의 하려면 Object를 매개변수 타입으로 받아야 한다.
    - Object에서 상속한 equals와는 별개인 equals를 새로 정의한 형식이다.
- @Override 애너테이션 추가
    
    ```java
    @Override
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }
    ```
    
    - 매개변수 타입이 Object가 아니여서 컴파일 오류가 발생한다.
- 올바른 @Override
    
    ```java
    @Override
        public boolean equals(Object o) {
            if (!(o instanceof Bigram)) return false;
            Bigram b = (Bigram) o;
            return b.first == first && b.second == second;
        }
    ```
    
    - 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Overrid 애너테이션 추가