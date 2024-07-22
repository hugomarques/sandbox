package com.hugodesmarques.threads;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ThreadPoolSemaphoreExample {

    public static void main(String[] args) {
        // Create a thread pool with 4 threads
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        
        // Create a semaphore with 1 permit
        Semaphore semaphore = new Semaphore(1);

        IntStream.range(1, 10).forEach(val -> {
            // Create a CompletableFuture to simulate an asynchronous task
            CompletableFuture<Void> futureTask = CompletableFuture.runAsync(() -> {
                try {
                    // Acquire the semaphore before starting the task
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " acquired the semaphore.");

                    // Simulate task execution
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + " completed the task." + val);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // Ensure the semaphore is released after the task is done
                    System.out.println(Thread.currentThread().getName() + " releasing the semaphore.");
                    semaphore.release();
                }
            }, threadPool);

            // Add a completion stage to the CompletableFuture
            futureTask.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    System.out.println("Task completed successfully.");
                } else {
                    System.out.println("Task completed exceptionally: " + throwable.getMessage());
                }
            });
        });

        // Shutdown the thread pool gracefully after tasks are completed
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}
