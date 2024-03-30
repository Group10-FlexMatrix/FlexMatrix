package io.github.group10.flex.matrix.Utils;

public class Benchmark {
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void end() {
        endTime = System.nanoTime();
    }

    public double getExecutionTime() {
        return (endTime - startTime) / 1e9;
    }
}

