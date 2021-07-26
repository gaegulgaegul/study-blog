package me.gaegul.ch02.item05.wrong_singleton;

import me.gaegul.ch02.item05.Lexicon;

import java.util.List;

public class SpellChecker {
    private final Lexicon dictionary = new Lexicon();

    private SpellChecker(/* ... */) {}

    public static SpellChecker INSTANCE = new SpellChecker(/* ... */);

    public boolean isValid(String word) {
        return false;
    }

    public List<String> suggestions(String typo) {
        return null;
    }
}
