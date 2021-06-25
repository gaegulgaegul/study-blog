# 2. 동작 파라미터화 코드 전달하기

- 첫번째 시도: 녹색 사과 필터링

    ```java
    enum Color { RED, GREEN }

    public static List<Apple> filterGreenApples(List<Apple> inventory) {
    	List<Apple> result = new ArrayList<>();
    	for (Apple apple : inventory) {
    		if (GREEN.equals(apple.getColor())) {
    			result.add(apple);
    		}
    	}
    	return result;
    }

    List<Apple> greenApples = filterGreenApples(inventory);
    ```

    - 녹색 사과만 필터링한다.
    - 빨간 사과도 필터링하고 싶을 때, 중복 부분은 추상화하여 구분한다.
- 두번째 시도: 색을 파라미터화

    ```java
    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
    	List<Apple> result = new ArrayList<>();
    	for (Apple apple : inventory) {
    		if (apple.getColor().equals(Color)) {
    			result.add(apple);
    		}
    	}
    	return result;
    }

    List<Apple> greenApples = filterApplesByColor(inventory, GREEN);
    List<Apple> redApples = filterApplesByColor(inventory, RED);
    ```

    - Color 매개변수를 추가해 색상을 구분한다.
    - 또 다른 요구사항 → 가벼운 사과, 무거운 사과(무게가 150g 이상인 사과) 구분

    ```java
    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
    	List<Apple> result = new ArrayList<>();
    	for (Apple apple : inventory) {
    		if (apple.getWeight() > weight) {
    			result.add(apple);
    		}
    	}
    	return result;
    }

    List<Apple> heavyApples = filterApplesByWeight(inventory, 150);
    ```

    - 또 다른 요구사항 추가 시 메서드를 계속 생성하여 매개변수만 다르게 선언하게 되며 중복 코드가 발생한다.
- 세번째 시도: 가능한 모든 속성을 필터링

    ```java
    public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
    	List<Apple> result = new ArrayList<>();
    	for (Apple apple : inventory) {
    		if ((flag && apple.getColor().equals(Color)) ||
    				(!flag && apple.getWeight() > weight)) {
    			result.add(apple);
    		}
    	}
    	return result;
    }

    List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
    List<Apple> heavyApples = filterApples(inventory, null, 150, false);
    ```

    - 위와 같은 방식은 요구사항이 변경되었을 경우 대응이 힘들다.
    - 효과적인 필터링을 위해 동작 파라미터화를 사용한다.

    ```java
    public interface ApplePredicate {
    	boolean test(Apple apple);
    }

    public class AppleHeavyWeightPredicate implements ApplePredicate {
    	public boolean test(Apple apple) {
    		return apple.getWeight() > 150;
    	}
    }

    public class AppleGreenColorPredicate implements ApplePredicate {
    	public boolean test(Apple apple) {
    		return GREEN.equals(apple.getColor());
    	}
    }
    ```

    - filter 메서드가 다르게 동작할 수 있도록 위와 같이 인터페이스 구현한다.
- 네번째 시도: 추상적 조건으로 필터링

    ```java
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    	List<Apple> result = new ArrayList<>();
    	for (Apple apple : inventory) {
    		if (p.test(apple)) {
    			result.add(apple);
    		}
    	}
    	return result;
    }

    List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate());
    List<Apple> heavyApples = filterApples(inventory, new AppleHeavyWeightPredicate());
    ```

    - ApplePredicate를 구현해서 필터링 조건을 추가한다.
    - ApplePredicate 객체에 의해 filterApples 메서드의 동작이 결정된다. filterApples 메서드의 동작을 파라미터화 한 것
- 다섯번째 시도: 익명 클래스 사용

    ```java
    List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
    	public boolean test(Apple apple) {
    		return RED.equals(apple.getColor());
    	}
    });
    ```

    - 메서드의 동작을 직접 구현한다.
    - 익명 클래스를 사용했지만 코드를 한눈에 알아보기 힘들다.
- 여섯번째 시도: 람다 표현식 사용

    ```java
    List<Apple> redApples = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
    ```

    - 람다 표현식을 사용해 간단하게 구현한다.
- 일곱번째 시도: 리스트 형식으로 추상화

    ```java
    public interface Predicate<T> {
    	boolean test(T t);
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
    	List<T> result = new ArrayList<>();
    	for (T e : inventory) {
    		if (p.test(e)) {
    			result.add(e);
    		}
    	}
    	return result;
    }

    List<Apple> redApples = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
    List<Integer> evenNumbers = filter(inventory, (Integer i) -> i % 2 == 0);
    ```

    - 리스트에서 필터 메서드를 사용할 수 있다.