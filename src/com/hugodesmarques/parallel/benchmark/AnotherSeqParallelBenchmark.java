package com.hugodesmarques.parallel.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class AnotherSeqParallelBenchmark {

    @Param({"100", "1000000"})
    private int size;

    private List<Integer> data;

    @Setup
    public void setup() {
        data = IntStream.rangeClosed(1, size).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public List<Double> testSequentialStream() {
        return data.stream()
                .map(Math::sin)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Double> testParallelStream() {
        return data.parallelStream()
                .map(Math::sin)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AnotherSeqParallelBenchmark.class.getName()) // specify the benchmark class here
                .forks(2)
                .build();

        new Runner(opt).run();
    }
}
