package me.gaegul.ch12;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.*;

public class FormatApp {
    public static void main(String[] args) {
        changeAbsoluteLocalDate();
        changeRelativeLocalDate();
        getTemporalAdjusters();
        getCustomDateTimeFormatter();
        getLocalDateTimeFormatter();
        getDateTimeFormatterBuilder();
    }

    private static void changeAbsoluteLocalDate() {
        System.out.println("===================== 절대적인 방식으로 속성 바꾸기 =======================");
        LocalDate date1 = LocalDate.of(2017, 9, 21);
        System.out.println(date1);
        LocalDate date2 = date1.withYear(2011);
        System.out.println(date2);
        LocalDate date3 = date2.withDayOfMonth(25);
        System.out.println(date3);
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);
        System.out.println(date4);
    }

    private static void changeRelativeLocalDate() {
        System.out.println("===================== 상대적인 방식으로 속성 바꾸기 =======================");
        LocalDate date1 = LocalDate.of(2017, 9, 21);
        System.out.println(date1);
        LocalDate date2 = date1.plusWeeks(1);
        System.out.println(date2);
        LocalDate date3 = date2.minusYears(6);
        System.out.println(date3);
        LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS);
        System.out.println(date4);
    }

    private static void getTemporalAdjusters() {
        System.out.println("===================== TemporalAdjusters =======================");
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        System.out.println(date1);
        LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
        System.out.println(date2);
        LocalDate date3 = date2.with(lastDayOfMonth());
        System.out.println(date3);
    }

    private static void getCustomDateTimeFormatter() {
        System.out.println("===================== 패턴으로 DateTimeFormatter 만들기 =======================");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        getDateTimeFomatter(formatter, 18);
    }

    private static void getLocalDateTimeFormatter() {
        System.out.println("===================== 지역화된 DateTimeFormatter 만들기 =======================");
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        getDateTimeFomatter(italianFormatter, 8);
    }

    private static void getDateTimeFormatterBuilder() {
        System.out.println("===================== DateTimeFormatterBuilder =======================");
        DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);
        getDateTimeFomatter(italianFormatter, 8);
    }

    private static void getDateTimeFomatter(DateTimeFormatter italianFormatter, int i) {
        LocalDate date1 = LocalDate.of(2014, 3, i);
        System.out.println(date1);
        String formattedDate = date1.format(italianFormatter);
        System.out.println(formattedDate);
        LocalDate date2 = LocalDate.parse(formattedDate, italianFormatter);
        System.out.println(date2);
    }
}
