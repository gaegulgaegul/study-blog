package me.gaegul.ch02.item03.public_static_final_field;

public class Elvis {
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {
    }

    public void leaveTheBuilding() {

    }

    // 싱글턴임을 보장해주는 readResolve
    private Object readResolve() {
        // 진짜 Elvis를 반환하고, 가짜 Elvis는 가비지 컬렉터에 맡긴다.
        return INSTANCE;
    }
}
