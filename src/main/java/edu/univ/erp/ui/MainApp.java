package edu.univ.erp.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {
        
        FlatLightLaf.setup();

        
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 12);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Button.focusWidth", 1);

        
        SwingUtilities.invokeLater(() -> {
            
            JFrame frame = new JFrame("University ERP System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(720, 420));
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(50, 90, 200));

            JLabel headerTitle = new JLabel("Welcome to University ERP", SwingConstants.CENTER);
            headerTitle.setForeground(Color.WHITE);
            headerTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            headerPanel.setPreferredSize(new Dimension(720, 80));
            headerPanel.add(headerTitle, BorderLayout.CENTER);

            
            JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
            contentPanel.setBackground(Color.WHITE);

            JLabel infoLabel = new JLabel(
                "<html><div style='text-align: center;'>"
              + "🎓 <b>University ERP</b> helps manage Students, Instructors, Courses, and Grades.<br>"
              + "Use the login below to access your dashboard."
              + "</div></html>", SwingConstants.CENTER);
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));


            JButton openLoginBtn = new JButton("Proceed to Login →");
            openLoginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            openLoginBtn.setBackground(new Color(50, 90, 200));
            openLoginBtn.setForeground(Color.WHITE);
            openLoginBtn.setFocusPainted(false);
            openLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            openLoginBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

            
            openLoginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    openLoginBtn.setBackground(new Color(30, 70, 170));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    openLoginBtn.setBackground(new Color(50, 90, 200));
                }
            });

            openLoginBtn.addActionListener(e -> {
                frame.dispose(); 
                new LoginForm().setVisible(true); 
            });

            contentPanel.add(infoLabel);
            contentPanel.add(openLoginBtn);

            
            JLabel footer = new JLabel("© 2025 University ERP — All Rights Reserved", SwingConstants.CENTER);
            footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            footer.setForeground(new Color(100, 100, 100));

            
            frame.add(headerPanel, BorderLayout.NORTH);
            frame.add(contentPanel, BorderLayout.CENTER);
            frame.add(footer, BorderLayout.SOUTH);

            
            frame.pack();
            frame.setVisible(true);
        });
    }
}
