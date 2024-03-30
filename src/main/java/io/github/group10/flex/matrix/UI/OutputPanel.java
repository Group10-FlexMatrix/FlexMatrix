package io.github.group10.flex.matrix.UI;

import io.github.group10.flex.matrix.Utils.Benchmark;
import io.github.group10.flex.matrix.Utils.Utils;
import io.github.group10.flex.matrix.Algorithm.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ForkJoinPool;

public class OutputPanel extends JPanel {
    private JButton computeNativeButton, computeParallelButton, clearButton;
    private final MatrixInputPanel matrixA, matrixB;
    private MatrixOutputPanel nativeOutput, parallelOutput;
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
        computeNativeButton = new JButton("Compute Native");
        computeParallelButton = new JButton("Compute Parallel");
        clearButton = new JButton("Clear");

        nativeOutput = new MatrixOutputPanel("Native");
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
        buttonPanel.add(computeNativeButton, gbc);
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
        outputPanel.add(nativeOutput);
        outputPanel.add(parallelOutput);
        add(outputPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nativeOutput.clear();
                parallelOutput.clear();
            }
        });

        computeNativeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                var benchmark = new Benchmark();
                var A = matrixA.getMatrixValues();
                var B = matrixB.getMatrixValues();

                int m = A.length, n = A[0].length;
                int p = B.length, q = B[0].length;
                matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", m, n, p, q));

                int[][] result;
                try {
                    var mulMatrix = new Native(A, B);
                    benchmark.start();
                    result = mulMatrix.execute();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                            "Error Calculate Native", JOptionPane.ERROR_MESSAGE);
                    benchmark.end();
                    return;
                }
                benchmark.end();
                nativeOutput.setResult(Utils.matrixToString(result), benchmark.getExecutionTime());
            }
        });

        computeParallelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                var benchmark = new Benchmark();
                int [][] A = matrixA.getMatrixValues();
                int [][] B = matrixB.getMatrixValues();

                int m = A.length, n = A[0].length;
                int p = B.length, q = B[0].length;
                int divideThreshold = Math.max(m,Math.max(n,q)) / 2;

                matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", m, n, p, q));

                int [][] result;
                try {
                    ForkJoinPool pool = new ForkJoinPool();
                    var mulMatrix = new DaC(divideThreshold, A, B, 0, m-1, 0, n-1, 0, p-1, 0, q-1);
                    benchmark.start();
                    result = pool.invoke(mulMatrix);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                            "Error Calculate Native", JOptionPane.ERROR_MESSAGE);
                    benchmark.end();
                    return;
                }
                benchmark.end();
                parallelOutput.setResult(Utils.matrixToString(result), benchmark.getExecutionTime());
            }
        });
    }
}
