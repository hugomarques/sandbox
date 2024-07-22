package com.hugodesmarques.lazylambda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LazyLambdaExample {
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // Execução do Stream fora do Supplier
        System.out.println("Execução do Stream fora do Supplier");
        List<Integer> doubledNumbers =
                numbers.stream()
                        .map(n -> {
                            System.out.println("Dobro de: " + n);
                            return n * 2;
                        })
                        .collect(Collectors.toList());

        writeToFileWithLock("output.txt", () -> doubledNumbers);
        System.out.println("Doubled Numbers: " + doubledNumbers);
    }

    private static void writeToFileWithLock(String filePath, Supplier<List<Integer>> supplier) {
        lock.lock();
        System.out.println("Lock adquirido!");
        try {
            List<Integer> result = supplier.get();
            String content = result.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            Files.write(Paths.get(filePath), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Resultado escrito no arquivo: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("Lock released!");
        }
    }
}