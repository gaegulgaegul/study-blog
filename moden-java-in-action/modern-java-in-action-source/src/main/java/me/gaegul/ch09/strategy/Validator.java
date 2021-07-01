package me.gaegul.ch09.strategy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Validator {

    private final ValidationStrategy strategy;

    public boolean validate(String s) {
        return strategy.execute(s);
    }
}
