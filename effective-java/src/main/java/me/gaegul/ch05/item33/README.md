# 아이템 33. 타입 안전 이종 컨테이너를 고려하라

- 타입 안전 이종 컨테이너 패턴
    - 컨테이너 대신 키를 매개변수화하고 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공한다.
    - 제네릭 타입 시스템의 값의 타입이 키와 같음을 보장한다.
- 즐겨 찾는 인스턴스를 저장하고 검색할 수 있는 Favorites 클래스

    ```java
    public class Favorites {
        private Map<Class<?>, Object> favorites = new HashMap<>();

        public <T> void putFavorite(Class<T> type, T instance) {
            favorites.put(Objects.requireNonNull(type), instance);
        }
        public <T> T getFavorite(Class<T> type) {
            return type.cast(favorites.get(type));
        }

        public static void main(String[] args) {
            Favorites f = new Favorites();

            f.putFavorite(String.class, "Java");
            f.putFavorite(Integer.class, 0xcafebabe);
            f.putFavorite(Class.class, Favorites.class);

            String favoriteString = f.getFavorite(String.class);
            int favoriteInteger = f.getFavorite(Integer.class);
            Class<?> favoriteClass = f.getFavorite(Class.class);

            System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getClass());
        }
    }
    ```

    - `private Map<Class<?>, Object> favorites = new HashMap<>();`
        - 비한정적 와일드카드 타입이 중첩되어 있다.
            - 맵이 아니라 키가 와일드카드 타입인 것이다.
            - 모든 키가 서로 다른 매개변수화 타입일 수 있다.
            - 다양한 타입을 지원 할 수 있다.
        - 값의 타입은 단순히 Object이다.
            - 키와 값 사이의 타입 관계를 보증하지 않는다.
            → 모든 값이 키로 명시한 타입임을 보증하지 않는다.
    - putFavorite
        - 주어진 Class 객체와 즐겨찾기 인스턴스를 favorites에 추가해 관계를 짓는다.
    - getFavorite
        - 주어진 Class 객체에 해당하는 값을 favorites 맵에서 꺼낸다.
        - Class의 cast 메서드를 사용해 객체 참조를 Class가 가리키는 타입으로 동적 형변환한다.
            - cast 메서드
                - 형변환 연산자의 동적 버전
                - 인수가 Class 객체가 알려주는 타입의 인스턴스인지 검사
                    - 맞으면 인수 반환
                    - 아니면 ClassCastException
- Favorites 클래스에 알아두어야 할 제약
    - Class 객체를 제네릭이 아닌 LawType으로 넘기면 타입 안전성이 깨진다.
        - 클라이언트 코드에서는 컴파일 타임에 비검사 경고 발생
        - 동적 형변환으로 런타임 타입 안전성 확보

            ```java
            public <T> void putFavorite(Class<T> type, T instance) {
                favorites.put(Objects.requireNonNull(type), type.cast(instance));
            }
            ```

            - `java.util.Collections` → `checkedSet, checkedList, checkedMap` 메서드는 동적형변환을 통해 타입 안전성을 확보한 컬렉션 래퍼
    - 실체화 불가 타입에는 사용할 수 없다.
        - String, Stirng[]은 저장할 수 있지만 List<String>는 저장할 수 없다. → 컴파일 오류 발생
- 한정적 타입 토큰
    - 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입 토큰을 제한하는 타입 토큰
    - AnnotatedElement 인터페이스에 선언된 getAnnotation

        ```java
        public interface AnnotatedElement {
            ...
            
            // 대상 요소에 달려있는 어노테이션을 런타임에 읽어오는 기능
            // 리플렉션의 대상이 되는 타입, 클래스, 메서드, 필드 등 프로그램 요소를 표현하는 타입에서 구현
            <T extends Annotation> T getAnnotation(Class<T> annotationClass);
        }
        ```

        - annotationClass 인수는 어노테이션의 타입을 의미하는 한정적 타입 토큰
    - asSubclass 메서드
        - 한정적 타입 토큰을 받는 메서드에 넘기려면 asSubclass 메서드를 이용한다.
        - 호출된 인스턴스 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다.

        ```java
        static Annotation getAnnotation(AnnotationElement element,
                                        String annotationTypeName) {
            Class<?> annotationType = null; // 비한정적 타입 토큰
            try {
                anntationType = Class.forName(annotationTypeName);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
            return element.getAnnotation(anntationType.asSubclass(Annotation.class);
        }
        ```