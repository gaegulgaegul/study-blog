# 아이템 60. 정확한 답이 필요하다면 float와 double은 피하라

### 이진 부동소수점

- float와 double 타입은 과학과 공학 계산용으로 설계되었다.
- 넓은 범위의 수를 빠르게 정밀한 근사치로 계산하도록 세심하게 설계되었다.
- 금융 관련 계산과 맞지 않다.

    ```java
    double funds = 1.00;
    int itemsBought = 0;
    for (double price = 0.10; funds >= price; price += 0.10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
    
    // console
    3개 구입
    잔돈(달러):0.3999999999999999
    ```


### 금융 계산에는 BigDecimal

- 금융계산에는 BigDecimal, int 혹은 long 를 사용해야 한다.

    ```java
    final BigDecimal TEN_CENTS = new BigDecimal(".10");
    
    int itemsBought = 0;
    BigDecimal funds = new BigDecimal("1.00");
    for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0; price = funds.add(TEN_CENTS)) {
        funds = funds.subtract(price);
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
    
    // console
    1개 구입
    잔돈(달러):0.90
    ```

- BigDecimal은 기본 타입보다 쓰기 불편하고 느리다는 단점이 있다.
- 반올림 기능이 필요하다면 BigDecimal이 좋다.
- 성능이 중요하고 소수점을 직접 추적할 수 있고 숫자가 너무 크지 않다면 int나 long을 사용하라
    - 아홉 자리 십진수 표현 → int
    - 열여덟 자리 십진수 표현 → long
    - 열여덟 자리 이상 십진수 표현 → BigDecimal