package com.hugodesmarques.parallel;

import java.util.Arrays;
import java.util.List;

public class SequentialStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Criando um stream sequencial
        numbers.stream()
                .forEach(n -> System.out.println("Thread: " + Thread.currentThread().getName() + " - Número: " + n));
    }
}