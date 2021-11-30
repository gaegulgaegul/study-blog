# 아이템 50. 적시에 방어적 복사본을 만들라

### 자바는 안전한 언어다.

- 네이티브 메서드를 사용하지 않는다.
- C, C++에서 자주 보는 버퍼 오버런, 배열오버런, 와일드 포인터 등의 메모리 충돌 오류에서 안전하다.
- 하지만 다른 클래스로부터 침범을 다 막을 수 있는건 아니다.

<aside>
💡 클라이언트가 불변식을 깨뜨린다고 가정하고 방어적으로 프로그래밍을 해야 한다.

</aside>



### 기간을 표현하는 클래스

- 불변식을 지키지 못하는 클래스

    ```java
    public final class Period {
        private final Date start;
        private final Date end;
    
        /**
         * @param start 시작 시각
         * @param end 종료 시각; 시작 시각보다 뒤여야 한다.
         * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
         * @throws NullPointerException start나 end가 null이면 발생한다.
         */
        public Period(Date start, Date end) {
            if (start.compareTo(end) > 0) {
                throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
            }
            this.start = start;
            this.end = end;
        }
    
        public Date getStart() {
            return start;
        }
    
        public Date getEnd() {
            return end;
        }
    
        public static void main(String[] args) {
            Date start = new Date();
            Date end = new Date();
            Period p = new Period(start, end);
            end.setYear(78); // p의 내부를 수정했다.
        }
    }
    ```

    - Period 클래스만 보면 불변식이 깨지지 않을 것 같지만 변수인 Date가 가변이라는 사실을 이용하면 쉽게 깨진다.
    - Period 대신 Instant를 사용한다.(LocalDateTime, ZonedDateTime 또한 사용)
- 생성자에서 받은 가변 매개변수 각각을 방어적으로 복사해야 한다.

    ```java
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
    
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }
    }
    ```

    - 유효성 검사 전 방어적 복사본을 만들고, 복사본을 통해 유효성을 검사했다.
        - 순서가 다르지만 방어적 복사를 사용할 경우 위와 같은 순서로 진행한다.
    - Date의 clone을 사용하지 않았다.
        - Date는 final이 아니므로 clone이 Date가 정의한 게 아닐 수 있다.
        - 매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 clone을 사용하면 안된다.
- 가변 필드의 방어적 복사본을 반환한다. - 접근자

    ```java
    public Date getStart() {
        return new Date(start.getTime());
    }
    
    public Date getEnd() {
        return new Date(end.getTime());
    }
    ```

    - Period 내부의 가변필드에 접근할 방법이 없으니 확실하다. 모든 필드가 객체 안에 완벽하게 캡슐화되었다.
    - 접근자 메서드는 방어적 복사에 clone을 사용해도 된다. 하지만 인스턴스를 복사하는데 일반적으로 생성자나 정적 팩터리를 쓰는게 좋다.

### 되도록 불변 객체들을 조합해 객체를 조합해 객체를 구성

- 메서드든 생성자든 클라이언트가 제공한 객체의 참조를 내부 자료구조에 보관할 때 객체가 변경될 수 있는지 생각해야 한다.
    - 변경될 수 있다면 그 객체가 클래스에 넘겨진 뒤 임의로 변경되어도 그 클래스가 문제없이 동작할지를 확인해야 한다.
    - 확신할 수 없다면 복사본을 만들어 저장해야 한다.
- 방어적 복사는 성능저하가 따르고 항상 사용할 수 있지 않다.
    - 호출자가 컴포넌트를 수정하지 않는다고 확신되면 방어적 복사를 생략할 수 있다.
    - 다른 패키지를 사용한다고 해서 넘겨받은 가변 매개변수를 할상 방어적으로 복사해 저장하는 것은 아니다.

  → 호출하는 쪽에서 해당 객체를 수정하지 않는다는 확신이 있어야 한다. 메서드나 생성자에 내용을 문서화해야 한다.