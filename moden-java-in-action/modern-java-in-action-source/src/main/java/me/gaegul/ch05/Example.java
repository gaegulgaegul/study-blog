package me.gaegul.ch05;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Example {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        System.out.println("===========================================================");
        System.out.println("거래자와 트랜잭션");
        System.out.println("===========================================================");
        System.out.println("1. 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리하시오.");

        List<Transaction> tr2011 = transactions.stream()
                .filter(t -> t.getYear() == 2011)
                .sorted(comparing(Transaction::getValue))
                .collect(toList());
        tr2011.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("2. 거래자가 근무하는 모든 도시를 중복 없이 나열하시오.");

        List<String> cities = transactions.stream()
                .map(Transaction::getTrader)
                .map(Trader::getCity)
                .distinct()
                .collect(toList());
        System.out.println(cities.toString());

        System.out.println("===========================================================");
        System.out.println("3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.");

        List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader)
                .filter(t -> t.getCity().equals("Cambridge"))
                .distinct()
                .sorted(comparing(Trader::getName))
                .collect(toList());
        traders.forEach(System.out::println);

        System.out.println("===========================================================");
        System.out.println("4. 모든 거래자의 이름을 알파벳순으로 정렬해서 반환하시오.");

        List<String> traderNames = transactions.stream()
                .map(t -> t.getTrader().getName())
                .distinct()
                .sorted()
                .collect(toList());
        System.out.println(traderNames.toString());

        System.out.println("===========================================================");
        System.out.println("5. 밀라노에 거래자가 있는가?");

        boolean isMilanBased = transactions.stream()
                .anyMatch(t -> t.getTrader().getCity().equals("Milan"));
        System.out.println(isMilanBased);

        System.out.println("===========================================================");
        System.out.println("6. 케임브리지에 거주하는 거래자의 모든 트랙잭션값을 출력하시오.");

        List<Integer> cambridgeTransactionValues = transactions.stream()
                .filter(t -> t.getTrader().getCity().equals("Cambridge"))
                .map(Transaction::getValue)
                .collect(toList());
        System.out.println(cambridgeTransactionValues.toString());

        System.out.println("===========================================================");
        System.out.println("7. 전체 트랜잭선 중 최댓값은 얼마인가?");

        Optional<Integer> maxValue = transactions.stream()
                .map(Transaction::getValue)
                .reduce(Integer::max);
        System.out.println(maxValue.get());

        System.out.println("===========================================================");
        System.out.println("8. 전체 트랜잭선 중 최솟값은 얼마인가?");

        Optional<Integer> minValue = transactions.stream()
                .map(Transaction::getValue)
                .reduce(Integer::min);
        System.out.println(minValue.get());

        System.out.println("===========================================================");
    }
}
