package edu.univ.erp.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import edu.univ.erp.api.auth.AuthAPI;
import edu.univ.erp.api.auth.AuthAPI.LoginResult;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private static final int MAX_ATTEMPTS = 5;
    private java.util.Map<String, Integer> attemptMap = new java.util.HashMap<>();
    private java.util.Map<String, Long> lockMap = new java.util.HashMap<>();

    
    private final AuthAPI authAPI = new AuthAPI();

    public LoginForm() {

    

        
        setTitle("University ERP - Login");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(245, 247, 250));

        
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel logo = new JLabel("🎓", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 45));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("University ERP Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        title.setForeground(new Color(40, 70, 160));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBackground(Color.WHITE);
        form.setMaximumSize(new Dimension(350, 100));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField = new JTextField();
        styleField(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        styleField(passwordField);

        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passwordField);

        
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());

        
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(new Color(200, 0, 0));
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        
        root.add(Box.createVerticalStrut(10));
        root.add(logo);
        root.add(title);
        root.add(Box.createVerticalStrut(10));
        root.add(form);
        root.add(Box.createVerticalStrut(20));
        root.add(loginButton);
        root.add(Box.createVerticalStrut(15));
        root.add(messageLabel);

        add(root);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(new Color(50, 90, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 70, 170));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 90, 200));
            }
        });
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        
        if (isLocked(username)) {
            long remaining = (lockMap.get(username) - System.currentTimeMillis()) / 1000;
            messageLabel.setText("❌ Account locked. Try again in " + remaining + " seconds");
            return;
        }

        LoginResult result = authAPI.login(username, password);

        if (result.isSuccess()) {
            
            attemptMap.remove(username);
            lockMap.remove(username);
            messageLabel.setText("✔ Welcome " + result.getUser().getRole());

            switch (result.getUser().getRole().toUpperCase()) {
                case "ADMIN" -> new AdminDashboard().setVisible(true);
                case "INSTRUCTOR" -> new InstructorDashboard().setVisible(true);
                default -> new StudentDashboard().setVisible(true);
            }

            dispose();
        } else {
            
            int attempts = attemptMap.getOrDefault(username, 0) + 1;
            attemptMap.put(username, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                
                lockMap.put(username, System.currentTimeMillis() + 300000);
                messageLabel.setText("❌ Too many failed attempts. Account locked for 5 minutes.");
            } else {
                int remaining = MAX_ATTEMPTS - attempts;
                messageLabel.setText("❌ " + result.getMessage() + " (" + remaining + " attempts remaining)");
            }
        }
    }

    private boolean isLocked(String username) {
        Long lockTime = lockMap.get(username);
        if (lockTime != null && System.currentTimeMillis() < lockTime) {
            return true;
        }
        
        if (lockTime != null) {
            lockMap.remove(username);
        }
        return false;
    }



    public static void main(String[] args) {
        FlatMacLightLaf.setup();
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Button.arc", 15);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Panel.arc", 15);

        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
