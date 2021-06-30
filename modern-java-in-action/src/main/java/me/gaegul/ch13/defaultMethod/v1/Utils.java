package me.gaegul.ch13.defaultMethod.v1;

import java.util.List;

public class Utils {
    public static void paint(List<Resizable> l) {
        l.forEach(r -> {
            r.setAbsoluteSize(42, 42);
            r.draw();
        });
    }
}
