package me.gaegul.ch03.item11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PhoneNumber {

    private final short areaCode, prefix, lineNum;
    private int hashCode;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "지역코드");
        this.prefix = rangeCheck(prefix, 999, "프리픽스");
        this.lineNum = rangeCheck(lineNum, 9999, "가입자 번호");
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max) throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PhoneNumber)) return false;
        PhoneNumber that = (PhoneNumber) o;
        return areaCode == that.areaCode &&
                prefix == that.prefix &&
                lineNum == that.lineNum;
    }

    // 전형적인 hashCode 메서드
//    @Override
//    public int hashCode() {
//        int result = Short.hashCode(areaCode);
//        result += 31 * result + Short.hashCode(prefix);
//        result += 31 * result + Short.hashCode(lineNum);
//        return result;
//    }

    // Objects 클래스의 hash 정적 메서드
//    @Override
//    public int hashCode() {
//        return Objects.hash(areaCode, prefix, lineNum);
//    }

    // 해시코드를 지연 초기화 - 스레드 안정성까지 고려해야 한다.
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result += 31 * result + Short.hashCode(areaCode);
            result += 31 * result + Short.hashCode(prefix);
            result += 31 * result + Short.hashCode(lineNum);
            hashCode = result;
        }
        return result;
    }

    public static void main(String[] args) {
        Map<PhoneNumber, String> m = new HashMap<>();
        m.put(new PhoneNumber(707, 867, 5309), "제니");
        System.out.println(m.get(new PhoneNumber(707, 867, 5309)));
    }
}
