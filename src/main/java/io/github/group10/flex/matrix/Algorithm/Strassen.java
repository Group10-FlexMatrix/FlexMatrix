package io.github.group10.flex.matrix.Algorithm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Strassen {
    public static int[][] multiply(int[][] a, int[][] b, int rows_A, int cols_A, int rows_B, int cols_B) {
        int newSize = Math.max(Math.max(rows_A, cols_A), Math.max(rows_B, cols_B));
        newSize = (int) Math.pow(2, Math.ceil(Math.log(newSize) / Math.log(2)));

        int[][] paddedA = padMatrix(a, newSize);
        int[][] paddedB = padMatrix(b, newSize);

        int paddedSize = Math.max(Math.max(paddedA.length, paddedA[0].length), Math.max(paddedB.length, paddedB[0].length));
        paddedSize = (int) Math.pow(2, Math.ceil(Math.log(paddedSize) / Math.log(2)));
        int[][] result = new int[paddedSize][paddedSize];

        strassenMultiply(paddedA, paddedB, result, 0, 0);

        return result;
    }

    private static void strassenMultiply(int[][] a, int[][] b, int[][] result, int i, int j) {
        int paddedSize = a.length;

        if (paddedSize <= 64) {
            standardMultiply(a, b, result, i, j);
        } else {
            int halfSize = paddedSize / 2;
            int[][] a11 = subMatrix(a, 0, 0, halfSize, halfSize);
            int[][] a12 = subMatrix(a, 0, halfSize, halfSize, paddedSize);
            int[][] a21 = subMatrix(a, halfSize, 0, paddedSize, halfSize);
            int[][] a22 = subMatrix(a, halfSize, halfSize, paddedSize, paddedSize);

            int[][] b11 = subMatrix(b, 0, 0, halfSize, halfSize);
            int[][] b12 = subMatrix(b, 0, halfSize, halfSize, paddedSize);
            int[][] b21 = subMatrix(b, halfSize, 0, paddedSize, halfSize);
            int[][] b22 = subMatrix(b, halfSize, halfSize, paddedSize, paddedSize);

            /*try (var executor = Executors.newFixedThreadPool(7)) {
                executor.execute(() -> strassenMultiply(addMatrices(a11, a22), addMatrices(b11, b22), result, i, j));
                executor.execute(() -> strassenMultiply(addMatrices(a21, a22), b11, result, i + halfSize, j));
                executor.execute(() -> strassenMultiply(a11, subtractMatrices(b12, b22), result, i, j + halfSize));
                executor.execute(() -> strassenMultiply(a22, subtractMatrices(b21, b11), result, i + halfSize, j + halfSize));
                executor.execute(() -> strassenMultiply(addMatrices(a11, a12), b22, result, i, j));
                executor.execute(() -> strassenMultiply(subtractMatrices(a21, a11), addMatrices(b11, b12), result, i + halfSize, j));
                executor.execute(() -> strassenMultiply(subtractMatrices(a12, a22), addMatrices(b21, b22), result, i, j));
                executor.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

            ExecutorService executor = Executors.newFixedThreadPool(7);

            executor.execute(() -> strassenMultiply(addMatrices(a11, a22), addMatrices(b11, b22), result, i, j));
            executor.execute(() -> strassenMultiply(addMatrices(a21, a22), b11, result, i + halfSize, j));
            executor.execute(() -> strassenMultiply(a11, subtractMatrices(b12, b22), result, i, j + halfSize));
            executor.execute(() -> strassenMultiply(a22, subtractMatrices(b21, b11), result, i + halfSize, j + halfSize));
            executor.execute(() -> strassenMultiply(addMatrices(a11, a12), b22, result, i, j));
            executor.execute(() -> strassenMultiply(subtractMatrices(a21, a11), addMatrices(b11, b12), result, i + halfSize, j));
            executor.execute(() -> strassenMultiply(subtractMatrices(a12, a22), addMatrices(b21, b22), result, i, j));

            executor.shutdown();
            while (!executor.isTerminated()) {}
        }
    }

    private static void standardMultiply(int[][] a, int[][] b, int[][] result, int i, int j) {
        int m = a.length;
        int n = b[0].length;
        int o = a[0].length;
        for (int x = 0; x < m; x++) {
            for (int y = 0; y < n; y++) {
                for (int z = 0; z < o; z++) {
                    result[i + x][j + y] += a[x][z] * b[z][y];
                }
            }
        }
    }

    private static int[][] subMatrix(int[][] matrix, int startRow, int startCol, int endRow, int endCol) {
        int[][] sub = new int[endRow - startRow][endCol - startCol];
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol; j++) {
                sub[i - startRow][j - startCol] = matrix[i][j];
            }
        }
        return sub;
    }

    private static int[][] addMatrices(int[][] a, int[][] b) {
        int rows = a.length;
        int cols = a[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    private static int[][] subtractMatrices(int[][] a, int[][] b) {
        int rows = a.length;
        int cols = a[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
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
