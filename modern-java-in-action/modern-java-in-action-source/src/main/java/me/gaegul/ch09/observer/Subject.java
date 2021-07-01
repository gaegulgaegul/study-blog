package me.gaegul.ch09.observer;

public interface Subject {
    void registerObserver(Observer observer);
    void notifyObserver(String tweet);
}
