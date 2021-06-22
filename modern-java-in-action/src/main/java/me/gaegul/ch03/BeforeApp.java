package me.gaegul.ch03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BeforeApp {

    private static final String FILE_PATH = "src/main/java/me/gaegul/ch03/data.txt";

    public static void main(String[] args) throws IOException {
        BeforeApp beforeApp = new BeforeApp();
        System.out.println(beforeApp.processFile());
    }

    public String processFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            return br.readLine();
        }
    }
}
