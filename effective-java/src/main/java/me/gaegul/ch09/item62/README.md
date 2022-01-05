# 아이템 62. 다른 타입이 적절하다면 문자열 사용을 피하라

### 문자열은 다른 값 타입을 대신하기에 적합하기 않다.

- 받는 데이터가 수치형이라면 `int, float, BigInteger 등` 적당한 수치 타입으로 변환해야 한다.
- 예/아니오 질문의 답이라면 열거 타입이나 `boolean`으로 변환해야 한다.

### 문자열은 열거 타입을 대신하기에 적합하지 않다.

- 상수를 열거할 때는 문자열보다 열거 타입이 월등히 낫다.

### 문자열은 혼합 타입을 대신하기에 적합하지 않다.

- 여러 오소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 좋지 않다.

    ```java
    String compoundKey = className + "#" + i.next();
    ```

    - 두 요소에서 문자 #이 사용되었으면 결과가 일정하지 않다.
    - 문자열을 파싱해 사용해야 해서 느리고 오류 가능성이 커진다.
    - `equals, toString, compareTo` 메서드를 제공할 수 없다.

  → private 정적 멤버 클래스로 선언해야 한다.


### 문자열은 권한을 표현하기에 적합하지 않다.

- 클라이언트가 제공한 문자열 키로 스레드별 지역변수 식별

    ```java
    public class ThreadLocal {
    
        private ThreadLocal() {}
    
        // 현 스레드의 값을 키로 구분해 저장한다.    
        public static void set(String key, Object value) {}
        
        // (키가 가리키는) 현 스레드의 값을 반환한다.
        public static Object get(String key) {
            return null;
        }
    }
    ```

    - 문제점
        - 스레드 구분용 문자열 키가 전역 이름공간에서 공유된다.
        - 각 클라이언트가 고유한 키를 제공해야 한다.
        - 클라이언트의 키가 중복되면 변수를 공유하게 된다.
- Key 클래스로 권한을 구분

    ```java
    public class ThreadLocal {
    
        private ThreadLocal() {}
    
        public static class Key {
            Key() {}
        }
        
        // 위조 불가능한 고유 키를 생성한다.
        public static Key getKey() {
            return new Key();
        }
    
        // 현 스레드의 값을 키로 구분해 저장한다.
        public static void set(Key key, Object value) {}
    
        // (키가 가리키는) 현 스레드의 값을 반환한다.
        public static Object get(Key key) {
            return null;
        }
    }
    ```

    - 문자열 기반의 API의 문제를 모두 해결한다.
    - 개선할 여지
        - `set`과 `get`은 정적 메서드일 필요가 없다. → `Key` 클래스의 인스턴스 메서드로 변경
        - 현재 톱레벨 클래스 `ThreadLocal`의 기능이 없으므로 중첩 클래스 `Key`의 이름을 `ThreadLocal`로 바꾼다.
- `Key`를 `ThreadLocal`로 변경

    ```java
    public final class ThreadLocal {
    
        private ThreadLocal() {}
        
        public void set(Object value) {}
    
        public Object get() {
            return null;
        }
    }
    ```

    - get으로 얻은 Object를 실제 타입으로 형변환해 써야 해서 타입 안전하지 않다. → 매개변수화
- 매개변수화하여 타입안전성 확보

    ```java
    public final class ThreadLocal<T> {
    
        private ThreadLocal() {}
    
        public void set(T value) {}
    
        public T get() {
            return null;
        }
    }
    ```