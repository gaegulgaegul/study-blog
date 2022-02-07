# 아이템 77. 예외를 무시하지 말라

### 예외 처리

- API 설계자가 메서드 선언에 예외를 명시하는 이유는 **메서드를 사용할 때 적절한 조치를 취해달라는 것**
    - 메서드 호출을 try문으로 감싸고 catch 블록에서 아무것도 하지 않는다면 끝이다!

        ```java
        // catch 블록을 비워두면 예외가 무시된다.
        try {
            ...
        } catch (SomeException e) {
        }
        ```

- catch 블록을 비워두면 예외가 존재할 이유가 사라진다.
    - 화재경보를 무시하는 수준과 같다.
- 예외를 무시해야 할 때
    - FileInputStream을 닫을 때, 파일의 상태를 변경하지 않으니 복구할 것이 없다.
    - 예외를 무시하기로 했다면 catch 블록 안에 무시하기로 한 이유를 주석으로 남기고 예외 변수의 이름도 ignored로 바꾼다.

        ```java
        Future<Integer> f = exec.submit(planarMap::chromaticNumber);
        int numColors = 4; // 기본값. 어떤 지도라도 이 값이면 충분하다.
        try {
            numColors = f.get(1L, TimeUnit.SECONDS);
        } catch(TimeoutException | ExecutionException ignored) {
            // 기본값을 사용한다(색상 수를 최소화하면 좋지만, 필수는 아니다).
        }
        ```


### 검사와 비검사 예외

- 예측할 수 있는 상황, 프로그래밍 오류를 빈 catch 블록을 사용하면 오류를 내재한 상태로 동작한다.
- 알 수 없는 원인으로 프로그램이 갑자기 죽을 수 있다.
- 예외를 적절하게 처리하거나 바깥으로 전파하도록 하자