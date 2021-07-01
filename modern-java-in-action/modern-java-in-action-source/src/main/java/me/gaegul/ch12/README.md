# 12. 새로운 날짜와 시간 API

- LocalDate, LocalTime, Instant, Duration, Period 클래스
    - LocalDate와 LocalTime 사용
        - LocalDate → 시간을 제외한 날짜를 표현하는 불변 객체
            - LocalDate.of() → 년, 월, 일 지정

            ```java
            LocalDate date = LocalDate.of(2017, 9, 21);
            int year = date.getYear();
            Month month = date.getMonth();
            int day = date.getDayOfMonth();
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            int len = date.lengthOfMonth();
            boolean leapYear = date.isLeapYear();
            ```

            - LocalDate.now() → 현재 날짜

            ```java
            LocalDate date = LocalDate.now();
            int year = date.get(ChronoField.YEAR);
            int month = date.get(ChronoField.MONTH_OF_YEAR);
            int day = date.get(ChronoField.DAY_OF_MONTH);
            ```

        - LocalTime → 시간을 표현하는 불변 객체
            - LocalTime.of
                - 시간, 분을 인수로 받는다.
                - 시간, 분, 초를 인수로 받는다.

            ```java
            LocalTime time = LocalTime.of(13, 45, 20);
            int hour = time.getHour();
            int minute = time.getMinute();
            int second = time.getSecond();
            ```

    - 날짜와 시간 조합
        - LocalDateTime → 날짜와 시간 모두 표현할 수 있다.

        ```java
        LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);
        ```

    - Instant 클래스 : 기계의 날짜와 시간
        - 사람이 표현하는 날짜, 시간은 기계에서 표현하기 어렵다.
        - 기계의 관점에서 연속된 시간에서 특정 지점을 하나의 큰 수로 표현하는 것이 자연스러운 방식이다.
        - Instant.ofEpochSecond 메서드를 통해 기계의 관점에서 시간을 표현한다.
    - Duration과 Period 정의
        - Duration → 두 시간 객체 사이의 지속시간
        - Period → 두 날짜 객체 사이의 기간

        ```java
        Duration threeMinutes = Duration.ofMinutes(3);
        Duration threeMinutes2 = Duration.of(3, ChronoUnit.MINUTES);

        Period tenDays = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
        ```

- 날짜 조정, 파싱, 포매팅
    - withAttribute → 절대적인 방식으로 LocalDate의 속성을 바꾼다.

    ```java
    LocalDate date1 = LocalDate.of(2017, 9, 21);
    LocalDate date2 = date1.withYear(2011);
    LocalDate date3 = date2.withDayOfMonth(25);
    LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);
    ```

    - 상대적인 방식으로 LocalDate의 속성 바꾸기

    ```java
    LocalDate date1 = LocalDate.of(2017, 9, 21);
    LocalDate date2 = date1.plusWeeks(1);
    LocalDate date3 = date2.minusYears(6);
    LocalDate date4 = date3.plus(6, ChronoUnit.MONTHS);
    ```

    - TemporalAdjusters 사용하기
        - with 메서드에서 좀 더 다양한 동작을 수행할 수 있도록 하는 기능을 제공한다.
        - 커스텀하게 구현할 경우 TemporalAdjuster 구현
        - TemporalAdjusters 정적 팩토리 메서드 구현체

        ```java
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
        LocalDate date3 = date2.with(lastDayOfMonth());
        ```

    - 날짜와 시간 객체 출력과 파싱
        - 패턴으로 DateTimeFormatter 만들기

        ```java
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        String formattedDate = date1.format(formatter);
        LocalDate date2 = LocalDate.parse(formattedDate, formatter);
        ```

        - 지역화된 DateTimeFormatter 만들기

        ```java
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        LocalDate date1 = LocalDate.of(2014, 3, 8);
        String formattedDate = date1.format(italianFormatter);
        LocalDate date2 = LocalDate.parse(formattedDate, italianFormatter);
        ```

        - DateTimeFormatterBuilder
            - 세부적으로 포매터를 제어할 수 있다.

        ```java
        DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);
        ```

- 다양한 시간대와 캘린더 활용법
    - 시간대 사용하기
        - 표준 시간이 같은 지역을 묶어서 시간대 규칙 집합을 정의한다.
            - ZoneRules 클래스의 시간대를 이용한다.
            - ZonedDateTime을 통해 지정한 시간의 상대적인 시점을 표현한다.
    - UTC/Greenwich 기준의 고정 오프셋
        - 협정 세계시/그리니치 표준시를 기준으로 시간대를 표현한다.
            - OffsetDateTime을 만들어 사용는 것을 권장한다.