package com.hugodesmarques.threads;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolWithLimitsExample {

    private static final int TOTAL_TASKS = 1_000_000;
    private static final int THREAD_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;

    private final ExecutorService mainExecutor = new ThreadPoolExecutor(
            THREAD_POOL_SIZE,
            THREAD_POOL_SIZE,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new NamedThreadFactory("Producer"), // ThreadFactory com expressão lambda)
            new RejectionLoggingPolicy(new ThreadPoolExecutor.CallerRunsPolicy()) // Política de rejeição com log
    );

    public static void main(String[] args) {
        ThreadPoolWithLimitsExample example = new ThreadPoolWithLimitsExample();
        example.run();
    }

    public void run() {
        for (int i = 0; i < TOTAL_TASKS; i++) {
            mainExecutor.submit(this::submitRequest);
        }
    }

    private void submitRequest() {
        // Simula o envio de uma requisição para o cliente gRPC
        CompletableFuture<Response> future = asyncGrpcCall();

        // Processa a resposta usando o mesmo executor
        future.thenApplyAsync(this::processResponse, mainExecutor);
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
        int queueSize = ((ThreadPoolExecutor) mainExecutor).getQueue().size();
        System.out.println("Current queue size: " + queueSize);
        printHeapSize();
        return future;
    }

    private Response processResponse(Response response) {
        // Processa a resposta do cliente gRPC
        System.out.println("Processando resposta... thread: " + Thread.currentThread().getName());
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

    // Política de rejeição personalizada que registra um log quando uma tarefa é rejeitada
    static class RejectionLoggingPolicy implements RejectedExecutionHandler {
        private final RejectedExecutionHandler handler;

        public RejectionLoggingPolicy(RejectedExecutionHandler handler) {
            this.handler = handler;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("Tarefa rejeitada: " + r.toString() + " thread: " + Thread.currentThread().getName());
            handler.rejectedExecution(r, executor);
        }
    }

    // ThreadFactory personalizado para nomear threads
    static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + "-thread-" + threadNumber.getAndIncrement());
        }
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