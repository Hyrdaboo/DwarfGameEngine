package DwarfEngine;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
    private static final int numThreads = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    private static final Future[] futures = new Future[numThreads];

    public static void executeInParallel(int numItems, Task task) {
        int numThreads = Math.min(ThreadPool.numThreads, numItems);
        for (int i = 0; i < numThreads; i++) {
            final long j = i;
            futures[i] = executor.submit(() -> {
                int i0 = (int) (j * numItems / numThreads);
                int i1 = (int) ((j + 1) * numItems / numThreads);
                task.run(i0, i1);
            });
        }
        try {
            for (int i = 0; i < numThreads; i++) {
                futures[i].get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static abstract class Task {
        public abstract void run(int i0, int i1);
    }
}
