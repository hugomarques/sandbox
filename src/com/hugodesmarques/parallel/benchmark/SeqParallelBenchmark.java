package com.hugodesmarques.parallel.benchmark;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 2)
public class SeqParallelBenchmark {

    @Param({"100", "1000000"})
    private int size;
    private List<Integer> data;

    @Setup
    public void setup() {
        data = IntStream.rangeClosed(1, size).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public void test_sequential() {
        data.stream().mapToInt(Integer::intValue).sum();

    }

    @Benchmark
    public void test_parallel() {
        data.stream().parallel().mapToInt(Integer::intValue).sum();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SeqParallelBenchmark.class.getName()) // specify the benchmark class here
                .forks(2)
                .build();
        new Runner(opt).run();
    }
}
