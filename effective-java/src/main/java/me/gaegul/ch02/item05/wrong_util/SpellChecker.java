package me.gaegul.ch02.item05.wrong_util;

import me.gaegul.ch02.item05.Lexicon;

import java.util.List;

public class SpellChecker {
    private static final Lexicon dictionary = new Lexicon();

    private SpellChecker() {} // 객체 생성 방지

    private static boolean isValid(String word) {
        return false;
    }

    private static List<String> suggestions(String typo) {
        return null;
    }
}
