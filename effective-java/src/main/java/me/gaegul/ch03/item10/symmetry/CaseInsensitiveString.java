package me.gaegul.ch03.item10.symmetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CaseInsensitiveString {

    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성이 잘못 정의된 예제
//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof CaseInsensitiveString)
//            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
//        if (o instanceof String) // 한 방향으로만 동작한다.
//            return s.equalsIgnoreCase((String) o);
//        return false;
//    }

    // 대칭성을 보장하는 구현
    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString &&
                (((CaseInsensitiveString) o).s.equalsIgnoreCase(s));
    }

    @Override
    public int hashCode() {
        return Objects.hash(s);
    }

    public static void main(String[] args) {
        // 대칭성 위배 예제
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String s = "Polish";

        System.out.println("cis.equals(s) : " + cis.equals(s)); // true
        System.out.println("s.equals(cis) : " + s.equals(cis)); // false

        // 컬렉션에서 대칭성 위배 예제
        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);
        System.out.println("list.contains(s) : " + list.contains(s)); // false
    }
}
