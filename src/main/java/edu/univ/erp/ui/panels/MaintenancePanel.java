package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.ui.components.MaintenanceModeNotifier;

public class MaintenancePanel extends JPanel {

    private final AdminAPI adminAPI = new AdminAPI();
    private JLabel statusLabel;

    public MaintenancePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("⚙️ Maintenance Mode", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        title.setForeground(new Color(30, 60, 150));
        add(title, BorderLayout.NORTH);

        statusLabel = new JLabel("Current Status: Loading...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(Color.GRAY);

        JButton toggleBtn = new JButton("🔄 Toggle Maintenance Mode");
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        toggleBtn.setBackground(new Color(50, 90, 200));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);

        toggleBtn.addActionListener(e -> {
            boolean currentStatus = adminAPI.isMaintenanceMode();
            boolean newStatus = !currentStatus;
            
            if (adminAPI.toggleMaintenanceMode(newStatus)) {
                updateStatusLabel(newStatus);
                MaintenanceModeNotifier.notifyListeners();
                String message = newStatus 
                    ? "Maintenance Mode is now ENABLED.\nStudents and instructors can view but not modify data."
                    : "Maintenance Mode is now DISABLED.\nNormal system access restored.";
                JOptionPane.showMessageDialog(this, message,
                    newStatus ? "Maintenance ON" : "Maintenance OFF", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to toggle maintenance mode.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));
        centerPanel.add(statusLabel);
        centerPanel.add(toggleBtn);

        add(centerPanel, BorderLayout.CENTER);

        
        SwingUtilities.invokeLater(this::loadStatus);
    }

    private void loadStatus() {
        boolean isOn = adminAPI.isMaintenanceMode();
        updateStatusLabel(isOn);
    }

    private void updateStatusLabel(boolean isOn) {
        if (isOn) {
            statusLabel.setText("Current Status: ON ✅");
            statusLabel.setForeground(new Color(0, 180, 0));
        } else {
            statusLabel.setText("Current Status: OFF ❌");
            statusLabel.setForeground(Color.GRAY);
        }
    }
}
