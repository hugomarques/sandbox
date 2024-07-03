package com.hugodesmarques.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SequentialVSParallelExample {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // Usando ParallelStream para processar uma pequena quantidade de registros
        List<Integer> squaredNumbers = numbers.parallelStream()
                .map(n -> n * n)
                .collect(Collectors.toList());

        System.out.println("Squared Numbers Parallel: " + squaredNumbers);
    }
}
