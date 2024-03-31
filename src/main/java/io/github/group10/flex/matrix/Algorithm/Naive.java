package io.github.group10.flex.matrix.Algorithm;

public class Naive {
    private final int[][] A, B;

    public Naive(int[][] A, int[][] B) {
        this.A = A;
        this.B = B;
    }

    public int[][] execute() throws Exception {
        if (A == null || B == null)
            return null;

        int m = A.length, n = A[0].length;
        int p = B.length, q = B[0].length;

        if (n != p) throw new Exception("Invalid shape to perform multiplication");

        int[][] C = new int[m][q];
        for (int row = 0; row < m; ++row)
            for (int col = 0; col < q; ++col) {
                C[row][col] = 0;
                for (int idx = 0; idx < n; ++idx)
                    C[row][col] += A[row][idx] * B[idx][col];
            }

        return C;
    }
}
