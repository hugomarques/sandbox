package com.hugodesmarques.threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ThreadPoolsWithSemaphores {
    private static final int THREAD_POOL_SIZE = 10;
    private static final int TOTAL_TASKS = 1_000_000;
    private static final int MAX_CONCURRENT_REQUESTS = 100;
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_REQUESTS);

    private final ExecutorService mainExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) {
        ThreadPoolsWithSemaphores example = new ThreadPoolsWithSemaphores();
        example.run();
    }

    public void run() {
        for (int i = 0; i < TOTAL_TASKS; i++) {
            try {
                System.out.println("Submetendo request: " + i);
                semaphore.acquire();
                mainExecutor.submit(this::submitRequest);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void submitRequest() {
        try {
            // Simula o envio de uma requisição para o cliente gRPC
            CompletableFuture<Response> future = asyncGrpcCall();

            // Processa a resposta usando o mesmo executor
            future.thenApplyAsync(this::processResponse, mainExecutor)
                    .whenComplete((result, ex) -> semaphore.release());
        } catch (Exception e) {
            semaphore.release();
        }
    }

    private CompletableFuture<Response> asyncGrpcCall() {
        // Simula uma chamada gRPC assíncrona
        CompletableFuture<Response> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(100); // Simula o atraso da rede
                future.complete(new Response(512));
            } catch (InterruptedException e) {
                future.completeExceptionally(e);
            }
        }).start();
        printHeapSize();
        return future;
    }

    private Response processResponse(Response response) {
        // Processa a resposta do cliente gRPC
        System.out.println("Processando resposta");
        return response;
    }

    private void printHeapSize() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        System.out.println("Heap size (total): " + totalMemory / (1024 * 1024) + " MB");
        System.out.println("Heap size (used): " + usedMemory / (1024 * 1024) + " MB");
        System.out.println("Heap size (max): " + maxMemory / (1024 * 1024) + " MB");
    }

    public static class Response {
        private byte[] data;

        public Response(int sizeInKB) {
            this.data = new byte[sizeInKB * 1024]; // 1 KB = 1024 bytes
        }

        public byte[] getData() {
            return data;
        }
    }
}