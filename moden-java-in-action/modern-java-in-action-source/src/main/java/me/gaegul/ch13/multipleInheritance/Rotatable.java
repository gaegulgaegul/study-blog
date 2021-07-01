package me.gaegul.ch13.multipleInheritance;

public interface Rotatable {

    void setRotatable(int angleInDegrees);
    int getRotatable();
    default void rotateBy(int angleInDegrees) {
        setRotatable((getRotatable() + angleInDegrees) % 360);
    }
}
