# 아이템 45. 스트림은 주의해서 사용하라

### 스트림 API

- 다량의 데이터 처리 작업을 돕는다.
- 핵심적인 추상 개념
    - 데이터 원소의 유한 혹은 무한 시퀀스를 뜻한다.
    - 스트림 파이프라인은 이 원소들로 수행하는 연산단계를 표현하는 개념
    

### 스트림의 원소

- 컬렉션, 배열, 파일, 정규표현식 패턴 matcher, 난수 생성기, 다른 스트림
- 스트림 안의 데이터 원소들은 객체 참조나 기본 타입 값(int, long, double)

### 스트림 파이프라인

- 동작 방식
    - 소스 스트림에서 시작해 종단 연산으로 끝난다.
    - 사이에 하나 이상의 중간 연산(스트림의 변환)이 있을 수 있다.
- 지연 평가(Lazy evaluation)
    - 평가는 종단 연산이 호출될 때 이뤄지며, 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다.
    - 종단 연산이 없는 스트림 파이프라인은 아무 일도 하지 않는다.
- 플루언트 API(fluent API)
    - 메서드 연쇄 지원
    - 파이프라인 여러 개를 연결해 표현식 하나로 만들 수 있다.
- 병렬 처리
    - 스트림 파이프라인은 기본적으로 순차 처리
    - parallel 메서드를 통해 병렬 처리
    

### 스트림을 제대로 사용하는 노하우

- 아나그램(Anagram)
    - 사전 파일에서 단어를 읽어 사용자가 지정한 문턱값보다 원소 수가 많은 아나그램 그룹 출력
        
        ```java
        public class Anagrams {
            public static void main(String[] args) throws FileNotFoundException {
                File dictionary = new File(args[0]);
                int minGroupSize = Integer.parseInt(args[1]);
        
                Map<String, Set<String>> groups = new HashMap<>();
                try (Scanner s = new Scanner(dictionary)) {
                    while (s.hasNext()) {
                        String word = s.next();
        
                        groups.computeIfAbsent(alphabetize(word),
                                (unused) -> new TreeSet<>())
                                .add(word);
                    }
                }
        
                for (Set<String> group : groups.values()) {
                    if (group.size() >= minGroupSize)
                        System.out.println(group.size() + ": " + group);
                }
            }
        
            private static String alphabetize(String s) {
                char[] a = s.toCharArray();
                Arrays.sort(a);
                return new String(a);
            }
        }
        ```
        
        - computeIfAbsent
            - 맵 안에 키가 있는지 찾아 있으면 키에 매핑된 값 반환, 없으면 함수 객체를 키에 적용하여 값을 계산해 반환
            - 각 키에 다수의 값을 매핑하는 맵을 쉽게 구현
    - 과한 스트림 사용
        
        ```java
        public class Anagrams {
            public static void main(String[] args) throws IOException {
                Path dictionary = Paths.get(args[0]);
                int minGroupSize = Integer.parseInt(args[1]);
                
                try (Stream<String> words = Files.lines(dictionary)) {
                    words.collect(
                            groupingBy(word -> word.chars().sorted()
                                    .collect(StringBuilder::new,
                                            (sb, c) -> sb.append((char) c),
                                            StringBuilder::append).toString()))
                            .values().stream()
                            .filter(group -> group.size() >= minGroupSize)
                            .map(group -> group.size() + ": " + group)
                            .forEach(System.out::println);
                }
            }
        }
        ```
        
        - 코드가 읽거나 유지보수하기 어렵다.
    - 적절한 스트림 활용
        
        ```java
        public class Anagrams {
            public static void main(String[] args) throws IOException {
                Path dictionary = Paths.get(args[0]);
                int minGroupSize = Integer.parseInt(args[1]);
        
                try (Stream<String> words = Files.lines(dictionary)) {
                    words.collect(groupingBy(word -> alphabetize(word)))
                            .values().stream()
                            .filter(group -> group.size() >= minGroupSize)
                            .forEach(g -> System.out.println(g.size() + ": " + g));
                }
            }
        
            private static String alphabetize(String s) {
                char[] a = s.toCharArray();
                Arrays.sort(a);
                return new String(a);
            }
        }
        ```
        
        - 람다에서 타입 이름을 자주 생략하므로 매개변수 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다.
        - 도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복 코드에서보다 스트림 파이프라인에서 훨씬 크다.
- char 값들을 처리할 때는 스트림을 사용하지 않는 것이 좋다.
    
    ```java
    "Hello world!".chars().forEach(System.out::print);
    
    // console
    721011081081113211911111410810033
    
    "Hello world!".chars().forEach(x -> System.out.print((char) x));
    
    // console
    Hello world!
    ```
    
- 기존 코드는 스트림을 사용하도록 리팩터링하되 새 코드가 더 나아 보일 때만 반영하자.
    - 스트림으로 바꾸는게 가능해도 유지보수 측면에서 손해를 볼 수 있다.
    - 스트림과 반복문을 적절히 조합하는 것이 최선이다.
    

### 스트림으로는 할 수 없지만 반복문으로 할 수 있는 일

- 범위 안의 지역변수를 읽고 수정할 수 있다.
    - 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있다.
    - 지역변수를 수정하는 것은 불가능
- return 문을 사용해 메서드에서 빠져나가거나, break나 continue 문을 사용할 수 있다.
    - 람다는 할 수 없다.
- 메서드 선언에서 명시된 검사 예외를 던질 수 있다.
    - 람다는 할 수 없다.

### 스트림으로 하기 적절한 일

- 원소들의 시퀀스를 일관되게 변환
- 원소들의 시퀀스를 필터링
- 원소들의 시퀀스를 하나의 연산을 사용해 결합
    - 더하기, 연결하기, 최솟값 구하기 등
- 원소들의 시퀀스를 컬렉션에 모은다
    - 공통된 속성을 기준으로 묶어가며
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

### 스트림으로 처리하기 어려운 일

- 한 데이터가 여러 단계의 파이프라인을 통과할 때 각 단계에서의 값에 동시에 접근하기 어렵다.
    - 하나의 파이프라인을 통과하면 기존의 값은 사라진다.
    - 기존 값과 결과 값을 쌍으로 저장하는 객체를 사용할 수 있지만 스트림의 의도와 멀어진다.
- 메르센 소수
    
    ```java
    public class Mersenne {
    
        static Stream<BigInteger> primes() {
            return Stream.iterate(TWO, BigInteger::nextProbablePrime);
        }
    
        public static void main(String[] args) {
            primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                    .filter(mersenne -> mersenne.isProbablePrime(50))
                    .limit(20)
                    .forEach(System.out::println);
        }
    }
    ```
    
    - 소수들을 사용해 메르센 수를 계산하고, 결과값이 소수인 경우만 남긴 다음, 결과 스트림의 원소 수를 20개로 제한해놓고, 작업이 끝나면 출력한다.
    - 만약 map 파이프라인의 p를 출력해야 한다고 가정했을 때
        - 초기 스트림에만 나타나므로 결과를 출력하는 종단 연산에서는 접근할 수 없다.
        - 첫번째 중간 연산에서 수행한 매핑을 거꾸로 수행해 메르센 수의 지수를 쉽게 계산한다.
        
        ```java
        primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(mp -> System.out.println(mp.bitLength() + ": " + mp));
        ```
        
- 데카르트 곱 계산
    - 두 집합의 원소들로 만들 수 있는 모든 조합을 계산한다.
    - for-each 방식
        
        ```java
        private static List<Card> newDeck() {
          List<Card> result = new ArrayList<>();
          for (Suit suit : Suit.values()) {
              for (Rank rank : Rank.values()) {
                  result.add(new Card(suit, rank));
              }
          }
          return result;
        }
        ```
        
    - Stream 방식
        
        ```java
        private static List<Card> newDeckStream() {
            return Stream.of(Suit.values())
                   .flatMap(suit ->
                           Stream.of(Rank.values())
                                   .map(rank -> new Card(suit, rank)))
                    .collect(Collectors.toList());
        }
        ```