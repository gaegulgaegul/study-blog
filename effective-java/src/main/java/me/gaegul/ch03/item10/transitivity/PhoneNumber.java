package me.gaegul.ch03.item10.transitivity;

import java.util.Objects;

public final class PhoneNumber {

    private final short areaCode, prefix, lineNum;

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

    @Override
    public int hashCode() {
        return Objects.hash(areaCode, prefix, lineNum);
    }

    public static void main(String[] args) {
        PhoneNumber phoneNumber1 = new PhoneNumber(02, 123, 4567);
        PhoneNumber phoneNumber2 = new PhoneNumber(02, 123, 4567);

        System.out.println("phoneNumber1.equals(phoneNumber2) : " + phoneNumber1.equals(phoneNumber2));
        System.out.println("phoneNumber2.equals(phoneNumber1) : " + phoneNumber2.equals(phoneNumber1));
    }
}
