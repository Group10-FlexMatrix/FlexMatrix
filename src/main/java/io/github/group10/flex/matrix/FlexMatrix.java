package io.github.group10.flex.matrix;

import javax.swing.*;
import io.github.group10.flex.matrix.UI.*;

class FlexMatrix {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("Flex Matrix");
            frame.setContentPane(new MainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}