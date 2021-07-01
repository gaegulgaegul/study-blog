package me.gaegul.ch03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AfterApp {

    private static final String FILE_PATH = "src/main/java/me/gaegul/ch03/data.txt";

    public static void main(String[] args) throws IOException {
        AfterApp afterApp = new AfterApp();

        String oneLine = afterApp.processFile(br -> br.readLine());
        System.out.println(oneLine);

        String twoLine = afterApp.processFile(br -> br.readLine() + br.readLine());
        System.out.println(twoLine);
    }

    public String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            return p.process(br);
        }
    }
}
