# 3. 람다 표현식

- 람다란 무엇인가?
    - 메서드로 전달할 수 있는 익명 함수를 단순화한 것
    - 특징
        - 익명 → 이름이 없으므로 익명이라 표현한다.
        - 함수 → 특정 클래스에 종속되지 않는다.
        - 전달 → 메서드 인수나 변수로 저장할 수 있다.
        - 간결성 → 익명 클래스처럼 구현할 필요가 없다.

    ```java
    Comparator<Apple> byWeight = new Comparator<Apple>() {
    	public int compare(Apple a1, Apple a2) {
    		return a1.getWeight().compareTo(a2.getWeight());
    	}
    };

    Comparator<Apple> byWeight = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
    ```

- 함수형 인터페이스
    - 하나의 추상 메서드를 지정하는 인터페이스
    - 많은 디폴트 메서드를 포함할 수 있다.
    - 전체 표현식을 함수형 인터페이스의 인스턴스로 취급할 수 있고 익명 내부 클래스로도 같은 기능을 구현할 수도 있다.
    - 람다 표현식 전체가 함수형 인터페이스의 인스턴스로 취급된다.
    - Predicate
        - T(타입) → boolean

        ```java
        @FunctionalInterface
        public interface Predicate<T> {
        	boolean test(T t)
        }
        ```

    - Consumer
        - T(타입) → void

        ```java
        @FunctionalInterface
        public interface Consumer<T> {
        	void accept(T t)
        }
        ```

    - Function
        - T(타입) → R(리턴 타입)

        ```java
        @FunctionalInterface
        public interface Function<T, R> {
        	R apply(T t)
        }
        ```

- 람다 활용: 실행 어라운드 패턴
    - 자원을 열고, 처리한 다음에, 자원을 닫는다.
    - 실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태

        ```java
        public String processFile() throws IOException {
        	try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        		return br.readLine();
        	}
        }
        ```

    - 1단계: 동작 파라미터화를 기억하라
        - 한번에 두 줄을 읽어 반환하는 경우?
        - BufferedReader를 이용해서 다른 동작을 수행할 수 있도록 processFile 메서드로 동작을 전달

        ```java
        String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
        ```

    - 2단계: 함수형 인터페이스를 이용해서 동작 전달
        - BufferedReader → String과 IOException을 던질 수 있는 시그니처와 일치하는 함수형 인터페이스 작성
        - processFile 메서드에 인수로 전달

        ```java
        @FunctionalInterface
        public interface BufferedReaderProcessor {
        	String process(BufferedReader br) throws IOException;
        }

        public String processFile(BufferedReaderProcessor p) throws IOException {
        	...
        }
        ```

    - 3단계: 동작 실행
        - 람다 코드가 processFile 내부에서 BufferedReaderProcessor 객체의 process를 호출한다.

        ```java
        public String processFile(BufferedReaderProcessor p) throws IOException {
        	try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        		return p.process(br);
        	}
        }
        ```

    - 4단계: 람다 전달

        ```java
        String oneLine = processFile((BufferedReader br) -> br.readLine());
        String twoLine = processFile((BufferedReader br) -> br.readLine() + br.readLine());
        ```

- 형식 검사, 형식 추론, 제약
    - 형식 검사
        - 람다에 사용되는 context(람다가 전달될 메서드 파라미터나 할당되는 변수)를 이용해서 형식(type)을 추론할 수 있다.
        - 람다 코드의 형식 확인 과정

            ```java
            List<Apple> heavierThan150g = filter(inventory, (Apple apple) -> apple.getWeight() > 150);
            ```

            1. filter 메서드의 선언을 확인
            2. filter 메서드는 두번째 파라미터로 Predicate<Apple> 형식을 기대한다.
            3. Predicate<Apple>은 test라는 한 개의 추상 메서드를 정의하는 함수형 인터페이스
            4. test 메서드는 Apple을 받아 boolean을 반환하는 함수 디스크립터
            5. filter 메서드로 전달된 인수는 위와 같은 요구사항을 만족해야 한다.
    - 형식 추론
        - 자바 컴파일러는 람다 표현식이 사용된 context를 이용해서 람다 표현식과 관련된 함수형 인터페이스를 추론하고 함수 디스크립터를 추론할 수 있다.
        - 결과적으로 람다 표현식의 파라미터 형식에 접근할 수 있으므로 람다 문법에서 파라미터 형식을 생략할 수 있다.

        ```java
        Comparator<Apple> c = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

        Comparator<Apple> c = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
        ```

    - 지역 변수 사용
        - 지역 변수 사용 시, 명시적으로 final로 선언되어 있어야 하거나 final 변수와 같이 사용되어야 한다.
        - 지역 변수 값이 바뀌면 안된다.

        ```java
        int portumber = 1337;
        Runnable r = () -> System.out.println(portumber);
        ```

- 메서드 참조, 생성자 참조
    - 특정 메서드만 호출하는 람다의 축약형
    - 실제 사용할 메서드를 명시적으로 작성하여 가독성을 높일 수 있다.
    - 메서드 참조 방식
        1. 정적 메서드 참조 → `Integer::parseInt`
        2. 다양한 형식의 인스턴스 메서드 참조 → `String::length`
        3. 기존 객체의 인스턴스 메서드 참조 → `expensiveTransaction::getValue`
    - `ClassName::new`를 통해 생성자 참조