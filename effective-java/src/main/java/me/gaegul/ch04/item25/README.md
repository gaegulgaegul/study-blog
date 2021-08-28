# 아이템 25. 톱레벨 클래스는 한 파일에 하나만 담으라

- 소스 파일 하나에 톱레벨 클래스 여러 개를 선언
    - 컴파일러에서는 문제 없다.
    - 한 클래스를 여러 가지로 정의할 수 있다.
        - 어느 파일의 소스를 먼저 컴파일하는지에 따라 사용이 달라진다.

            ```bash
            > javac Main.java Utensil.java
            // Main.class
            package me.gaegul.ch04.item25.test;

            public class Main {
                public Main() {
                }

                public static void main(String[] var0) {
                    System.out.println("pancake");
                }
            }

            > javac Dessert.java Main.java

            // Main.class
            package me.gaegul.ch04.item25.test;

            public class Main {
                public Main() {
                }

                public static void main(String[] var0) {
                    System.out.println("potpie");
                }
            }
            ```

        - 클래스 명이 같아 컴파일 에러가 발생한다.

            ```java
            // Utensil.java
            class Utensil {
                static final String NAME = "pan";
            }

            class Dessert {
                static final String NAME = "cake";
            }

            // Dessert.java
            class Utensil {
                static final String NAME = "pot";
            }

            class Dessert {
                static final String NAME = "cake";
            }

            // 클래스가 중복되었다고 에러 발생
            ```

- 톱레벨 클래스들은 서로 다른 파일로 분리한다.
    - 여러 클래스를 한 파일에 담고 싶으면 정적 멤버 클래스로 선언한다.

        ```java
        public class Test {
            public static void main(String[] args) {
                System.out.println(Utensil.NAME + Dessert.NAME);
            }

            private static class Utensil {
                static final String NAME = "pan";
            }

            private static class Dessert {
                static final String NAME = "cake";
            }
        }
        ```