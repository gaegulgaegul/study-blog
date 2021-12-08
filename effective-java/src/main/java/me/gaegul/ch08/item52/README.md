# 아이템 52. 다중정의는 신중히 사용하라

### 메서드 다중 정의

- 컬렉션 분류기

    ```java
    public class CollectionClassifier {
    
        public static String classify(Set<?> set) {
            return "집합";
        }
    
        public static String classify(List<?> list) {
            return "리스트";
        }
    
        public static String classify(Collection<?> collection) {
            return "그 외";
        }
    
        public static void main(String[] args) {
            Collection<?>[] collections = {
                    new HashSet<String>(),
                    new ArrayList<BigInteger>(),
                    new HashMap<String, String>().values()
            };
    
            for (Collection<?> collection : collections) {
                System.out.println(classify(collection));
            }
        }
    }
    
    // console
    그 외
    그 외
    그 외
    ```

    - 다중 정의된 세 classify 중 어느 메서드를 호출할지가 컴파일타임에 정해진다.
        - 컴파일 타임에는 for문 안의 collection은 항상 Collection<?> 타입이다.
        - 항상 Collection<?> 매개변수를 가진 classify만 호출된다.

    <aside>
    💡 재정의한 메서드는 동적으로 선택되고, 다중정의한 메서드는 정적으로 선택된다.

    </aside>

- 오류 해결

    ```java
    public static String classify(Collection<?> collection) {
        return collection instanceof Set ? "집합" : 
               collection instanceof List ? "리스트" : "그 외";
    }
    ```


### 메서드 재정의

- 해당 객체의 런타임 타입이 어떤 메서드를 호출 할지의 기준이 된다.
- 메서드를 재정의하고 '하위 클래스의 인스턴스'에서 메서드를 호출하면 재정의한 메서드가 실행된다.
- 컴파일 타임에는 '하위 클래스의 인스턴스 타입'이 무엇이었냐는 상관없다.
- 재정의된 메서드 호출 매커니즘

    ```java
    class Wine {
        String name() {
            return "포도주";
        }
    }
    
    class SparklingWine extends Wine {
        @Override
        String name() {
            return "발포성 포도주";
        }
    }
    
    class Champagne extends SparklingWine {
        @Override
        String name() {
            return "샴페인";
        }
    }
    
    public class Overriding {
        public static void main(String[] args) {
            List<Wine> wineList = List.of(new Wine(), new SparklingWine(), new Champagne());
    
            for (Wine wine : wineList) {
                System.out.println(wine.name());
            }
        }
    }
    
    // console
    포도주
    발포성 포도주
    샴페인
    ```

    <aside>
    💡 가장 하위에서 정의한 재정의 메서드가 실행된다.

    </aside>


### 다중정의가 혼동을 일으키는 상황을 피해야 한다.

- API 사용자가 매개변수를 넘기면서 어떤 다중정의 메서드가 호출될지 모른다면 프로그램은 오작동하기 쉽다.
    - 런타임에서 오작동을 확인할 수 있다.
- 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자
- 가변인수를 사용하는 메서드라면 다중정의를 아예 하지 말아야 한다.

### 다중정의하는 대신 메서드 이름을 다르게 지어주는 방법

- ObjectOutputStream
    - write 메서드
        - writeBoolean(boolean), writeInt(int), writeLong(long)
    - read 메서드
        - readBoolean(boolean), readInt(int), readLong(long)
- 모든 기본 타입과 일부 참조 타입용 변형을 가지고 있다.
- 이 방식의 장점은 write & read 메서드 이름과 짝을 맞추기 좋다.

### 오토박싱

```java
public class SetList {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        List<Integer> list = new ArrayList<>();

        for (int i = -3; i < 3; i++) {
            set.add(i);
            list.add(i);
        }

        for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove(i);
        }
        System.out.println(set + " " + list);
    }
}

// console
[-3, -2, -1] [-2, 0, 2]
```

- 기대한 결과값은 '[-3, -2, -1] [-3, -2, -1]'이지만 set은 기대에 맞게 출력되고 list는 홀수를 제거한 정보가 출력되었다.
- `set.remove(i)`의 시그니처는 `remove(Object)`
    - 다중정의된 다른 메서드가 없으니 기대한 대로 동작한다.
- `list.remove(i)`는 다중 정의된 `remove(int index)`를 선택한다.
    - 지정된 위치를 제거하는 기능이다. 하지만 원래 리스트의 정보는 [-3, -2, -1, 0, 1, 2]이고 0~2번째 원소를 제거하면 [-2, 0, 2]가 남는다.
    - list.remove의 인수를 Integer로 형변환하면 올바른 다중정의 메서드를 선택하게 된다.

<aside>
💡 제네릭 도입 전 Object와 int가 근본적으로 달라서 문제가 없었지만, 제네릭과 오토박싱 도입 후 List의 remove(int)와 remove(Object)의 매개변수 타입은 근본적으로 다르지 않게 되었다.

</aside>

### 람다와 메서드 참조

```java
// 1번.Thread의 생성자 호출
new Thread(System.out::println).start();

// 2번. ExecutorService의 submit 메서드 호출
ExecutorService exec = Executors.newCacheThreadPool();
exec.submit(System.out::println);
```

- 1번은 컴파일 되고 2번은 컴파일 되지 않는다.
    - `System.out::println`와 `submit`이 다중 정의 되어 있다.
    - `System.out::println`
        - 부정확한 메서드 참조, 암시적 타입 람다식
            - 목표 타입이 선택되기 전에는 그 의미가 정해지지 않기 때문에 적용성 테스트 때 무시된다.
    - 서로 다른 함수형 인터페이스라도 인수 위치가 같으면 혼란이 생긴다.

    <aside>
    💡 메서드를 다중정의할 때, 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안 된다.

    </aside>