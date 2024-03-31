package io.github.group10.flex.matrix.UI;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class MatrixOutputPanel extends JPanel {
    private JTextArea matrixInputArea;
    private JScrollPane scrollPane;
    private JLabel executeTimeLabel;
    private JLabel executeTimeResultLabel;

    public MatrixOutputPanel(String outputName) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(outputName));

        createComponents();
        layoutComponents();
    }

    private void createComponents() {
        matrixInputArea = new JTextArea(40, 40);
        matrixInputArea.setText("");
        matrixInputArea.setFont(new Font("Noto Sans", Font.PLAIN, 15));

        executeTimeLabel = new JLabel("Execute Time:");
        executeTimeResultLabel = new JLabel();

        scrollPane = new JScrollPane(matrixInputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    private void layoutComponents() {
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(executeTimeLabel);
        bottomPanel.add(executeTimeResultLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setResult(String result, double executeTime) {
        matrixInputArea.setText(result);
        var df = new DecimalFormat("0.###########");
        executeTimeResultLabel.setText(String.valueOf(df.format(executeTime)));
    }

    public void clear() {
        matrixInputArea.setText("");
        executeTimeResultLabel.setText("");
    }
}
