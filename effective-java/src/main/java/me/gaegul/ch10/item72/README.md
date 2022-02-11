# 아이템 72. 표준 예외를 사용하라

### 표준 예외 재사용

- 많은 프로그래머에게 이미 익숙한 규약을 따르면서 제공되는 API를 사용하는 사람들이 익히기 쉬워진다.
- 예외 클래스가 적을수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게 걸린다.

### 대표적으로 사용되는 표준 예외

- `IllegalArgumentException`
    - 호출자가 인수로 부적절한 값을 넘길 때 사용되는 예외
- `IllegalStateException`
    - 대상 객체의 상태가 호출된 메서드를 수행하기 적합하지 않을 때 사용되는 예외
    - 제대로 초기화되지 않은 객체를 사용하려 할 때 던질 수 있다.
- `NullPointerException`
    - null 값을 허용하지 않은 메서드에서 null을 넘기면 관례상 해당 예외를 사용한다.
        - 관례가 아니면 `IllegalArgumentException`를 사용해야 하는 상황이다.
- `IndexOutOfBoundsException`
    - 어떤 시퀀스의 허용 범위를 넘는 값을 건넬 때 관례상 사용하는 예외
- `ConcurrentModificationException`
    - 단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수정하려할 때 사용하는 예외
    - 동시 수정을 정확하게 검출할 수 있는 안정된 방법은 없지만 문제가 생길 가능성을 알려주는 용도로 사용
- `UnsupportedOperationException`
    - 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때 사용하는 예외
    - 보통 인터페이스 구현체에서 메서드 일부를 구현할 수 없을 때 사용


### 표준 예외를 사용할 때

- `Exception, RunTimeException, Throwable, Error`는 직접 재사용하지 말자
    - 위 클래스들은 추상 클래스라고 생각해라
- 상황에 부합한다면 항상 표준 예외를 사용하자
- 예외의 이름뿐 아니라 예외가 던져지는 맥락도 부합될 때 재사용한다.
- 인수 값이 무엇이었든 어차피 실패했을거라면 `IllegalStateException`을, 그렇지 않다면 `IllegalArgumentException`을 던지자