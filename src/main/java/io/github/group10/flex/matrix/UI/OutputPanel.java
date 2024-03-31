package io.github.group10.flex.matrix.UI;

import io.github.group10.flex.matrix.Model.MultiplicationInput;
import io.github.group10.flex.matrix.Utils.Benchmark;
import io.github.group10.flex.matrix.Utils.Utils;
import io.github.group10.flex.matrix.Algorithm.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ForkJoinPool;

public class OutputPanel extends JPanel {
    private JButton computeNaiveButton, computeDaCButton, computeStrassenButton, clearButton;
    private final MatrixInputPanel matrixA, matrixB;
    private MatrixOutputPanel naiveOutput, dacOutput, strassenOutput;
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
        computeDaCButton = new JButton("Compute DaC");
        computeStrassenButton = new JButton("Compute Strassen");
        clearButton = new JButton("Clear");

        naiveOutput = new MatrixOutputPanel("Naive");
        dacOutput = new MatrixOutputPanel("DaC");
        strassenOutput = new MatrixOutputPanel("Strassen");


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
        buttonPanel.add(computeDaCButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        buttonPanel.add(computeStrassenButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        buttonPanel.add(clearButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        buttonPanel.add(matrixMulInfoLabel, gbc);
        add(buttonPanel, BorderLayout.NORTH);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayout());
        outputPanel.add(naiveOutput);
        outputPanel.add(dacOutput);
        outputPanel.add(strassenOutput);
        add(outputPanel, BorderLayout.CENTER);
    }

    private MultiplicationInput prepareMultiplicationInput() {
        var A = matrixA.getMatrixValues();
        var B = matrixB.getMatrixValues();

        if (A == null)
            return null;
        if (B == null)
            return null;

        return new MultiplicationInput(new Benchmark(), A, B, A.length, A[0].length, B.length, B[0].length);
    }

    private void addListeners() {
        clearButton.addActionListener(e -> {
            naiveOutput.clear();
            dacOutput.clear();
            strassenOutput.clear();
        });

        computeNaiveButton.addActionListener(actionEvent -> {
            var input = prepareMultiplicationInput();
            if (input == null)
                return;

            matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", input.m(), input.n(), input.p(), input.q()));

            int[][] result;
            try {
                var mulMatrix = new Naive(input.A(), input.B());
                input.benchmark().start();
                result = mulMatrix.execute();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                        "Error Calculate Naive", JOptionPane.ERROR_MESSAGE);
                input.benchmark().end();
                return;
            }
            input.benchmark().end();
            naiveOutput.setResult(Utils.matrixToString(result), input.benchmark().getExecutionTime());
        });

        computeDaCButton.addActionListener(actionEvent -> {
            var input = prepareMultiplicationInput();
            if (input == null)
                return;
            int divideThreshold = Math.max(input.m(),Math.max(input.n(),input.q())) / 2;

            matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", input.m(), input.n(), input.p(), input.q()));

            int [][] result;
            try (var pool = new ForkJoinPool()) {
                var mulMatrix = new DaC(divideThreshold, input.A(), input.B(), 0, input.m()-1, 0, input.n()-1, 0, input.p()-1, 0, input.q()-1);
                input.benchmark().start();
                result = pool.invoke(mulMatrix);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                        "Error Calculate Naive", JOptionPane.ERROR_MESSAGE);
                input.benchmark().end();
                return;
            }
            input.benchmark().end();
            dacOutput.setResult(Utils.matrixToString(result), input.benchmark().getExecutionTime());
        });

        computeStrassenButton.addActionListener(actionEvent -> {
            var input = prepareMultiplicationInput();
            if (input == null)
                return;

            matrixMulInfoLabel.setText(String.format("A(%d x %d) * B(%d x %d)\n", input.m(), input.n(), input.p(), input.q()));

            int [][] result;
            try {
                input.benchmark().start();
                result = Strassen.multiply(input.A(), input.B());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(OutputPanel.this, e.getMessage(),
                        "Error Calculate Strassen", JOptionPane.ERROR_MESSAGE);
                input.benchmark().end();
                return;
            }
            input.benchmark().end();
            strassenOutput.setResult(Utils.matrixToString(result), input.benchmark().getExecutionTime());
        });
    }
}
