package com.hugodesmarques.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class WorkStealingExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Configurando o ForkJoinPool para usar um número específico de threads
        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        try {
            customThreadPool.submit(() ->
                    numbers.parallelStream()
                            .forEach(n -> {
                                try {
                                    if (n % 2 == 0) {
                                        // Simulando uma tarefa que leva tempo
                                        Thread.sleep(2000);
                                    } else {
                                        Thread.sleep(100);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Thread: " + Thread.currentThread().getName() + " - Número: " + n);
                            })
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customThreadPool.shutdown();
        }
    }
}