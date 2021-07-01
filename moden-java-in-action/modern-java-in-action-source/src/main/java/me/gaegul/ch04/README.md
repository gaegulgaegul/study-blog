# 4. 스트림 소개

- 스트림이란 무엇인가?
    - 선언형으로 데이터를 처리할 수 있다.
        - 루프와 조건문 등의 제어 블록을 사용할 필요없이 동작의 수행을 지정할 수 있다.
        - `filter, sorted, map, collect` 같은 **빌딩 블록 연산**으로 연결해 데이터 파이프라인을 만들 수 있다. 코드 가독성과 명확성도 유지된다.
    - 멀티 스레드 코드를 구현하지 않아도 데이터를 투명하게 병렬로 처리할 수 있다.
    - 예제 테스트 코드

    ```java
    List<Dish> menu = Arrays.asList(
    	new Dish("pork", false, 800, Dish.Type.MEAT),
    	new Dish("beef", false, 700, Dish.Type.MEAT),
    	new Dish("chicken", false, 400, Dish.Type.MEAT),
    	new Dish("french fries", true, 530, Dish.Type.OTHER),
    	new Dish("rice", true, 350, Dish.Type.OTHER),
    	new Dish("season fruit", true, 120, Dish.Type.OTHER),
    	new Dish("pizza", true, 550, Dish.Type.OTHER),
    	new Dish("prawns", false, 300, Dish.Type.FISH),
    	new Dish("salmon", false, 450, Dish.Type.FISH)
    );

    @Data
    @AllArgsConstrutor
    public class Dish {
    	private final String name;

    	@Getter(AccessLevel.none)
    	private final boolean vegetarian;
    	private final int calories;
    	private final Type type;

    	public boolean isVegetarian() {
    		return this.vegetarian;
    	}

    	public enum Type {
    		MEAT, FISH, OTHER
    	}
    }
    ```

- 스트림 시작하기
    - 특징
        - 연속된 요소
        - 소스 → 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 사용한다.
        - 데이터 처리 연산
        - 파이프라이닝
        - 내부 반복
    - 스트림과 컬렉션
        - 컬렉션은 현재 자료구조가 포함하는 모든 값을 메모리에 저장하는 자료구조
        - 스트림은 요청할 때만 요소를 계산하는 고정된 자료구조
        - 컬렉션은 요청에 대한 모든 동작을 끝내고 결과를 반환하지만 스트림은 요청에 대한 동작 중 일부 결과를 끝내고 반환하는 것을 반복한다.
    - 딱 한번만 탐색할 수 있다.
    - 외부 반복과 내부 반복
        - 외부 반복 → 컬렉션 인터페이스를 이용해서 사용자가 직접 요소를 반복
        - 내부 반복 → 반복을 알아서 처리하고 결과 스트림 값을 어딘가에 저장
        - 외부 반복보다 내부 반복이 좋은 이유
            - 작업을 투명하게 병렬로 처리하거나 더 최적화된 다양한 순서로 처리할 수 있다.
            - 스트림의 내부 반복은 데이터 표현과 하드웨어를 활용한 병렬성 구현을 자동으로 선택한다.
- 스트림 연산

    ```java
    List<String> names = menu.stream()
                             .filter(dish -> dish.getCalories() > 300)
                             .map(Dish::getName)
                             .limit(3)
                             .collect(toList());
    ```

    - `filter, map, limit`는 서로 연결되어 파이프라인을 형성한다. → **중간 연산**
    - `collect`로 파이프라인을 실행한 다음에 닫는다. → **최종 연산**
    - 중간 연산
        - filter나 sorted 같은 중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해서 질의를 만들 수 있다.
        - 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다. → ***lazy***
    - 최종 연산
        - 스트림 파이프라인에서 결과를 도출한다.
        - 최종 연산에 의해 List, Integer, void 등 스트림 이외의 결과가 반환된다.