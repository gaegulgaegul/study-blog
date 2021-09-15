package me.gaegul.ch05.item31;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Chooser<T> {

    private final List<T> choiceList;

    public Chooser(Collection<? extends T> choices) {
        this.choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }

    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1,2,3,4,5);
        Chooser<Number> numberChooser = new Chooser<>(integers);
        System.out.println(numberChooser.choose());
    }
}
