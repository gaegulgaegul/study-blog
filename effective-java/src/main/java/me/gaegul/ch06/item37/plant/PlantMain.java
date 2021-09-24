package me.gaegul.ch06.item37.plant;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class PlantMain {

    public static void main(String[] args) {
        List<Plant> garden = List.of(
                new Plant("1년생 식물", Plant.LifeCycle.ANNUAL),
                new Plant("다년생 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("2년생 식물", Plant.LifeCycle.BIENNIAL));

        // ordinal 잘못된 사용 방법
//        useOrdinal(garden);
        // EnumMap을 사용해 데이터와 열거 타입 매핑
        useEnumMap(garden);

        // EnumMap 예제
        // EnumMap을 사용하지 않고 스트림을 사용하는 코드
//        useStreamWithoutEnumMap(garden);

        // EnumMap을 이용해 데이터와 열거 타입을 매핑한다.
        useStreamWithEnumMap(garden);
    }

    private static void useOrdinal(List<Plant> garden) {
        // 배열은 제네릭과 호환되지 않는다.
        Set<Plant>[] plantByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        for (int i = 0; i < plantByLifeCycle.length; i++) {
            plantByLifeCycle[i] = new HashSet<>();
        }

        for (Plant p : garden)
            plantByLifeCycle[p.lifeCycle.ordinal()].add(p);

        for (int i = 0; i < plantByLifeCycle.length; i++) {
            // 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다.
            System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantByLifeCycle[i]);
        }
    }

    private static void useEnumMap(List<Plant> garden) {
        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

        for (Plant.LifeCycle lc : Plant.LifeCycle.values()) {
            plantsByLifeCycle.put(lc, new HashSet<>());
        }

        for (Plant p : garden) {
            plantsByLifeCycle.get(p.lifeCycle).add(p);
        }

        System.out.println(plantsByLifeCycle);
    }

    private static void useStreamWithoutEnumMap(List<Plant> garden) {
        System.out.println(garden.stream()
                .collect(groupingBy(p -> p.lifeCycle)));
    }

    private static void useStreamWithEnumMap(List<Plant> garden) {
        System.out.println(garden.stream()
                .collect(groupingBy(p -> p.lifeCycle,
                        () -> new EnumMap<>(Plant.LifeCycle.class), toSet())));
    }
}
