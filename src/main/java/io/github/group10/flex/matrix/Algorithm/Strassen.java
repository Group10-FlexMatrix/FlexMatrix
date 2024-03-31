package io.github.group10.flex.matrix.Algorithm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Strassen {
    public static int[][] multiply(int[][] A, int[][] B) {
        int n = A.length;

        if (n == 1) {
            int[][] C = new int[1][1];
            C[0][0] = A[0][0] * B[0][0];
            return C;
        } else {
            int newSize = n / 2;
            int[][] a11 = new int[newSize][newSize];
            int[][] a12 = new int[newSize][newSize];
            int[][] a21 = new int[newSize][newSize];
            int[][] a22 = new int[newSize][newSize];

            int[][] b11 = new int[newSize][newSize];
            int[][] b12 = new int[newSize][newSize];
            int[][] b21 = new int[newSize][newSize];
            int[][] b22 = new int[newSize][newSize];

            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    a11[i][j] = A[i][j];
                    a12[i][j] = A[i][j + newSize];
                    a21[i][j] = A[i + newSize][j];
                    a22[i][j] = A[i + newSize][j + newSize];

                    b11[i][j] = B[i][j];
                    b12[i][j] = B[i][j + newSize];
                    b21[i][j] = B[i + newSize][j];
                    b22[i][j] = B[i + newSize][j + newSize];
                }
            }

            ExecutorService executor = Executors.newFixedThreadPool(8);
            int[][][] results = new int[7][][];

            executor.submit(() -> results[0] = multiply(add(a11, a22), add(b11, b22)));
            executor.submit(() -> results[1] = multiply(add(a21, a22), b11));
            executor.submit(() -> results[2] = multiply(a11, sub(b12, b22)));
            executor.submit(() -> results[3] = multiply(a22, sub(b21, b11)));
            executor.submit(() -> results[4] = multiply(add(a11, a12), b22));
            executor.submit(() -> results[5] = multiply(sub(a21, a11), add(b11, b12)));
            executor.submit(() -> results[6] = multiply(sub(a12, a22), add(b21, b22)));

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int[][] c11 = add(sub(add(results[0], results[3]), results[4]), results[6]);
            int[][] c12 = add(results[2], results[4]);
            int[][] c21 = add(results[1], results[3]);
            int[][] c22 = add(sub(add(results[0], results[2]), results[1]), results[5]);

            // Combine sub-matrices to form the result
            int[][] C = new int[n][n];
            for (int i = 0; i < newSize; i++) {
                for (int j = 0; j < newSize; j++) {
                    C[i][j] = c11[i][j];
                    C[i][j + newSize] = c12[i][j];
                    C[i + newSize][j] = c21[i][j];
                    C[i + newSize][j + newSize] = c22[i][j];
                }
            }

            return C;
        }
    }

    static int[][] add(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    static int[][] sub(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    public static int[][] padMatrix(int[][] matrix, int newSize) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] paddedMatrix = new int[newSize][newSize];

        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                if (i < cols && j < rows) {
                    paddedMatrix[j][i] = matrix[j][i];
                } else {
                    paddedMatrix[j][i] = 0;
                }
            }
        }

        return paddedMatrix;
    }
}
