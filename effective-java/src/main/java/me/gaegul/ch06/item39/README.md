# 아이템 39. 명명 패턴보다 애너테이션을 사용하라

### 명명 패턴

- 변수, 함수의 이름을 일관된 방식으로 작성하는 패턴
- 명명 패턴의 단점
    - 오타가 나면 안된다.
    - 올바른 프로그램 요소에서만 사용된다고 보증할 방법이 없다.
        - 명명 패턴을 사용하는 곳에서만 사용한다는 보장이 없다.
    - 프로그램 요소를 매개변수로 전달할 방법이 없다.
        - 명명 패턴을 적용한 요소를 매개변수로 전달할 방법이 없다.

    → 애너테이션을 통해 해결할 수 있다.

### 마커 애너테이션

- 타입 선언

    ```java
    /**
     * 테스트 메서드임을 선언하는 애너테이션
     * 매개변수 없는 정적 메서드 전용
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test {
    }
    ```

    - 메타애너테이션
        - 애너테이션 안에 선언되는 애너테이션
        - `@Retention(RetentionPolicy.RUNTIME)`→ @Test가 런타임 시에도 유지되어야 한다.
        - `@Target(ElementType.METHOD)`→ @Test가 반드시 메서드 선언에서만 사용되어야 한다.
    - "매개변수 없는 정적 메서드 전용"
        - 이 제약을 컴파일러가 강제할 수 있도록 하는게 좋다.
        → 애너테이션 처리기 구현(*java.annotation.processing API 참고*)
- 마커 애너테이션 사용

    ```java
    public class Sample {
        @Test public static void m1() {} // 성공해야 한다.
        public static void m2() {}
        @Test public static void m3() { // 실패해야 한다.
            throw new RuntimeException("실패");
        }
        public static void m4() {}
        @Test public void m5() {} // 잘못 사용한 예: 정적 메서드가 아니다.
        public static void m6() {}
        @Test public static void m7() { // 실패해야 한다.
            throw new RuntimeException("실패");
        }
        public static void m8() {}
    }
    ```

- 마커 애너테이션 처리

    ```java
    public static void main(String[] args) throws ClassNotFoundException {
            int tests = 0;
            int passed = 0;
            Class<?> testClass = Class.forName("me.gaegul.ch06.item39.Sample");
            for (Method m : testClass.getDeclaredMethods()) {
                // @Test 애너테이션이 붙어있는지 확인
                if (m.isAnnotationPresent(Test.class)) {
                    tests++;
                    try {
                        m.invoke(null);
                        passed++;
                    } catch (InvocationTargetException wrappedExc) {
                        Throwable exc = wrappedExc.getCause();
                        System.out.println(m + " 실패: " + exc);
                    } catch (Exception exc) {
                        System.out.println("잘못 사용한 @Test: " + m);
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }

    // console
    잘못 사용한 @Test: public void me.gaegul.ch06.item39.Sample.m5()
    public static void me.gaegul.ch06.item39.Sample.m7() 실패: java.lang.RuntimeException: 실패
    public static void me.gaegul.ch06.item39.Sample.m3() 실패: java.lang.RuntimeException: 실패
    성공: 1, 실패: 3
    ```

### 특정 예외를 던저야 성공하는 테스트

- 매개변수를 하나 받는 애너테이션

    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTest {
        Class<? extends Throwable> value();
    }
    ```

    - `Class<? extends Throwable>`→ Throwable을 확장한 클래스의 Class 객체, 모든 예외 타입을 다 수용한다.
- 매개변수 하나 애너테이션 사용

    ```java
    public class Sample2 {
        
        @ExceptionTest(ArithmeticException.class)
        public static void m1() { // 성공해야 한다.
            int i = 0;
            i = i / i;
        }

        @ExceptionTest(ArithmeticException.class)
        public static void m2() { // 실패해야 한다.(다른 예외 발생)
            int[] a = new int[0];
            int i = a[1];
        }

        @ExceptionTest(ArithmeticException.class)
        public static void m3() {} // 실패해야 한다. (예외가 발생하지 않는다.)
    }
    ```

- 매개변수 하나 애너테이션 처리

    ```java
    public class ExceptionRunTests {
        public static void main(String[] args) throws ClassNotFoundException {
            int tests = 0;
            int passed = 0;
            Class<?> testClass = Class.forName("me.gaegul.ch06.item39.Sample2");
            for (Method m : testClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(ExceptionTest.class)) {
                    tests++;
                    try {
                        m.invoke(null);
                        System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                    } catch (InvocationTargetException wrappedExc) {
                        Throwable exc = wrappedExc.getCause();
                        Class<? extends Throwable> excType = m.getAnnotation(ExceptionTest.class).value();
                        if (excType.isInstance(exc)) {
                            passed++;
                        } else {
                            System.out.printf("테스트 %s 실패: 기대한 예외 %s, 발생한 예외 %s%n", m, excType.getName(), exc);
                        }
                    } catch (Exception exc) {
                        System.out.println("잘못 사용한 @Test: " + m);
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }

    // console
    테스트 public static void me.gaegul.ch06.item39.Sample2.m3() 실패: 예외를 던지지 않음
    테스트 public static void me.gaegul.ch06.item39.Sample2.m2() 실패: 기대한 예외 java.lang.ArithmeticException, 발생한 예외 java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 0
    성공: 1, 실패: 2
    ```

    - @Test 애너테이션과 달리 **애너테이션 매개변수의 값을 추출**하여 테스트 메서드가 올바른 예외를 던지는지 확인하는데 사용한다.

### 배열 매개변수를 받아 특정 예외들을 확인하는 테스트

- 배열 매개변수를 받는 애너테이션 타입

    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTest {
        Class<? extends Throwable>[] value();
    }
    ```

- 배열 매개변수를 받아 애너테이션을 사용

    ```java
    public class Sample3 {
        @ExceptionTest({ IndexOutOfBoundsException.class, NullPointerException.class })
        public static void doublyBad() {
            List<String> list = new ArrayList<>();

            // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundsException이나
            // NullPointerException을 던질 수 있다.
            list.addAll(5, null);
        }
    }
    ```

    - 배열 매개변수는 원소를 중괄호로 감싸 컴마로 구분해준다.
- 배열 매개변수를 받아 애너테이션 처리

    ```java
    public class ExceptionRunTests {
        public static void main(String[] args) throws ClassNotFoundException {
            int tests = 0;
            int passed = 0;
            Class<?> testClass = Class.forName("me.gaegul.ch06.item39.Sample3");
            for (Method m : testClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(ExceptionTest.class)) {
                    tests++;
                    try {
                        m.invoke(null);
                        System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                    } catch (Throwable wrappedExc) {
                        Throwable exc = wrappedExc.getCause();
                        int oldPassed = passed;
                        Class<? extends Throwable>[] excTypes = m.getAnnotation(ExceptionTest.class).value();
                        for (Class<? extends Throwable> excType : excTypes) {
                            if (excType.isInstance(exc)) {
                                passed++;
                                break;
                            }
                        }
                        if (passed == oldPassed) {
                            System.out.printf("테스트 %s 실패: %s %n", m, exc);
                        }
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }

    // console
    성공: 1, 실패: 0
    ```

### @Repeatble

- 배열 매개변수 대신 @Repeatble 매타 애너테이션을 사용할 수 있다.
    - @Repeatable 사용시 주의사항
        - @Repeatable을 단 애너테이션을 반환하는 컨테이너 애너테이션을 하나 더 정의하고 @Repeatable에  컨테이너 애너테이션의 class 객체를 매개변수로 전달한다.
        - 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 매서드를 정의해야 한다.
        - 컨테이너 애너테이션 타입에는 @Retention, @Traget을 명시해야 한다.
- 반복 가능 애너테이션 타입

    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(ExceptionTestContainer.class)
    public @interface ExceptionTest {
        Class<? extends Throwable> value();
    }

    // 컨테이너 애너테이션
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTestContainer {
        ExceptionTest[] value();
    }
    ```

- 반복 가능 애너테이션 사용

    ```java
    public class Sample4 {
        @ExceptionTest(IndexOutOfBoundsException.class)
        @ExceptionTest(NullPointerException.class)
        public static void doublyBad() {
            List<String> list = new ArrayList<>();
            list.addAll(5, null);
        }
    }
    ```

- 반복 가능 애너테이션 처리

    ```java
    public class ExceptionRunTests {
        public static void main(String[] args) throws ClassNotFoundException {
            int tests = 0;
            int passed = 0;
            Class<?> testClass = Class.forName("me.gaegul.ch06.item39.repeatable.Sample4");
            for (Method m : testClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(ExceptionTest.class) || m.isAnnotationPresent(ExceptionTestContainer.class)) {
                    tests++;
                    try {
                        m.invoke(null);
                        System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                    } catch (Throwable wrappedExc) {
                        Throwable exc = wrappedExc.getCause();
                        int oldPassed = passed;
                        ExceptionTest[] excTests = m.getAnnotationsByType(ExceptionTest.class);
                        for (ExceptionTest excTest : excTests) {
                            if (excTest.value().isInstance(exc)) {
                                passed++;
                                break;
                            }
                        }
                        if (passed == oldPassed) {
                            System.out.printf("테스트 %s 실패: %s %n", m, exc);
                        }
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }
    ```

    - 반복 가능 애너테이션을 여러 개 달면 하나만 달았을 때와 구분하기 위해 해당 컨테이너 애너테이션 타입이 적용된다.
    - isAnnotationPresent 메서드를 통해 둘을 명확하게 구분해야 한다.
        - 반복 가능 애너테이션을 여러 번 사용하고 검사하면 컨테이너가 반복 가능 애너테이션에 사용되어 false가 된다.
        - 두 애너테이션을 모두 검사해야 한다.