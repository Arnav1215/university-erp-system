package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.User;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class AddUserPanel extends JPanel {

    private final AdminAPI adminAPI = new AdminAPI();

    public AddUserPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("Add New User", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        add(title, BorderLayout.NORTH);

        JPanel card = ERPComponents.createCard();
        card.setLayout(new GridLayout(7, 2, 12, 12));

        JTextField username = new JTextField();
        JTextField fullname = new JTextField();
        JTextField role = new JTextField();
        JTextField password = new JTextField();
        JTextField email = new JTextField();
        JTextField additional = new JTextField(); 

        card.add(new JLabel("Username:"));
        card.add(username);
        card.add(new JLabel("Full Name:"));
        card.add(fullname);
        card.add(new JLabel("Role (ADMIN / STUDENT / INSTRUCTOR):"));
        card.add(role);
        card.add(new JLabel("Password:"));
        card.add(password);
        card.add(new JLabel("Email:"));
        card.add(email);
        card.add(new JLabel("Roll No (Student) / Department (Instructor):"));
        card.add(additional);

        JButton save = ERPComponents.createPrimaryButton("Save User");
        card.add(new JLabel(""));
        card.add(save);

        save.addActionListener(e -> {
            String usernameText = username.getText().trim();
            String fullnameText = fullname.getText().trim();
            String roleText = role.getText().trim().toUpperCase();
            String passwordText = password.getText().trim();
            String emailText = email.getText().trim();
            String additionalText = additional.getText().trim();

            if (usernameText.isEmpty() || fullnameText.isEmpty() || roleText.isEmpty() || passwordText.isEmpty() || emailText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!roleText.equals("ADMIN") && !roleText.equals("STUDENT") && !roleText.equals("INSTRUCTOR")) {
                JOptionPane.showMessageDialog(this, "Role must be ADMIN, STUDENT, or INSTRUCTOR", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                User user = new User();
                user.setUsername(usernameText);
                user.setRole(roleText);

                Object profile = null;
                String profileType = null;

                if ("STUDENT".equals(roleText)) {
                    if (additionalText.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Roll number is required for students!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Student student = new Student();
                    student.setRollNo(additionalText);
                    student.setFullName(fullnameText);
                    student.setEmail(emailText);
                    profile = student;
                    profileType = "STUDENT";
                } else if ("INSTRUCTOR".equals(roleText)) {
                    if (additionalText.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Department is required for instructors!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Instructor instructor = new Instructor();
                    instructor.setDepartment(additionalText);
                    instructor.setFullName(fullnameText);
                    instructor.setEmail(emailText);
                    profile = instructor;
                    profileType = "INSTRUCTOR";
                }

                String result = adminAPI.createUser(user, passwordText, profileType, profile);
                JOptionPane.showMessageDialog(this, result, result.contains("successfully") ? "Success" : "Error", 
                    result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                
                if (result.contains("successfully")) {
                    // Clear fields
                    username.setText("");
                    fullname.setText("");
                    role.setText("");
                    password.setText("");
                    email.setText("");
                    additional.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ERPColors.BACKGROUND);
        wrapper.add(card, BorderLayout.NORTH);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(wrapper, BorderLayout.CENTER);
    }
}
