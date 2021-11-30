package me.gaegul.ch08.item50.period;

import java.util.Date;

public class PeriodMain {
    public static void main(String[] args) {
        Date start = new Date();
        Date end = new Date();
        Period p = new Period(start, end);
        end.setYear(78); // p의 내부를 수정했다.
    }
}
