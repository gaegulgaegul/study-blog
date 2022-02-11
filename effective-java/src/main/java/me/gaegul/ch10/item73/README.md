# 아이템 73. 추상화 수준에 맞는 예외를 던지자

### 수행되는 일과 관련 없어 보이는 예외 발생

- 메서드가 저수준 예외를 처리하지 않고 바깥으로 전파하는 상황
    - 내부 구현 방식을 드러내어 윗 레벨 API를 오염시킨다.
    - 다음 릴리즈에서 구현 방식을 바꾸면 다른 예외가 나와 클라이언트 프로그램에도 영향을 미친다.

### 예외 번역

- 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야 한다.

    ```java
    try {
        ... // 저수준 추상화를 이용한다.
    } catch(LowerLevelException e) {
        // 추상화 수준에 맞게 번역한다.
        throw new HigherLevelException(...);
    }
    ```

- AbstractSequentialList에서 수행되는 예외 번역

    ```java
    /**
     * 이 리스트 안의 지정한 위치의 원소를 반환한다.
     * @throws IndexOutOfBoundsException index가 범위 밖이라면,
     *         즉 ({@code index < 0 || index >= size()})이면 발생한다.
     */
    public E get(int index) {
        try {
            return listIterator(index).next();
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }
    ```


### 예외 연쇄

- 문제의 근본 원인인 저수준 예외를 고수준 예외에 실어 보내는 방식
- 별도의 접근자 메서드를 통해 필요하면 언제든 저수준 예외를 꺼내 볼 수 있다.

    ```java
    try {
        ... // 저수준 추상화를 이용한다.
    } catch(LowerLevelException cause) {
        // 저수준 예외를 고수준 예외에 실어 보낸다.
        throw new HigherLevelException(cause);
    }
    ```

- 예외 연쇄용 생성자

    ```java
    class HigherLevelException extends Exception {
        HigherLevelException(Throwable cause) {
            super(cause);
        }
    }
    ```

    - 대부분 표준 예외는 예외 연쇄용 생성자를 가진다. 그렇지 않은 예외라도 `Throwable`의 `initCause` 메서드를 통해 **원인**을 추가할 수 있다.

### 예외 예방

- 무턱대고 예외를 전파하는 것보다 예외 번역이 우수하지만 남용되면 안 된다.
    - 가능하면 저수준 메서드가 반드시 성공하도록 하여 아래 계층에서는 예외가 발생하지 않도록 하는 것이 최선이다.
    - 아래 계층 메서드로 매개변수를 전달하기 전에 미리 검사하는 방법을 사용해야 한다.
- 아래 계층에서 예외를 피할 수 없다면 상위 계층에서 그 예외를 처리하여 API 호출자에까지 전파하지 않고 적절한 로깅 기능을 활용하여 기록해두면 좋다.