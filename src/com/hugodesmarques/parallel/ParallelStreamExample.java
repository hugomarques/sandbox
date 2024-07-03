package com.hugodesmarques.parallel;

import java.util.Arrays;
import java.util.List;

public class ParallelStreamExample {
    public static void main(String[] args) {

        System.out.println("Number of cores available: "+ Runtime.getRuntime().availableProcessors());

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Criando um stream paralelo
        numbers.parallelStream()
                .forEach(n -> System.out.println("Thread: " + Thread.currentThread().getName() + " - Número: " + n));
    }
}