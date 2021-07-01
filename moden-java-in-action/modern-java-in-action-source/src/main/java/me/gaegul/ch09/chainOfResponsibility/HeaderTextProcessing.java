package me.gaegul.ch09.chainOfResponsibility;

public class HeaderTextProcessing extends ProcessingObject<String> {
    @Override
    protected String handleWork(String text) {
        return "Form Raoul, Mario and Alan: " + text;
    }
}
