package io.github.group10.flex.matrix.UI;

import io.github.group10.flex.matrix.Utils.Benchmark;
import io.github.group10.flex.matrix.Utils.Utils;
import io.github.group10.flex.matrix.Algorithm.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ForkJoinPool;

public class OutputPanel extends JPanel {
    private JButton computeNaiveButton, computeParallelButton, clearButton;
    private final MatrixInputPanel matrixA, matrixB;
    private MatrixOutputPanel naiveOutput, parallelOutput;
    private JLabel matrixMulInfoLabel;

    public OutputPanel(MatrixInputPanel matrixA, MatrixInputPanel matrixB) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Output"));

        createComponents();
        layoutComponents();
        addListeners();
    }

    private void createComponents() {
        computeNaiveButton = new JButton("Compute Naive");
        computeParallelButton = new JButton("Compute Parallel");
        clearButton = new JButton("Clear");

        naiveOutput = new MatrixOutputPanel("Naive");
        parallelOutput = new MatrixOutputPanel("Parallel");

        matrixMulInfoLabel = new JLabel();
        matrixMulInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void layoutComponents() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(computeNaiveButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(computeParallelButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        buttonPanel.add(clearButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        buttonPanel.add(matrixMulInfoLabel, gbc);
        add(buttonPanel, BorderLayout.NORTH);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayout());
        outputPanel.add(naiveOutput);
        outputPanel.add(parallelOutput);
        add(outputPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        clearButton.addActionListener(e -> {
            naiveOutput.clear();
            parallelOutput.clear();
        });

        computeNaiveButton.addActionListener(actionEvent -> {
            var benchmark = new Benchmark();
            var A = matrixA.getMatrixValues();
            var B = matrixB.getMatrixValues();
            if (A == null)
                return;
            if (B == null)
                return;

            int m = A.length, n = A[0].length;
            int p = B.length, q = B[0].length;
            matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", m, n, p, q));

            int[][] result;
            try {
                var mulMatrix = new Naive(A, B);
                benchmark.start();
                result = mulMatrix.execute();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                        "Error Calculate Naive", JOptionPane.ERROR_MESSAGE);
                benchmark.end();
                return;
            }
            benchmark.end();
            naiveOutput.setResult(Utils.matrixToString(result), benchmark.getExecutionTime());
        });

        computeParallelButton.addActionListener(actionEvent -> {
            var benchmark = new Benchmark();
            int [][] A = matrixA.getMatrixValues();
            int [][] B = matrixB.getMatrixValues();

            if (A == null)
                return;
            if (B == null)
                return;

            int m = A.length, n = A[0].length;
            int p = B.length, q = B[0].length;
            int divideThreshold = Math.max(m,Math.max(n,q)) / 2;

            matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", m, n, p, q));

            int [][] result;
            try (var pool = new ForkJoinPool()) {
                var mulMatrix = new DaC(divideThreshold, A, B, 0, m-1, 0, n-1, 0, p-1, 0, q-1);
                benchmark.start();
                result = pool.invoke(mulMatrix);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                        "Error Calculate Naive", JOptionPane.ERROR_MESSAGE);
                benchmark.end();
                return;
            }
            benchmark.end();
            parallelOutput.setResult(Utils.matrixToString(result), benchmark.getExecutionTime());
        });
    }
}
