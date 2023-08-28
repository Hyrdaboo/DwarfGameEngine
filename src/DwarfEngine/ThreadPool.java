package DwarfEngine;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
	private static final int numThreads = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
	private static final ThreadLocal<ArrayList<Future<?>>> futures = ThreadLocal.withInitial(ArrayList::new);

	public static void executeInParallel(int numItems, Task task) {
		ArrayList<Future<?>> futures = ThreadPool.futures.get();
		int numTasks = numItems / 8;
		int size0 = futures.size();
		futures.ensureCapacity(size0 + numTasks);
		for (int i = 0; i < numTasks; i++) {
			final long j = i;
			futures.add(executor.submit(() -> {
				int i0 = (int) (j * (long) numItems / numTasks);
				int i1 = (int) ((j + 1) * (long) numItems / numTasks);
				for (int k = i0; k < i1; k++) {
					task.run(k);
				}
			}));
		}
		try {
			for (int i = numTasks - 1; i >= 0; i--) {
				futures.remove(size0 + i).get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static abstract class Task {
		public abstract void run(int i);
	}
}
