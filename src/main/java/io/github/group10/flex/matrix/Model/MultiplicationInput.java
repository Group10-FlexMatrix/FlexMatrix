package io.github.group10.flex.matrix.Model;

import io.github.group10.flex.matrix.Utils.Benchmark;

public record MultiplicationInput(
    Benchmark benchmark,
    int [][] A,
    int [][] B,
    int m, int n,
    int p, int q
) { }
