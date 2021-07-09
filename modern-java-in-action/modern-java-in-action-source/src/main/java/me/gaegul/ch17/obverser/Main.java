package me.gaegul.ch17.obverser;

import io.reactivex.Observable;
import me.gaegul.ch17.TempInfo;

import static me.gaegul.ch17.obverser.TempObservable.*;

public class Main {
    public static void main(String[] args) {
        temperature();
        celsiusTemperature();
        negativeTemperature();
        celsiusTemperatures();
    }

    private static void temperature() {
        getTempObservable(getTemperature("New York"));
    }

    private static void celsiusTemperature() {
        getTempObservable(getCelsiusTemperature("New York"));
    }

    private static void negativeTemperature() {
        getTempObservable(getNegativeTemperature("New York"));
    }

    private static void celsiusTemperatures() {
        getTempObservable(getCelsiusTemperatures("New York", "Chicago", "San Francisco"));
    }

    private static void getTempObservable(Observable<TempInfo> observable) {
        observable.blockingSubscribe(new TempObserver());
    }
}
