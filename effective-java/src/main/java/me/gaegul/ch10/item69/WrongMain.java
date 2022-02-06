package me.gaegul.ch10.item69;

public class WrongMain {

    public static void main(String[] args) {
        Mountain[] range = new Mountain[]{};
        try {
            int i = 0;
            while (true) {
                range[i++].climb();
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
