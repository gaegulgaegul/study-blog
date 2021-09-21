package me.gaegul.ch06.item36;

import java.util.Set;

public class Text {
//    public static final int STYLE_BOLD = 1 << 0; // 1
//    public static final int STYLE_ITALIC = 1 << 1; // 2
//    public static final int STYLE_UNDERLINE = 1 << 2; // 3
//    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 4
//
//    // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
//    public void applyStyles(int styles) {}

    public enum Style {
        BOLD, ITALIC, UNDERLINE, STRIKETHROUGH
    }

    public void applyStyles(Set<Style> styles) {}
}
