package me.gaegul.ch12;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class DateTimeApp {
    public static void main(String[] args) {
        getLocalDateOf();
        getLocalDateNow();
        getLocalTimeOf();
        getLocalDateTimeOf();
        getDurationAndPeriod();
    }

    private static void getLocalDateOf() {
        System.out.println("=================== LocalDate.of =====================");
        LocalDate date = LocalDate.of(2017, 9, 21);

        int year = date.getYear();
        System.out.println(year);

        Month month = date.getMonth();
        System.out.println(month);

        int day = date.getDayOfMonth();
        System.out.println(day);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        System.out.println(dayOfWeek);

        int len = date.lengthOfMonth();
        System.out.println(len);

        boolean leapYear = date.isLeapYear();
        System.out.println(leapYear);
    }

    private static void getLocalDateNow() {
        System.out.println("=================== LocalDate.now =====================");
        LocalDate date = LocalDate.now();

        int year = date.get(ChronoField.YEAR);
        System.out.println(year);

        int month = date.get(ChronoField.MONTH_OF_YEAR);
        System.out.println(month);

        int day = date.get(ChronoField.DAY_OF_MONTH);
        System.out.println(day);
    }

    private static void getLocalTimeOf() {
        System.out.println("=================== LocalTime.of =====================");
        LocalTime time = LocalTime.of(13, 45, 20);
        int hour = time.getHour();
        System.out.println(hour);

        int minute = time.getMinute();
        System.out.println(minute);

        int second = time.getSecond();
        System.out.println(second);
    }

    private static void getLocalDateTimeOf() {
        System.out.println("=================== LocalDateTime =====================");
        LocalDate date = LocalDate.of(2017, 9, 21);
        LocalTime time = LocalTime.of(13, 45, 20);

        LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
        System.out.println(dt1);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        System.out.println(dt2);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        System.out.println(dt3);
        LocalDateTime dt4 = date.atTime(time);
        System.out.println(dt4);
        LocalDateTime dt5 = time.atDate(date);
        System.out.println(dt5);
    }

    private static void getDurationAndPeriod() {
        System.out.println("=================== Duration =====================");

        Duration threeMinutes = Duration.ofMinutes(3);
        System.out.println(threeMinutes);
        Duration threeMinutes2 = Duration.of(3, ChronoUnit.MINUTES);
        System.out.println(threeMinutes2);

        System.out.println("=================== Period =====================");

        Period tenDays = Period.ofDays(10);
        System.out.println(tenDays);
        Period threeWeeks = Period.ofWeeks(3);
        System.out.println(threeWeeks);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
        System.out.println(twoYearsSixMonthsOneDay);
    }

}
