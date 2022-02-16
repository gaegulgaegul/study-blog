# 아이템 87. 커스텀 직렬화 형태를 고려해보라

### 기본 직렬화 형태

- **직렬화하는 시점에 특정 객체에 관련된 모든 데이터를 직렬화한다**는 뜻
    - 기본 직렬화 형태는 유연성, 성능, 정확성 측면에서 신중히 고민한 후 합당할 때만 사용해야 한다.
    - 직접 설계를 해도 기본 직렬화 형태와 거의 같은 형태가 나와야 한다.
    - 어떤 객체의 기본 직렬화 형태는 그 객체를 루트로 하는 객체 그래프의 물리적 모습을 나름 효율적으로 인코딩 한다.
        - 객체가 포함한 데이터들과 그 객체에서부터 시작해 접근할 수 있는 모든 객체를 담아낸다.
        - 객체들의 연결 위상까지 기술한다.

        ```json
        {
            name: "홍길동",
            age: "30",
            company: {
                name: "samsung",
                ceo: "김기남"
            },
            familly: [
                { name: "홍상직", age: 58, relation: "아버지" },
                { name: "옥영향", age: 56, relation: "어머니" },
            ]
        }
        ```

- 객체의 물리적 표현과 논리적 내용이 같다면 기본 직렬화 형태라도 무방하다.
    - 성명은 논리적으로 `이름, 성, 중간이름` 3가지 문자열로 구성된다.

        ```java
        public class Name implements Serializable {
            /**
             * 성. null이 아니여야 함.
             * @serial
             */
            private final String lastName;
        
            /**
             * 이름. null이 아니여야 함.
             * @serial
             */
            private final String firstName;
        
            /**
             * 중간이름. 중간이름이 없다면 null.
             * @serial
             */
            private final String middleName;
        
            ... // 나머지 코드 생략
        }
        ```

- 기본 직렬화 형태가 적합하다고 결정했더라도 불변식 보장과 보안을 위해 readObject 메서드를 제공해야 할 때가 많다.
    - `Name` 클래스의 경우 `readObject` 메서드가 `lastName, firstName` 필드가 `null`이 아님을 보장해야 한다.

### 기본 직렬화 형태가 적합하지 않은 예

```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;
    
    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }

    ... // 나머지 코드 생략  
}
```

- 논리적으로 *일련의 문자열을 표현*하지만 물리적으로 문자열들을 *이중 연결 리스트*로 연결했다.
- 기본 직렬화 형태를 사용하면 각 노드의 양방향 연결 정보를 모두 포함한다.
- 객체의 물리적 표현과 논리적 표현의 차이가 클 때 기본 직렬화 형태를 사용하면 발생하는 문제점
    1. 공개 API가 현재의 내부 표현 방식에 영구히 묶인다.
        1. StringList.Entry 클래스가 공개 API가 된다.
        2. 내부 표현 방식을 바꿔도 StringList는 이중 연결 리스트로 표현된 입력도 처리할 수 있어야 한다.
           → 연결 리스트를 사용하지 않더라도 제거할 수 없다.
    2. 너무 많은 공간을 차지할 수 있다.
        1. 연결 리스트의 모든 엔트리와 연결 정보까지 저장했지만 **정작 클라이언트가 필요한건 문자열**이다.
    3. 시간이 너무 많이 걸릴 수 있다.
        1. 객체의 데이터와 클래스 구조를 파악하는데 오래 걸린다.
    4. 스택 오버플로를 일으킬 수 있다.
        1. 기본 직렬화 과정은 객체 그래프를 재귀 순회하는데 StringList에 원소를 `1,000~1,800개` 정도 담으면 직렬화 과정에서 `StackOverflowError`가 발생한다.

### 합리적인 직렬화 형태

- StirngList
    - `transient` 한정자를 추가해 인스턴스 필드가 기본 직렬화 형태에 포함되지 않도록 한다.
        - size, head
    - String.Entry 클래스(물리적 데이터)를 직렬화 하지 않는다.

    ```java
    public final class StringList implements Serializable {
        private transient int size = 0;
        private transient Entry head = null;
    
        // 이제는 직렬화 되지 않는다.
        private static class Entry {
            String data;
            Entry next;
            Entry previous;
        }
    
        // 지정한 문자열을 이 리스트에 추가한다.
        public final void add(String s) {}
    
        /**
         * 이 {@code StringList} 인스턴스를 직렬화한다.
         *
         * @serialData 이 리스트의 크기(포함된 문자열의 개수)를 기록한 후
         * ({@code int}), 이어서 모든 원소를(각각은 {@code String})
         * 순서대로 기록한다.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            s.writeInt(size);
    
            // 모든 원소를 올바른 순서로 기록한다.
            for (Entry e = head; e != null; e = e.next)
                s.writeObject(e.data);
        }
    
        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            int numElements = s.readInt();
    
            // 모든 원소를 읽어 이 리스트에 삽입한다.
            for (int i = 0; i < numElements; i++)
                add((String) s.readObject());
        }
    
        ... // 나머지 코드는 생략
    }
    ```

    - `StringList`의 필드가 모두 `transient`더라도 `writeObject, readObject`는 가장 먼저 `defaultWriteObject, defaultReadObject`를 호출한다.
        - 향후 릴리스에서 `transient`가 아닌 인스턴스 필드가 추가되었을 때 **상호 호환**된다.
            - 신버전 인스턴스를 직렬화하고 구버전으로 역직렬화하면 새로 추가된 필드는 무시된다.
            - `defaultReadObject`를 호출하지 않는다면 `StreamCorruptedException`이 발생한다.
- 커스텀 직렬화 형태
    - 기본 직렬화를 수용하든 하지 않든 `defaultWriteObject` 메서드를 호출하면 `transient`로 선언하지 않은 모든 인스턴스가 직렬화 된다.
    - 해당 객체의 논리적 상태(실제 수행에 필요한 정보)와 무관한 필드라고 확신할 때만 `transient` 한정자를 생략한다.
    - 기본 직렬화 사용 시 `transient` 필드들은 역직렬화될 때 기본값(0, null, false)으로 초기화 된다.
        - 기본값이 아닌 값을 사용해야 한다면 `defaultReadObject`를 호출하고 해당 필드를 원하는 값으로 선언한다.