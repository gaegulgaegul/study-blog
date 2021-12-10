# 아이템 48. 스트림 병렬화는 주의해서 적용하라

### 동시성 작업

- 자바로 동시성 프로그램을 작성하기는 쉬워지지만 올바르고 빠르게 작성하는 일은 어려운 작업
- 동시성 프로그래밍을 할 때는 안정성, 응답 가능 상태를 유지해야 한다.

### 메르센 소수

```java
public class Mersenne {

    static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime).parallel();
    }

    public static void main(String[] args) {
        primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(mp -> System.out.println(mp.bitLength() + ": " + mp));
    }
}
```

- `parallel`
    - 스트림 라이브러리가 파이프라인의 병렬화하는 방법을 찾지 못 한다.
    - 데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로 성능 개선 할 수 없다.

<aside>
💡 스트림 파이프라인을 마구잡이로 병렬화시키면 안된다.

</aside>

### 스트림 병렬화 효과가 좋은 소스

- ArrayList, HashMap, HashSet, ConcurrentHashMap
- int 배열, long 배열
- 모두 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어 일을 다수의 스레드에 분배하기 좋다.
    - 작업은 Spliterator가 담당
        - Stream 또는 Iterable의 spliterator 메서드
- 원소들을 순차적으로 실행할 때 참조 지역성이 좋다.
    - 이웃한 원소의 참조들이 메모리에 연속해서 저장된다.
    - 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 아주 중요한 요소로 작용한다.
    - 참조 지역성이 가장 좋은 것은 **기본 타입의 배열**이다.


### 스트림 파이프라인의 종단 연산 동작 방식

- 종단 연산 중 병렬화에 가장 적합한 것은 축소
    - Stream의 reduce
    - min, max, count, sum
- 조건에 맞으면 바로 반환되는 메서드
    - anyMaych, allMatch, nonMatch
- Stream의 collect 메서드는 병렬화 작업에 가장 적합하지 않다.
    - 직접 구현한 Stream, Iterable, Collection이 병렬화를 제대로 사용하고 싶다면 spliterator 메서드를 재정의하고 결과 스트림의 병렬화 성능을 강도 높게 테스트 해야 한다.


### 스트림을 잘못 병렬화 하는 경우

- 스트림을 잘못 병렬화하면 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다.
- 결과가 잘못되거나 오작동하는 것은 안전 실패
    - 병렬화한 파이프라인이 사용하는 mappers, filters, 프로그래머가 제공한 다른 함수 객체가 명세대로 동작하지 않을 때 발생한다.
- Stream 명세의 함수 객체에 관한 엄중한 규약(비간섭, 상태를 갖지 않는다 등)을 지켜야 한다.

### 스트림 병렬화 적용 조건

> (스트림 안의 원소 수 X 수행되는 코드 줄 수) > 최소 수십 만
>
- 스트림 병렬화는 오직 성능 최적화 수단이다.
- 반드시 성능 테스트를 하여 병렬화를 사용할 가치가 있는지 확인해야 한다.
- 조건이 잘 갖춰지면 parallel 메서드 호출 하나로 거의 프로세서 코어 수에 비례하는 성능이 향상된다.
    - 소수 계산 스트림 파이프라인

        ```java
        private static long pi(Long n) {
            return LongStream.rangeClosed(2, n)
                    .mapToObj(BigInteger::valueOf)
                    .filter(i -> i.isProbablePrime(50))
                    .count();
        }
        ```

    - 병렬화 버전

        ```java
        private static long piWithParallel(Long n) {
            return LongStream.rangeClosed(2, n)
                    .parallel()
                    .mapToObj(BigInteger::valueOf)
                    .filter(i -> i.isProbablePrime(50))
                    .count();
        }
        ```