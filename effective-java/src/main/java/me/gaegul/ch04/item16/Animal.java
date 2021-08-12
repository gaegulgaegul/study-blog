package me.gaegul.ch04.item16;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Animal {

    private String type;
    private String name;

    public static void main(String[] args) {
        Animal cat = new Animal("고양이", "나비");
        System.out.println("종류 : " + cat.type + ", 이름 : " + cat.name);
    }
}
