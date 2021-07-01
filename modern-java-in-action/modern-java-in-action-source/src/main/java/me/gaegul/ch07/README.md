# 7. 병렬 데이터 처리와 성능

- 병렬 스트림
    - 순차 스트림을 병렬 스트림으로 변환하기
        - 순차 스트림에 parallel 메서드를 호출하면 병렬로 처리된다.
        - parallel을 내부적으로 호출하면 boolean flag가 설정된다.

        ```java
        public long parallelSum(long n) {
        	return Stream.iterate(1L, i -> i + 1)
                       .limit(n)
                       .parallel()
                       .reduce(0L, Long::sum);
        }
        ```

        - sequential 메서드를 호출하면 병렬 스트림을 순차 스트림으로 변환한다.
        - 연산별로 병렬로 실행할지 순차로 실행할지 구분할 수 있다.
        - 처리방식을 둘 다 선언한 경우 스트림 파이프라인의 마지막 호출에 따라 구분된다.

        ```java
        Stream.parallel()
              .filter(...)
              .sequential()
              .map(...)
              .parallel() // 마지막이 병렬 처리 선언이므로 병렬로 실행
              .reduce();
        ```

    - 스트림 성능 측정
        - 자바 마이크로벤치마크 하니스(JMH) → 성능 측정 라이브러리

        ```java
        @Benchmark
        public long parallelSum(long n) {
        	return Stream.iterate(1L, i -> i + 1)
                       .limit(n)
                       .reduce(0L, Long::sum);
        }
        ```

        - 스트림 측정
            - 순차 스트림 → 3.278 score
            - 병렬 스트림 → 604.059 score
            - 측정 결과 문제점
                - 반복 결과로 박싱된 객체가 만들어지므로 숫자를 더하려면 언박싱을 해야 한다.
                - 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기 어렵다. → iterate 연산을 청크로 분할하기 어렵다. 따라서 순차처리 방식과 유사하게 움직인다.
        - 더 특화된 메서드 사용
            - LongStream.rangeClosed를 통해 박싱과 언박싱 오버헤드를 없앤다.

            ```java
            @Benchmark
            public long rangeSum() {
            	return LongStream.rangeClosed(1, N)
                               .reduce(0L, Integer::sum);
            }
            ```

            - 스트림 측정
                - 순차 스트림 → 5.315 score
                - 병렬 스트림 → 2.677 score
            - 병렬 스트림이 더 빠르게 하기 위해 필요한 조건
                - 스트림을 재귀적으로 분할
                - 각 서브스트림을 서로 다른 스레드의 리듀싱 연산으로 할당
                - 결과를 하나로 할당
    - 병렬 스트림 효과적으로 사용하기
        - 확실하지 않으면 측정해라
        - 자동 박싱, 언박싱을 주의하라
        - 순차 스트림보다 병렬 스트림이 성능이 떨어지는 연산이 있다.
            - limit, findFirst 등 요소의 순서에 의존하는 연산은 순차 스트림이 적합하다.
        - 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하라
            - 처리할 요소 수 N, 하나의 요소를 처리하는데 드는 비용 Q, 전체 스트림 파이프라인 처리 비용 N*Q
            - Q가 높아지는 것은 성능 개선을 위해 병렬 스트림으로 전환한다.
        - 소량의 데이터에서는 병렬 스트림이 도움되지 않는다.
        - 스트림을 구성하는 자료구조가 적절한지 확인하라
        - 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.
        - 최종 연산의 병합 과정(Collector.combiner 메서드) 비용을 확인하라
            - 병합 과정 성능이 안좋다면 서브스트림의 부분 결과 합치는 과정에서 상쇄할 수 있다.
    - 포크/조인 프레임워크
        - RecursiveTask 활용
            - 스레드 풀 이용 시 ResursiveTask<R>의 서브클래스 구현
            - ResursiveTask의 추상 메서드 compute 구현

            ```java
            protected abstract R compute();

            // 대부분 compute 메서드 구현 방식
            if (태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
            	순차적으로 태스크 계산
            } else {
            	태스크를 두 서브태스크로 분할
            	태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
            	모든 서브태스크의 연산이 완료될 때까지 기다림
            	각 서브태스크 결과를 합침
            }
            ```

        - 포크/조인 프레임워크를 제대로 사용하는 방법
            - join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 기다린다. 따라서 두 서브태스크가 같이 시작된 다음에 join을 호출해야 한다.
            - RecursiveTask 내에서는 ForkJoinPool의 invoke 메서드를 사용하면 안된다.
                - compute나 fork 메서드를 직접 호출한다.
                - 순차 코드에서 병렬 계산을 시작할 때만 invoke를 사용한다.
            - 서브태스크에 fork 메서드를 호출해서 ForkJoinPool의 일정을 조절할 수 있다.
                - 한쪽 작업에는 fork, 다른쪽 작업에는 compute를 호출하는게 효율적이다.
            - 포크/조인 프레임워크를 이용하는 병렬 작업은 디버깅이 어렵다.
            - 병렬 처리로 성능을 개선하려면 태스크를 여러 독립적인 서브태스크로 분할할 수 있어야 한다.
            - 각 서브태스크의 실행시간은 새로운 태스크를 포킹하는데 드는 시간보다 길어야 한다.
        - 작업 훔치기
            - ForkJoinPool의 모든 스레드를 거의 공정하게 분할한다.
            - 각 스레드는 자신에게 할당된 태스크를 포함하는 이중 연결 리스트를 참조하면서 작업이 끝날 때마다 큐의 헤드에서 다른 태스크를 가져와 작업을 처리한다.
            - **할일이 없어진 스레드는 다른 스레드 큐의 꼬리에서 작업을 훔쳐온다.** 모든 작업에 반복된다.
    - Spliterator 인터페이스
        - 소스의 요소 탐색 기능 제공
        - 병렬 작업에 특화

        ```java
        public interface Spliterator {
        	boolean tryAdvance(Consumer<? super T> action);
        	Spliterator<T> trySplit();
        	long estimateSize();
        	int characteristics();
        }
        ```

        - tryAdvance → Spliterator의 요소를 하나씩 순차적으로 소비하면 탐색해야 할 요소가 남아있으면 true를 반환한다.
        - trySplit → Spliterator의 일부 요소를 분할해서 두번째 Spliterator을 생성한다.
        - estimateSize → 탐색해야 할 요소 수를 반환한다.
        - 분할 과정
            - 스트림을 여러 스트림으로 분할하는 과정은 재귀적으로 일어난다.
            - 첫번째 Spliterator에 trySplit을 호출하면 두번째 Spliterator가 생성된다.
            - trySplit의 결과가 null이 될 때까지 위 작업을 반복한다.
        - 커스텀 Spliterator 구현하기
            - 반복형으로 단어 수를 세는 메서드

            ```java
            final String SENTENCE = "Nel      mezzo del cammin di nostra vita "
                                  + "mi ritrovai in una  selva oscura "
                                  + "ch  la dritta via era  smarrita ";

            public int countWordsIteratively(String s) {
            	int counter = 0;
            	boolean lastSpace = true;
            	for (char c : s.toCharArray()) {
            		if (Character.isWhitespace(c)) {
            			lastSpace = true;
            		} else {
            			if (lastSpace) counter++;
            			lastSpace = false;
            		}
            	}
            	return counter;
            }

            Found 19 words
            ```

            - 함수형으로 단어 수를 세는 메서드 재구현하기

            ```java
            public class WordCounter {
            	private final int counter;
            	private final boolean lastSpace;

            	public WordCounter(int counter, boolean lastSpace) {
            		this.counter = counter;
            		this.lastSpace = lastSpace;
            	}

            	public WordCounter accumulate(Character c) {
            		if (Character.isWhitespace(c)) {
            			return lastSpace ? this : new WordCounter(counter, true);
            		} else {
            			return lastSpace ? new WordCounter(counter+1, false) : this;
            		}
            	}

            	public WordCounter combine(WordCounter wordCounter) {
            		return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
            	}

            	public int getCounter() {
            		return counter;
            	}

            }

            private int countWords(Stream<Character> stream) {
            	WordCounter wordCounter = stream.reduce(
            			new WordCounter(0, true),
            			WordCounter::accumulate,
            			WordCounter::combine);
            	return wordCounter.getCounter();
            }

            Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
            System.out.println(countWords(stream));

            Found 19 words
            ```

            - WordCounter 병렬로 수행하기

            ```java
            public class WordCounterSpliterator implements Spliterator<Character> {

            	private final String string;
            	private int currentChar = 0;

            	public WordCounterSpliterator(String string) {
            		this.string = string;
            	}

            	@Override
            	public boolean tryAdvance(Consumer<? super Character> action) {
            		action.accept(string.charAt(currentChar++));
            		return currentChar < string.length();
            	}

            	@Override
            	public Spliterator<Character> trySplit() {
            		int currentSize = string.length();

            		if (currentSize < 10) {
            			return null;
            		}

            		for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
            			if (Character.isWhitespace(string.charAt(splitPos))) {
            				Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
            				currentChar = splitPos;
            				return spliterator;
            			}
            		}
            		return null;
            	}

            	@Override
            	public long estimateSize() {
            		return string.length() - currentChar;
            	}

            	@Override
            	public int characteristics() {
            		return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
            	}

            }

            Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
            Stream<Character> parallelStream = StreamSupport.stream(spliterator, true);
            System.out.println(countWords(parallelStream));

            Found 19 words
            ```

            - tryAdvance → 문자열에서 현재 인덱스에 해당하는 문자를 Consumer에 제공한 다음 인덱스를 증가한다.
            - trySplit → 반복될 자료구조를 분할하는 로직을 포함한다.
            - estimateSize → Spliterator가 파싱해야 할 문자열 전체 길이와 현재 반복 중인 위치의 차
            - characteristics → 특성을 알려준다.
                - ORDERED → 문자열의 순서대로 사용함
                - SIZED → estimateSize 메서드의 반환값이 정확함
                - SUBSIZED → trySplit으로 생성된 Spliterator도 정확한 크기를 가짐
                - NONNULL → 문자열에는 null 문자가 존재하지 않음
                - IMMUTABLE → 문자열 자체가 불변 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음