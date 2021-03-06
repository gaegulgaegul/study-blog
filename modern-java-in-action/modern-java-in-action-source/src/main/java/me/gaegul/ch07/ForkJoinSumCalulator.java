package me.gaegul.ch07;

import java.util.concurrent.RecursiveTask;

public class ForkJoinSumCalulator extends RecursiveTask<Long> {

	private final long[] numbers;
	private final int start;
	private final int end;

	public static final long THRESHOLD = 10_000;

	private ForkJoinSumCalulator(long[] numbers, int start, int end) {
		this.numbers = numbers;
		this.start = start;
		this.end = end;
	}

	public ForkJoinSumCalulator(long[] numbers) {
		this(numbers, 0, numbers.length);
	}

	@Override
	protected Long compute() {
		int length = end - start;
		if (length <= THRESHOLD) {
			return computeSequentially();
		}

		ForkJoinSumCalulator leftTask = new ForkJoinSumCalulator(numbers, start, start + length/2);
		leftTask.fork();

		ForkJoinSumCalulator rightTask = new ForkJoinSumCalulator(numbers, start + length/2, end);
		Long rightResult = rightTask.compute();
		Long leftResult = leftTask.join();

		return rightResult + leftResult;
	}

	private long computeSequentially() {
		long sum = 0;
		for (int i = start; i < end; i++) {
			sum += numbers[i];
		}
		return sum;
	}

}
