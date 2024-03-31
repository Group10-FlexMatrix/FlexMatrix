package io.github.group10.flex.matrix.Utils;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {
    public static int[][] randomMatrixParallel(int rows, int cols, int min, int max) {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];

        IntStream.range(0, rows).parallel().forEach(i -> {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(max - min) + min;
            }
        });
        return matrix;
    }

    public static String matrixToString(int[][] matrix) {
        return Arrays.stream(matrix)
                .parallel()
                .map(row -> Arrays.stream(row).mapToObj(String::valueOf).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }

    public static int[][] parseMatrix(String matrixStr) {
        return Arrays.stream(matrixStr.split("\n"))
                .parallel() // Process rows in parallel
                .map(rowStr -> Arrays.stream(rowStr.trim().split("\\s+"))
                        .mapToInt(Integer::parseInt)
                        .toArray())
                .toArray(int[][]::new);
    }
}
