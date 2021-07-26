# 아이템 6. 불필요한 객체 생성을 피하라

- 객체를 매번 생성하기 보다 하나를 재사용하는 편이 좋다.
    - 하지 말아야 할 예시

        ```java
        String s = new String("bikini"); // 따라 하지 말 것!
        ```

        - 위 코드는 실행 할 때마다 객체를 새로 생성한다.
        - 빈번하게 호출되는 메서드에 있다면 String 인스턴스가 수백만개 만들어진다.

        ```java
        String s = "bikini";
        ```

        - 하나의 인스턴스를 사용하는 방식
        - JVM 안에서 이와 같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다.
- 생성자 대신 정적 팩터리 메서드를 사용하는 것이 좋다.
    - `new Boolean(String)` → `Boolean.valueOf(String)`
    - 불변 객체만 아니라 가변 객체도 사용 중에 불변이 인정되면 재사용할 수 있다.
    - 생성 비용이 비싼 객체는 **캐싱을 통해 재사용 권장**
    - 예시
        - 주어진 문자열이 로마 숫자인지 확인하는 메서드

        ```java
        static boolean isRomanNumeral(String s) {
        		return s.matches("^(=?.)M*(C[MD]|D?C{0,3})"
        						+ "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
        }
        ```

        - String.matches
            - 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만, 성능이 중요한 상황에서 반복해 사용하기엔 적합하지 않다.
            - 메서드 내부의 Pattern 인스턴스는 한번 사용하고 가비지 컬렉터 대상이 된다.

        ```java
        public class RomanNumerals {

            private static final Pattern ROMAN = Pattern.compile("^(=?.)M*(C[MD]|D?C{0,3})"
                    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
            
            static boolean isRomanNumerals(String s) {
                return ROMAN.matcher(s).matches();
            }

        } 
        ```

        - Pattern 인스턴스를 클래스 초기화 과정에서 직접 생성해 캐싱한다. isRomanNumerals 메서드가 호출되면 재사용한다.
        - isRomanNumerals 메서드가 한번도 호출되지 않으면 ROMAN 필드는 쓸데없이 초기화된 것
- 객체의 불변이 덜 명확하거나 아예 명확하지 않을 때
    - Map 인터페이스의 keySet 메서드
        - keySet을 호출 할 때마다 새로운 set 인스턴스를 반환하지 않고 매번 같은 set을 반환한다.
        - 반환된 set 인스턴스가 가변이라도 반환된 인스턴스들은 기능적으로 똑같다.
- 오토박싱
    - 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐려주지만, 완전히 없애주는 것은 아니다.
    - 예제

        ```java
        private static long sum() {
        		Long sum = 0L;
        		for (long i = 0; i <= Integer.MAX_VALUE; i++) {
        				sum += i;
        		}
        		return sum;
        }
        ```

        - Long으로 선언된 sum 변수는 2의 32승만큼 박싱이 일어난다.
    - 의도하지 않은 오토박싱이 숨어들지 않도록 주의
- 주의사항
    - "객체 생성은 비싸니 피해야 한다"로 오해하면 안된다. JVM에서 별다른 일을 하지 않은 작은 객체를 생성하고 회수하는 일이 부담되지 않는다.
    - 아주 무거운 객체가 아니고서 단순히 객체 생성을 피하고자 객체 풀을 만들면 안된다.
    - DB 연결은 생성 비용이 비싸니 재사용하는 편이 좋다.