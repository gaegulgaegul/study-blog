package me.gaegul.ch05.item33;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Favorites {
    // 비한정적 와일드카드 타입이 중첩되어 있다.
    // 모든 키가 서로 다른 매개변수화 타입일 수 있다.
    // 다양한 타입을 지원 할 수 있다.
    private Map<Class<?>, Object> favorites = new HashMap<>();

    // 주어진 Class 객체와 즐겨찾기 인스턴스를 favorites에 추가해 관계를 짓는다.
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }

    // 주어진 Class 객체에 해당하는 값을 favorites 맵에서 꺼낸다.
    // Class의 cast 메서드를 사용해 객체 참조를 Class가 가리키는 타입으로 동적 형변환한다.
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
