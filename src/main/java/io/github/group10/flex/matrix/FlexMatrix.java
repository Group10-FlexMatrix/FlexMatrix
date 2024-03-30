package io.github.group10.flex.matrix;

import javax.swing.*;
import java.awt.*;
import io.github.group10.flex.matrix.UI.*;

class FlexMatrix {
    public static void main(String[] args) throws Exception {
//
//        System.out.printf("A(%d x %d) * B(%d x %d)\n", m, n, p, q);
//
//
//        int divideThreshold = Math.max(m,Math.max(n,q)) / 2;
//        System.out.printf("Divide threshold: %d", divideThreshold);

        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                var frame = new JFrame("Flex Matrix");
                var mainPanel = new JPanel();
                mainPanel.setLayout(new GridLayout(1,1, 20, 0));

                var inputPanel = new JPanel();
                inputPanel.setLayout(new GridLayout());
                inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
                var matrixA = new MatrixInputPanel("A");
                var matrixB = new MatrixInputPanel("B");
                inputPanel.add(matrixA);
                inputPanel.add(matrixB);

                mainPanel.add(inputPanel);
                mainPanel.add(new OutputPanel(matrixA, matrixB));
                frame.setContentPane(mainPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}