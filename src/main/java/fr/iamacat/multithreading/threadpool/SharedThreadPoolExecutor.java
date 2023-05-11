package fr.iamacat.multithreading.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.iamacat.multithreading.config.MultithreadingandtweaksMultithreadingConfig;

public class SharedThreadPoolExecutor {

    private static final int NUM_THREADS = MultithreadingandtweaksMultithreadingConfig.numberofcpus;
    private static ExecutorService executorService;

    public static void main(String[] args) {
        // Create the shared thread pool executor with a named thread factory
        executorService = Executors.newFixedThreadPool(
            NUM_THREADS,
            new ThreadFactoryBuilder().setNameFormat("MyThread-%d")
                .build());

        // Submit tasks to the executor
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                // Perform some task in the background thread
                System.out.println(
                    "Task executed in thread: " + Thread.currentThread()
                        .getName());
            });
        }

        // Shutdown the executor service
        executorService.shutdown();
    }
}
