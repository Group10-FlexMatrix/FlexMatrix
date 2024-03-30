package io.github.group10.flex.matrix.Algorithm;

import java.util.concurrent.RecursiveTask;

public class DaC extends RecursiveTask<int[][]> {
    private final int[][] A, B;
    private final int A_m, A_n, B_p, B_q;
    private final int A_rowL, A_rowR, A_colL, A_colR, B_rowL, B_rowR, B_colL, B_colR;
    private final int THRESHOLD;

    public DaC(int thresh, int[][] A, int[][] B, int A_rowL, int A_rowR, int A_colL, int A_colR, int B_rowL, int B_rowR, int B_colL, int B_colR) {
        this.THRESHOLD = thresh;

        this.A = A;
        this.B = B;

        this.A_m = (A_rowR - A_rowL) + 1;
        this.A_n = (A_colR - A_colL) + 1;
        this.B_p = (B_rowR - B_rowL) + 1;
        this.B_q = (B_colR - B_colL) + 1;

        this.A_rowL = A_rowL;
        this.A_rowR = A_rowR;
        this.A_colL = A_colL;
        this.A_colR = A_colR;

        this.B_rowL = B_rowL;
        this.B_rowR = B_rowR;
        this.B_colL = B_colL;
        this.B_colR = B_colR;
    }

    @Override
    protected int[][] compute() {
        if (A_m * A_n <= 0 || B_p * B_q <= 0)
            return null;

        int max = Math.max(A_m, Math.max(A_n, B_q));

        if (max <= THRESHOLD) {
            try {
                return matrixMul();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /* Split A horizontally */
        if (max == A_m)
        {
            int A_midRow = (A_rowR - A_rowL) / 2;

            var mul1 = new DaC(THRESHOLD, A, B, A_rowL, A_midRow, A_colL, A_colR,
                                         B_rowL, B_rowR, B_colL, B_colR);
            mul1.fork();

            var mul2 = new DaC(THRESHOLD, A, B, A_midRow + 1, A_rowR, A_colL, A_colR,
                                         B_rowL, B_rowR, B_colL, B_colR);
            mul2.fork();

            try {
                return concatVertical(mul1.join(), mul2.join());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        /* Split B vertically */
        else if (max == B_q){
            int B_midCol = (B_colR - B_colL) / 2;

            var mul1 = new DaC(THRESHOLD, A, B, A_rowL, A_rowR, A_colL, A_colR,
                                         B_rowL, B_rowR, B_colL, B_midCol);
            mul1.fork();

            var mul2 = new DaC(THRESHOLD, A, B, A_rowL, A_rowR, A_colL, A_colR,
                                         B_rowL, B_rowR, B_midCol+1, B_colR);
            mul2.fork();

            try {
                return concatHorizontal(mul1.join(), mul2.join());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        /* Split A vertically and B horizontally */
        else
        {
            int A_midCol = (A_colR - A_colL) / 2;
            int B_midRow = (B_rowR - B_rowL) / 2;

            var mul1 = new DaC(THRESHOLD, A, B, A_rowL, A_rowR, A_colL, A_midCol,
                                         B_rowL, B_midRow, B_colL, B_colR);
            mul1.fork();

            var mul2 = new DaC(THRESHOLD, A, B, A_rowL, A_rowR, A_midCol + 1, A_colR,
                                  B_midRow+1, B_rowR, B_colL, B_colR);
            mul2.fork();

            try {
                return matrixSum(mul1.join(), mul2.join());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int[][] matrixMul() throws Exception {
        if (A_m * A_n <= 0 || B_p * B_q <= 0)
            return null;

        if (A_n != B_p) throw new Exception("Invalid shape to perform multiplication");

        int[][] C = new int[A_m][B_q];
        for (int row = A_rowL; row <= A_rowR; ++row)
            for (int col = B_colL; col <= B_colR; ++col) {
                C[row-A_rowL][col-B_colL] = 0;
                for (int idx = A_colL; idx <= A_colR; ++idx)
                    C[row-A_rowL][col-B_colL] += A[row][idx] * B[idx][col];
            }

        return C;
    }

    private static int[][] matrixSum(int[][] A, int[][] B) throws Exception {
        if (A == null || B == null)
            return null;

        int m = A.length, n = A[0].length;
        int p = B.length, q = B[0].length;

        if (m * n != p * q) throw new Exception("Invalid shape to perform summation");

        int[][] C = new int[m][n];
        for (int row = 0; row < m; ++row)
            for (int col = 0; col < q; ++col)
                C[row][col] = A[row][col] + B[row][col];

        return C;
    }

    private static int[][] concatHorizontal(int[][] A, int[][] B) throws Exception {
        if (A == null || B == null)
            return null;

        int m = A.length, n = A[0].length;
        int p = B.length, q = B[0].length;

        if (m != p) throw new Exception("Invalid shape to perform horizontal concatenation");

        int[][] C = new int[m][n+q];
        for(int row = 0; row < m; ++row)
            System.arraycopy(A[row], 0, C[row], 0, n);

        for(int row = 0; row < m; ++row)
            System.arraycopy(B[row], 0, C[row], n, n + q - n);

        return C;
    }

    private static int[][] concatVertical(int[][] A, int[][] B) throws Exception {
        if (A == null || B == null)
            return null;

        int m = A.length, n = A[0].length;
        int p = B.length, q = B[0].length;

        if (n != q) throw new Exception("Invalid shape to perform vertical concatenation");

        int[][] C = new int[m+p][n];
        for(int row = 0; row < m; ++row)
            System.arraycopy(A[row], 0, C[row], 0, n);

        for(int row = m; row < m+p; ++row)
            System.arraycopy(B[row - m], 0, C[row], 0, n);

        return C;
    }
}
