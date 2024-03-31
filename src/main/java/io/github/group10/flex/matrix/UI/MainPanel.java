package io.github.group10.flex.matrix.UI;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel  {
    private MatrixInputPanel matrixA, matrixB;
    private OutputPanel output;

    public MainPanel() {
        setLayout(new GridLayout(1,1, 20, 0));
        createComponents();
        layoutComponents();
    }

    private void createComponents() {
        matrixA = new MatrixInputPanel("A");
        matrixB = new MatrixInputPanel("B");

        output = new OutputPanel(matrixA, matrixB);
    }

    private void layoutComponents() {
        var inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        inputPanel.add(matrixA);
        inputPanel.add(matrixB);

        add(inputPanel);
        add(output);
    }
}
