package me.gaegul.ch09.chainOfResponsibility;

import lombok.Setter;

@Setter
public abstract class ProcessingObject<T> {

    protected ProcessingObject<T> successor;

    public T handle(T input) {
        T r = handleWork(input);
        if (successor != null) {
            return successor.handle(r);
        }
        return r;
    }

    protected abstract T handleWork(T input);
}
