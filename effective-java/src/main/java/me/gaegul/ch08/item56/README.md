# 아이템 56. 공개된 API 요소에는 항상 문서화 주석을 작성하라

### 자바독 유틸리티

- 소스코드 파일에 문서화 주석이라는 특수한 형태로 기술된 설명을 추려 API 문서로 변환해준다.
- API를 올바로 문서화하려면 공개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석을 달아야 한다.
    - 직렬화할 수 있는 클래스라면 직렬화 형태에 관해서도 적어야 한다.
    - 기본 생성자에는 문서화 주석을 달 방법이 없어 공개 클래스에는 기본 생성자를 사용하면 안 된다.
    - 유지보수까지 고려한다면 대다수의 공개되지 않은 클래스, 인터페이스, 생성자, 메서드, 필드에도 문서화 주석을 달아야 한다.
- 메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 한다.
    - 상속용으로 설계된 클래스의 메서드가 아니라면 무엇을 하는지를 기술해야 한다.
    - 문서화 주석에는 클라이언트가 해당 메서드가 호출하기 위한 전제조건, 메서드가 성공적으로 수행된 후에 만족해야 하는 사후조건을 모두 나열해야 한다.
        - 전제조건
            - `@throws` 태그로 비검사 예외를 선언하여 암시적으로 기술한다.
            - `@param` 태그를 이용해 그 조건에 영향 받는 매개변구에 기술할 수도 있다.
    - 부작용도 문서화해야 한다.
        - 사후조건으로 명확히 나타나지는 않지만 시스템의 상태에 어떠한 변화를 가져오는 것
          예) 백그라운드 스레드를 시작시키는 메서드는 해당 사실을 문서에 밝힌다.

### 메서드 계약을 완벽히 기술하려면

- `@param`
    - 모든 매개변수
- `@return`
    - void가 아닌 반환 타입
    - 코딩 표준이 허락하면 생략 가능
- `@throws`
    - 모든 예외
- `@param`, `@return` 의 설명은 명사구를 쓰지만 드물게 산술 표현식을 사용하기도 한다.
- `@param`, `@return`, `@throws` 의 설명에는 마침표를 붙이지 않는다.

```java
/**
 * Returns the element at the specified position in this list.
 * 
 * <p>This method is <i>not</i> guaranteed to run in constant
 * time. In some implementations it may run in time proportional
 * to the element position.
 *
 * @param index index of the element to return; nust be
 *        non-negative and less than the size of this list
 * @return the element at the specified position in this list
 * @throws IndexOutOfBoundsException if the index is out of range
 *         ({@code index < 0 || index >= size()})
 */
E get(int index);

// 한글 번역
/**
 * 이 리스트에서 지정한 위치의 원소를 반환한다.
 * 
 * <p>이 메서드는 상수 시간에 수행됨을 보장하지 <i>않는다</i>. 구현에 따라
 * 원소의 위치에 비례해 시간이 걸릴 수도 있다.
 *
 * @param index 반환할 원소의 인덱스; 0 이상이고 리스트 크기보다 작아야 한다.
 * @return 이 리스트에서 지정한 위치의 원소
 * @throws IndexOutOfBoundsException index가 범위를 벗어나면,
 *         즉, ({@code index < 0 || index >= size()})이면 발생한다.
 */
E get(int index);
```

### 상속용 클래스 설계 시 자기사용 패턴에 대해 문서화

- 메서드를 올바르게 재정의하는 방법을 알려줘야 한다.
- `@implSpec` 을 통해 문서화 한다.
    - 일반적인 문서화 주석은 해당 메서드와 클라이언트 사이의 계약을 설명한다.
    - 하위 클래스들이 그 메서드를 상속하거나 super 키워드를 이용해 호출할 때 그 메서드가 어떻게 동작하는지를 명확히 인지하고 사용하도록 해야 한다.

    ```java
    /**
     * Returns true if this collection is empty.
     * 
     * @implSpec
     * This implementation returns {@code this.size == 0}.
     * 
     * @return true if this collection is empty
     */
    public boolean isEmpty() { ... }
    
    // 한글 번역
    /**
     * 이 컬렉션이 비어있다면 true를 반환한다.
     * 
     * @implSpec
     * 이 구현은 {@code this.size == 0}의 결과를 반환한다.
     * 
     * @return 이 컬렉션이 비어있다면 true, 그렇지 않다면 false
     */
    public boolean isEmpty() { ... }
    ```


### {@literal}

- HTML 마크업이나 자바독 태그를 무시하게 해준다.

    ```java
    * A geometric series converges if {@literal |r| < 1}.
    
    // 한글 번역
    * {@literal |r| < 1}이면 기하 수열이 수렴한다.
    ```

    - API문서에는 **|r| < 1이면 기하 수열이 수렴한다.**
- 한 클래스 안에서 요약 설명이 똑같은 멤버가 둘 이상이면 안 된다.
    - 다중정의된 메서드들의 설명은 같은 문장으로 시작하는게 좋다.
    - 요약 설명에서는 마침표에 주의해야 한다.

        ```java
        /**
         * A suspect, such as Colonel Mustard or {@literal Mrs. Peacock}.
         */
        
        // 한글 번역
        /**
         * 머스타드 대령이나 {@literal Mrs. 피콕} 같은 용의자.
         */
        ```

    - 요약 설명은 완전한 문장이 되는 경우는 드둘다.
    - 메서드와 생성자 요약 설명은 동작을 설명하는 동사구여야 한다.

### 검색(색인)

- 자바 9부터 추가되었다.
- `{@index}` 를 사용해 중요한 용어를 추가로 색인화 할 수 있다.

### 문서화 주석에서 제네릭, 열거 타입, 애너테이션은 주의해야 한다.

- 재네릭 타입이나 제네릭 메서드를 문서화할 때는 모든 타입 매개변수에 주석을 달아야 한다.

    ```java
    /**
     * An object that maps keys to values. A map cannot contain
     * duplocatee keys; each key con map to at most one value.
     * 
     * {Remainder omitted}
     * 
     * @param <K> the type of keys to values. maintained by this map
     * @param <V> the type of mapped values
     */
    public interface Map<K, V> { ... }
    
    // 한글 번역
    /**
     * 키와 값을 매핑하는 객체. 맵은 키를 중복해서 가질 수 없다.
     * 즉, 키 하나가 가리킬 수 있는 값은 최대 1개다.
     * 
     * {나머지 설명 생략}
     * 
     * @param <K> 이 맵이 관리하는 키의 타입
     * @param <V> 매핑된 값의 타입
     */
    public interface Map<K, V> { ... }
    ```

- 열거 타입을 문서화할 때는 상수들에도 주석을 달아야 한다.

    ```java
    /**
     * An instrument section of a symphony orchestra.
     */
    public enum OrchestraSection {
        /** Woodwinds, such as flute, clarint, and oboe. */
        WOODWIND,
    
        /** Brass instruments, such as french horn and trumpet. */
        BRASS,
    
        /** Percussion instruments, such as timpani and cymbals. */
        PERCUSSION,
    
        /** Stringed imstruments, such as violin and cello. */
        STRING;
    }
    
    // 한글 번역
    /**
     * 심포니 오케스트라의 악기 세션
     */
    public enum OrchestraSection {
        /** 플루트, 클라리넷, 오보 같은 목관악기. */
        WOODWIND,
    
        /** 프렌치 호른, 트럼펫 같은 금관악기, */
        BRASS,
    
        /** 탐파니, 심벌즈 같은 타악기. */
        PERCUSSION,
    
        /** 바이올린, 첼로 같은 현악기. */
        STRING;
    }
    ```

- 애너테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다.

    ```java
    /**
     * Indicates that the annotated method is a test method that
     * must throw the designated exception to pass.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTest {
        /**
         * The exception that the annotated test method must throw
         * in order to pass. (The test is permitted to throw any
         * subtype of the described by this class object.)
         */
        Class<? extends Throwable> value();
    }
    
    // 한글 번역
    /**
     * 이 애너테이션이 달린 메서드는 명시한 예외를 던져야만 성공하는
     * 태스트 메서드임을 나타낸다.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTest {
        /**
         * 이 애너테이션을 단 테스트 메서드가 성공하려면 던져야 하는 예외.
         * (이 클래스의 하위 타입 예외는 모두 허용된다.)
         */
        Class<? extends Throwable> value();
    }
    ```

- 패키지 설명 주석은 `package-info.java` 파일에 작성한다.
- 모듈 시스템 주석은 `module-info.java` 파일에 작성한다.

### API 문서화에서 자주 누락되는 설명

- 스레드 안전성
    - 클래스 혹은 정적 메서드가 스레드 안전하든 그렇지 않든, 스레드 안전 수준을 반드시 API 설명에 포함해야 한다.
- 직렬화 가능성
    - 직렬화 할 수 있는 클래스라면 직렬화 형태도 API 설명에 기술해야 한다.

### 메서드 주석 상속

- 문서화 주석이 없는 API 요소를 발견하면 자바독이 가장 가까운 문서화 주석을 찾아준다.
- 상위 클래스보다 해당 클래스가 구현한 인터페이스를 먼서 찾는다.
- `@{inheritDoc}` 을 통해 상위 타입의 문서화 주석 일부를 상속할 수 있다.

<aside>
💡 자바독 유틸리티가 생성한 웹페이지를 읽어보면 잘 쓰여진 문서인지 확인 할 수 있다.

</aside>