package me.gaegul.ch13.defaultMethod.v1;

public interface Resizable extends Drawable {

    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);

}
