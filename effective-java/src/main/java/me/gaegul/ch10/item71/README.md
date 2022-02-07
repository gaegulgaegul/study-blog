# 아이템 71. 필요 없는 검사 예외 사용은 피하라

### 검사 예외는 발생한 문제를 프로그래머가 처리하여 안전성을 높인다.

- 검사 예외를 과하게 사용하면 안 된다.
- 메서드가 검사 예외를 던질 수 있다고 선언되어 있으면 호출하는 쪽에서 catch 블록으로 처리해야 한다.
- 스트림 안에서 예외를 던지는 메서드를 직접 사용할 수 없다.
- API를 제대로 사용해도 발생할 수 있는 예외 또는 프로그래머가 의미 있는 조치를 취할 경우가 아니라면 비검사 예외를 사용하는게 좋다.
- 예외를 어떻게 다룰 것인지 생각해야 한다.

    ```java
    ...
    } catch(TheCheckedException e) {
        throw new AssertionException();
    }
    
    ...
    } catch(TheCheckedException e) {
        e.printStackTrace();
        System.exit(1);
    }
    ```


### 검사 예외 회피

- 옵셔널 반환
    - 검사 예외를 던지는 대신 빈 옵셔널을 반환
- 검사 예외 메서드를 2개로 쪼개 비검사 예외로 바꿀 수 있다.
    - 예외가 던져질지 여부를 `boolean`으로 반환

        ```java
        // 리펙터링 전
        try {
            obj.action(args);
        } catch(TheCheckedException e) {
            ... // 예외 상황에 대처한다.
        }
        
        // 리펙터링 후
        if (obj.actionPermitted(args)) {
            obj.action(args);
        } else {
            ... // 예외 상황에 대처한다.
        }
        ```

        - `actionPermitted` 메서드는 상태 검사 메서드에 해당하여 잠재적인 단점을 고려해야 한다.
            - 외부 동기화 없이 여러 스레드가 동시에 접근할 수 있거나 외부 요인에 의해 상태가 변할 수 있다면 리펙터링을 하면 안 된다.
            - `actionPermitted`가 `action` 메서드의 작업 일부를 중복 수행한다면 성능에서 손해이니 리펙터링을 하면 안 된다.