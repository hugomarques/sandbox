package com.hugodesmarques.parallel.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SeqParallelBenchmark.class.getSimpleName()) // specify the benchmark class here
                .forks(2)
                .build();
        new Runner(opt).run();
    }
}
