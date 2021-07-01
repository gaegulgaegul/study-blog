# 11. null 대신 Optional 클래스

- 값이 없는 상황을 어떻게 처리할까?
    - 보수적인 자세로 NullPointerException 줄이기
        - null 확인 코드를 추가한다.

    ```java
    public String getCarInsuranceName(Person person) {
        if (person != null) {
            Car car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if (insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }
    ```

    - null 때문에 발생하는 문제
        - 에러의 근원이다.
        - 코드를 어지럽힌다.
        - 아무 의미가 없다.
        - 자바 철학에 위배된다.
        - 형식 시스템에 구멍을 만든다.
    - 다른 언어는 null 대신 무얼 사용하나?
        - 그루비 → 안전 내비게이션 연산자를 도입해서 null 문제 해결, null 참조 예외 걱정 없이 객체에 접근할 수 있다.
        - 하스켈 → 선택형 값을 저장할 수 있는 Maybe 형식 제공, Maybe는 주어진 형식의 값을 갖거나 아니면 아무 값도 갖지 않을 수 있다.
        - 스칼라 → T 형식의 값을 갖거나 아무 값도 갖지 않을 수 있는 Option[T] 제공
- Optional 클래스 소개
    - 하스켈과 스칼라의 영향을 받아서 *java.util.Optional<T>* 클래스 제공
    - 값이 있으면 Optional 클래스로 값을 감싸고 값이 없으면 `Optional.empty` 메서드로 Optional을 반환한다.
    - **null을 참조**하려면 `NullPointerException`이 발생하지만 `Optional.empty`는 Optional 객체이므로 이를 활용할 수 있다.
- Optional 적용 패턴
    - Optional 객체 만들기
        - 빈 Optional

        ```java
        Optional<Car> optCar = Optional.empty();
        ```

        - null이 아닌 값으로 Optional 만들기
            - 값이 null이라면 `NullPointerException`이 발생한다.

        ```java
        Optional<Car> optCar = Optional.of(car);
        ```

        - null값으로 Optional 만들기

        ```java
        Optional<Car> optCar = Optional.ofNullable(car);
        ```

    - 맵으로 Optional의 값을 추출하고 변환하기
        - 스트림의 map은 스트림의 각 요소에 제공된 함수를 적용하는 연산, 여기서 Optional 객체를 퇴대 요소의 개수가 한 개 이하인 데이터 컬렉션으로 생각할 수 있다.
        - Optional이 값을 포함하면 map의 인수로 제공된 함수가 값을 바꾼다. Optional이 비어있으면 아무 일도 일어나지 않는다.
    - flatMap으로 Optional 객체 연결
        - flatMap은 함수를 인수로 받아서 다른 스트림을 반환하는 메서드
        - 함수를 적용해서 생성된 모든 스트림이 하나의 스트림으로 병합되어 평준화된다.
        - 평준화 과정이란 이론적으로 두 Optional을 합치는 기능을 수행하면서 둘 중 하나라도 null이면 빈 Optional을 생성하는 연산이다.
        - flatMap을 빈 Optional에 호출하면 아무 일도 일어나지 않고 그대로 반환된다. 반면 Optional이 값을 감싸고 있다면 flatMap에 전달된 Function이 값에 적용된다.

        ```java
        public static String getCarInsuranceName(Optional<Person> person) {
            return person.flatMap(Person::getCar)
                    .flatMap(Car::getInsurance)
                    .map(Insurance::getName)
                    .orElse("Unknown");
        }
        ```

    - Optional 스트림 조작
        - Optional 스트림을 값을 가진 스트림으로 변환할 때 사용한다.

        ```java
        public static Set<String> getCarInsuranceNames(List<Person> persons) {
            return persons.stream()
                    .map(Person::getCar)
                    .map(optCar -> optCar.flatMap(Car::getInsurance))
                    .map(optIns -> optIns.map(Insurance::getName))
                    .flatMap(Optional::stream)
                    .collect(toSet());
        }
        ```

    - 디폴트 액션과 Optional 언랩
        - get()
            - 래핑된 값이 있으면 값을 반환, 없다면 `NoSuchElementException`
        - orElse(T other)
            - 값을 포함하지 않을 때 기본값을 제공한다.
        - orElseGet(Supplier<? extends T> other)
            - 값이 없을 때 Supplier가 실행된다.
            - 디폴트 메서드를 만드는 데 시간이 걸리거나 Optional이 비어있을 때만 기본값을 생성할 때 사용
        - orElseThrow(Supplier<? extends X> exceptionSupplier)
            - 값이 비었을 때 예외를 발생시킨다.
        - ifPresent(Consumer<? super T> consumer)
            - 값이 존재할 때 인수로 넘겨준 동작을 수행한다.
        - ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)
            - 값이 존재하는 경우 consumer를 실행하고 값이 없으면 runnable을 실행한다.
    - 두 Optional 합치기
        - isPersent()를 이용해서 값을 확인하고 Optional 합치기를 수행한다.

        ```java
        public static Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
            if (person.isPresent() && car.isPresent()) {
                return Optional.of(findCheapestInsurance(person.get(), car.get()));
            }
            return Optional.empty();
        }
        ```

        - Optional 언랩하지 않고 두 Optional 합치기

        ```java
        public static Optional<Insurance> nullSafeFindCheapestInsuranceWithFlatMap(Optional<Person> person, Optional<Car> car) {
            return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
        }
        ```

    - 필터로 특정값 거르기

        ```java
        Optional.ofNullable(new Insurance()).filter(insurance -> "CambridgeInsurance".equals(insurance.getName()))
                    .ifPresent(x -> System.out.println("ok"));
        ```

- Optional을 사용한 실용 예제
    - 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기
        - map의 경우 key에 해당하는 값이 없다면 null이 반환된다. 이를 **Optional.ofNullable()**로 감싸서 안전하게 사용할 수 있다.
    - 예외와 Optional 클래스
        - 예외가 발생하는 경우 Optional.empty()를 반환한다.

        ```java
        public static Optional<Integer> stringToInt(String s) {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        ```

    - 기본형 Optional을 사용하지 말아야 하는 이유
        - Optional의 최대 요소 수는 한개이므로 성능 기본형 Optional을 통해 성능 개선이 되지 않는다.
        - 기본형 Optional로 생성한 결과는 다른 일반 Optional과 혼용할 수 없다.