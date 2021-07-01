package me.gaegul.ch07;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Main {

	public static void main(String[] args) {

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 포크/조인 프레임워크");

		System.out.println(forkJoinSum(20));

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- Spliterator 인터페이스");

		final String SENTENCE = "Nel      mezzo del cammin di nostra vita "
				+ "mi ritrovai in una  selva oscura "
				+ "ch  la dritta via era  smarrita ";

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 반복형으로 단어 수를 세는 메서드");

		System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- 함수형으로 단어 수를 세는 메서드 재구현하기");

		Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
		System.out.println("Found " + countWords(stream) + " words");

		System.out.println("----------------------------------------------------------------------");
		System.out.println("-- WordCounter 병렬로 수행하기");

		WordCounterSpliterator spliterator = new WordCounterSpliterator(SENTENCE);
		Stream<Character> parallelStream = StreamSupport.stream(spliterator, true);
		System.out.println("Found " + countWords(parallelStream) + " words");

		System.out.println("----------------------------------------------------------------------");

	}

	public static long forkJoinSum(long n) {
		long[] numbers = LongStream.rangeClosed(1, n).toArray();
		ForkJoinTask<Long> task = new ForkJoinSumCalulator(numbers);
		return new ForkJoinPool().invoke(task);
	}

	public static int countWordsIteratively(String s) {
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

	private static int countWords(Stream<Character> stream) {
		WordCounter wordCounter = stream.reduce(
				new WordCounter(0, true),
				WordCounter::acculate,
				WordCounter::combine);
		return wordCounter.getCounter();
	}
}
