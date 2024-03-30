package io.github.group10.flex.matrix.UI;

import io.github.group10.flex.matrix.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MatrixInputPanel extends JPanel {
    private final String matrixName;
    private JTextArea matrixInputArea;
    private JScrollPane scrollPane;
    private JButton randomButton, clearButton, importFromFileButton, parseMatrixButton, setSizeButton;
    private int[][] matrixValues;
    private int rows = 0, cols = 0;
    private boolean hasSetSize = false;

    public MatrixInputPanel(String matrixName) {
        this.matrixName = matrixName;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Matrix " + matrixName));

        createComponents();
        layoutComponents();
        addListeners();
    }

    private void createComponents() {
        matrixInputArea = new JTextArea(40, 40);
        matrixInputArea.setText("Please input with follow schema:\nColumn separated by space, rows separated by new line.");
        matrixInputArea.setFont(new Font("Noto Sans", Font.PLAIN, 15));
        scrollPane = new JScrollPane(matrixInputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setSizeButton = new JButton("Set Size");
        randomButton = new JButton("Generate Random");
        importFromFileButton = new JButton("Import from File");
        parseMatrixButton = new JButton("Parse Matrix");
        clearButton = new JButton("Clear");
    }

    private void layoutComponents() {
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(randomButton);
        buttonPanel.add(setSizeButton);
        buttonPanel.add(importFromFileButton);
        buttonPanel.add(parseMatrixButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.NORTH);
    }

    private void addListeners() {
        setSizeButton.addActionListener(e -> inputMatrixSize());


        randomButton.addActionListener(e -> {
            if (!hasSetSize) {
                if (inputMatrixSize() == -1) return;
            }
            matrixValues = Utils.randomMatrixParallel(rows, cols, 0, 9);
            matrixInputArea.setText(Utils.matrixToString(matrixValues));
        });

        clearButton.addActionListener(e -> {
            matrixInputArea.setText("");
            matrixValues = null;
        });

        importFromFileButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(MatrixInputPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    matrixInputArea.setText(Files.readString(file.toPath()));
                    matrixValues = parseMatrix();
                }
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(MatrixInputPanel.this, "Error reading file: " + exception.getMessage(),
                        "Error Matrix " + matrixName, JOptionPane.ERROR_MESSAGE);
            }
        });

        parseMatrixButton.addActionListener(actionEvent -> matrixValues = parseMatrix());
    }

    private int inputMatrixSize() {
        rows = getMatrixSize("Enter number of rows:");
        if (rows == -1) return -1;

        cols = getMatrixSize("Enter number of columns:");
        if (cols == -1) return -1;

        if (rows <= 0 || cols <= 0) {
            JOptionPane.showMessageDialog(MatrixInputPanel.this, "Invalid matrix dimensions. Please enter positive values for rows and columns.",
                    "Error Matrix " + matrixName, JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        hasSetSize = true;
        return 1;
    }

    private int getMatrixSize(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(MatrixInputPanel.this, message, String.format("Matrix %s", matrixName), JOptionPane.PLAIN_MESSAGE);
            if (input != null) {
                try {
                    int size = Integer.parseInt(input);
                    if (size > 0) {
                        return size;
                    } else {
                        JOptionPane.showMessageDialog(MatrixInputPanel.this, "Please enter a positive number for size.",
                                "Invalid Input Matrix" + matrixName, JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(MatrixInputPanel.this, "Please enter a valid integer number.",
                            "Invalid Input Matrix" + matrixName, JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return -1;
            }
        }
    }

    private int[][] parseMatrix() {
        String matrixInput = matrixInputArea.getText().trim();
        int[][] matrix;
        if (matrixInput.isEmpty()) {
            return null;
        }
        try {
            matrix = Utils.parseMatrix(matrixInput);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MatrixInputPanel.this, "Column separated by space, rows separated by new line.",
                    "Invalid Input Matrix" + matrixName, JOptionPane.ERROR_MESSAGE);
            return null;
        }
        JOptionPane.showMessageDialog(MatrixInputPanel.this, "Parse matrix success.",
                "Success Matrix " + matrixName, JOptionPane.INFORMATION_MESSAGE);
        return matrix;
    }

    public int[][] getMatrixValues() {
        if (matrixValues == null) {
            JOptionPane.showMessageDialog(MatrixInputPanel.this, "Please Input Matrix " + matrixName,
                    "Invalid Input Matrix" + matrixName, JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return matrixValues;
    }
}
