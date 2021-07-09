package com.example.expenses.application;

import com.example.expenses.readers.Reader;
import com.example.expenses.readers.file.FileReader;
import com.example.expenses.readers.http.HttpReader;

public class ExpensesApplication {
    public static void main(String[] args) {
        System.out.println("module test");

        Reader.print();
        FileReader.print();
        HttpReader.print();
    }
}
