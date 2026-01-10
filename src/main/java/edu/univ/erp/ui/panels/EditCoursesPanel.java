package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.domain.Course;

public class EditCoursesPanel extends JPanel {
    
    private final AdminAPI adminAPI = new AdminAPI();
    private JComboBox<Course> courseCombo;
    private JTextField codeField;
    private JTextField titleField;
    private JTextField creditsField;
    private JTextArea descriptionArea;

    public EditCoursesPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initComponents();
        loadCourses();
    }

    private void initComponents() {
        
        JLabel title = new JLabel("Edit Courses", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(title, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        
        JPanel selectorPanel = new JPanel();
        selectorPanel.add(new JLabel("Select Course:"));
        courseCombo = new JComboBox<>();
        courseCombo.addActionListener(e -> loadSelectedCourse());
        selectorPanel.add(courseCombo);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadCourses());
        selectorPanel.add(refreshBtn);
        
        mainPanel.add(selectorPanel, BorderLayout.NORTH);

        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Course Code:"));
        codeField = new JTextField();
        formPanel.add(codeField);

        formPanel.add(new JLabel("Course Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Credits:"));
        creditsField = new JTextField();
        formPanel.add(creditsField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descriptionArea));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel();
        
        JButton updateBtn = new JButton("Update Course");
        updateBtn.addActionListener(e -> updateCourse());
        buttonPanel.add(updateBtn);
        
        JButton deleteBtn = new JButton("Delete Course");
        deleteBtn.setBackground(new Color(200, 50, 50));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteCourse());
        buttonPanel.add(deleteBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadCourses() {
        try {
            List<Course> courses = adminAPI.getAllCourses();
            courseCombo.removeAllItems();
            for (Course course : courses) {
                courseCombo.addItem(course);
            }
            if (!courses.isEmpty()) {
                loadSelectedCourse();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
        }
    }

    private void loadSelectedCourse() {
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected != null) {
            codeField.setText(selected.getCode());
            titleField.setText(selected.getTitle());
            creditsField.setText(String.valueOf(selected.getCredits()));
            descriptionArea.setText(selected.getDescription());
        }
    }

    private void updateCourse() {
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected == null) return;

        try {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            String creditsText = creditsField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (code.isEmpty() || title.isEmpty() || creditsText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields");
                return;
            }

            int credits = Integer.parseInt(creditsText);
            
            String result = adminAPI.updateCourse(selected.getCourseId(), code, title, credits, description);
            
            if ("Course updated successfully".equals(result)) {
                JOptionPane.showMessageDialog(this, result);
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + result);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits must be a number");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this course?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String result = adminAPI.deleteCourse(selected.getCourseId());
                if ("Course deleted successfully".equals(result)) {
                    JOptionPane.showMessageDialog(this, result);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + result);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }


}