package me.gaegul.ch09.chainOfResponsibility;

public class SpellCheckerProcessing extends ProcessingObject<String> {
    @Override
    protected String handleWork(String text) {
        return text.replace("labda", "lambda");
    }
}
