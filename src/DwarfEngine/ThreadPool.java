package DwarfEngine;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
	private static final int numThreads = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
	private static final ArrayList<Future> futures = new ArrayList<>();

	public static void executeInParallel(int numItems, Task task) {
		futures.ensureCapacity(numItems);
		for (int i = 0; i < numItems; i++) {
			final long j = i;
			futures.add(executor.submit(() -> task.run((int) (j * numItems / numItems))));
		}
		try {
			for (int i = 0; i < numItems; i++) {
				futures.get(i).get();
			}
			futures.clear();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static abstract class Task {
		public abstract void run(int i);
	}
}
