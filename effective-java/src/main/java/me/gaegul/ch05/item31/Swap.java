package me.gaegul.ch05.item31;

import java.util.List;

public class Swap {

    // 외부 API는 와일드카드 타입 유지, 내부에서 매개변수화 타입 사용
    public static void swap(List<?> list, int i, int j) {
        swapHelper(list, i, j);
    }

    // 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
    private static <E> void swapHelper(List<E> list, int i, int j) {
        // 컴파일러는 리스트가 List<E>인 것을 알고 있다.
        list.set(i, list.set(j, list.get(i)));
    }
}
