import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

class MatrixMultiplication {
    static private void print(int[][] A) {
        if (A == null) {
            System.out.print("Empty matrix!");
            return;
        }

        int n = A[0].length;
        for (int[] elm : A) {
            for (int j = 0; j < n; ++j)
                System.out.printf("%d ", elm[j]);
            System.out.println();
        }
        System.out.println();
    }
    static private int[][] arrayInit(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));

        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

        scanner.close();

        int m = lines.size();
        int n = lines.getFirst().split(",").length;
        int[][] A = new int[m][n];

        for (int i = 0; i < m; ++i) {
            String[] parts = lines.get(i).split(",");
            for (int j = 0; j < n; ++j) {
                A[i][j] = Integer.parseInt(parts[j]);
            }
        }

        return A;
    }
    public static void main(String[] args) throws Exception {
        int[][] A = arrayInit("D:\\UIT Learn\\Network application programing\\MatrixMultiplication\\MatrixMultiplication\\src\\main\\Input\\A.csv");
        int[][] B = arrayInit("D:\\UIT Learn\\Network application programing\\MatrixMultiplication\\MatrixMultiplication\\src\\main\\Input\\B.csv");

        int m = A.length, n = A[0].length;
        int p = B.length, q = B[0].length;

        System.out.printf("A(%d x %d) * B(%d x %d)\n", m, n, p, q);
//        var C = new Naive_Algorithm(A, B);
//        var result = C.execute();
//        print(result);


        int divideThreshold = Math.max(m,Math.max(n,q)) / 2;
        System.out.printf("Divide threshold: %d", divideThreshold);

        ForkJoinPool pool = new ForkJoinPool();
        var D = new DaC_Algorithm(divideThreshold, A, B, 0, m-1, 0, n-1, 0, p-1, 0, q-1);
        var result = pool.invoke(D);
//        print(result);
    }
}