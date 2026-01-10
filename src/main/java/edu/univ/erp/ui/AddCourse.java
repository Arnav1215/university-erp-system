package edu.univ.erp.ui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.domain.Course;

public class AddCourse extends JFrame {
    private JTextField codeField, titleField, creditsField;
    private final AdminAPI adminAPI = new AdminAPI();

    public AddCourse() {
        setTitle("Add New Course");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        JLabel codeLabel = new JLabel("Course Code:");
        JLabel titleLabel = new JLabel("Course Title:");
        JLabel creditsLabel = new JLabel("Credits:");
        codeField = new JTextField();
        titleField = new JTextField();
        creditsField = new JTextField();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        add(codeLabel); add(codeField);
        add(titleLabel); add(titleField);
        add(creditsLabel); add(creditsField);
        add(saveBtn); add(cancelBtn);

        saveBtn.addActionListener(e -> saveCourse());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void saveCourse() {
        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        String credits = creditsField.getText().trim();

        if (code.isEmpty() || title.isEmpty() || credits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            int c = Integer.parseInt(credits);
            if (c <= 0) throw new NumberFormatException();

            Course course = new Course();
            course.setCode(code);
            course.setTitle(title);
            course.setCredits(c);

            String result = adminAPI.createCourse(course);
            JOptionPane.showMessageDialog(this, result, result.contains("successfully") ? "Success" : "Error", 
                result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (result.contains("successfully")) {
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Credits must be a positive number!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
