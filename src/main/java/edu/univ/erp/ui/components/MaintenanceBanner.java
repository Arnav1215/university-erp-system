package edu.univ.erp.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MaintenanceBanner extends JPanel {

    public MaintenanceBanner() {
        setBackground(new Color(210, 40, 40));
        setLayout(new BorderLayout());

        JLabel label = new JLabel(
                "⚠ Maintenance Mode is ON — Editing is disabled",
                SwingConstants.CENTER
        );
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        add(label, BorderLayout.CENTER);
        setPreferredSize(new Dimension(0, 35));
    }
}
