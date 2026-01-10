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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;

public class EditSectionsPanel extends JPanel {
    
    private final AdminAPI adminAPI = new AdminAPI();
    private JComboBox<Section> sectionCombo;
    private JComboBox<Course> courseCombo;
    private JComboBox<Instructor> instructorCombo;
    private JTextField dayField;
    private JTextField timeField;
    private JTextField roomField;
    private JTextField capacityField;
    private JTextField semesterField;
    private JTextField yearField;

    public EditSectionsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initComponents();
        loadData();
    }

    private void initComponents() {
        
        JLabel title = new JLabel("Edit Sections", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(title, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        
        JPanel selectorPanel = new JPanel();
        selectorPanel.add(new JLabel("Select Section:"));
        sectionCombo = new JComboBox<>();
        sectionCombo.addActionListener(e -> loadSelectedSection());
        selectorPanel.add(sectionCombo);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        selectorPanel.add(refreshBtn);
        
        mainPanel.add(selectorPanel, BorderLayout.NORTH);

        
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Course:"));
        courseCombo = new JComboBox<>();
        formPanel.add(courseCombo);

        formPanel.add(new JLabel("Instructor:"));
        instructorCombo = new JComboBox<>();
        formPanel.add(instructorCombo);

        formPanel.add(new JLabel("Day:"));
        dayField = new JTextField();
        formPanel.add(dayField);

        formPanel.add(new JLabel("Time:"));
        timeField = new JTextField();
        formPanel.add(timeField);

        formPanel.add(new JLabel("Room:"));
        roomField = new JTextField();
        formPanel.add(roomField);

        formPanel.add(new JLabel("Capacity:"));
        capacityField = new JTextField();
        formPanel.add(capacityField);

        formPanel.add(new JLabel("Semester:"));
        semesterField = new JTextField();
        formPanel.add(semesterField);

        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField();
        formPanel.add(yearField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel();
        
        JButton updateBtn = new JButton("Update Section");
        updateBtn.addActionListener(e -> updateSection());
        buttonPanel.add(updateBtn);
        
        JButton deleteBtn = new JButton("Delete Section");
        deleteBtn.setBackground(new Color(200, 50, 50));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteSection());
        buttonPanel.add(deleteBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            
            List<Section> sections = adminAPI.getAllSections();
            sectionCombo.removeAllItems();
            for (Section section : sections) {
                sectionCombo.addItem(section);
            }

            
            List<Course> courses = adminAPI.getAllCourses();
            courseCombo.removeAllItems();
            for (Course course : courses) {
                courseCombo.addItem(course);
            }

            
            List<Instructor> instructors = adminAPI.getAllInstructors();
            instructorCombo.removeAllItems();
            instructorCombo.addItem(null); 
            for (Instructor instructor : instructors) {
                instructorCombo.addItem(instructor);
            }

            if (!sections.isEmpty()) {
                loadSelectedSection();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void loadSelectedSection() {
        Section selected = (Section) sectionCombo.getSelectedItem();
        if (selected != null) {
            
            for (int i = 0; i < courseCombo.getItemCount(); i++) {
                Course course = courseCombo.getItemAt(i);
                if (course != null && course.getCourseId() == selected.getCourseId()) {
                    courseCombo.setSelectedIndex(i);
                    break;
                }
            }

            
            for (int i = 0; i < instructorCombo.getItemCount(); i++) {
                Instructor instructor = instructorCombo.getItemAt(i);
                if (instructor != null && instructor.getUserId() == selected.getInstructorId()) {
                    instructorCombo.setSelectedIndex(i);
                    break;
                }
            }

            dayField.setText(selected.getDay());
            timeField.setText(selected.getTime());
            roomField.setText(selected.getRoom());
            capacityField.setText(String.valueOf(selected.getCapacity()));
            semesterField.setText(selected.getSemester());
            yearField.setText(String.valueOf(selected.getYear()));
        }
    }

    private void updateSection() {
        Section selected = (Section) sectionCombo.getSelectedItem();
        if (selected == null) return;

        try {
            Course course = (Course) courseCombo.getSelectedItem();
            Instructor instructor = (Instructor) instructorCombo.getSelectedItem();
            String day = dayField.getText().trim();
            String time = timeField.getText().trim();
            String room = roomField.getText().trim();
            String capacityText = capacityField.getText().trim();
            String semester = semesterField.getText().trim();
            String yearText = yearField.getText().trim();

            if (course == null || capacityText.isEmpty() || semester.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields");
                return;
            }

            int capacity = Integer.parseInt(capacityText);
            int year = Integer.parseInt(yearText);
            int instructorId = instructor != null ? instructor.getUserId() : 0;
            
            String result = adminAPI.updateSection(selected.getSectionId(), course.getCourseId(), 
                instructorId > 0 ? instructorId : null, day, time, room, capacity, semester, year);
            
            if ("Section updated successfully".equals(result)) {
                JOptionPane.showMessageDialog(this, result);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + result);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity and Year must be numbers");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteSection() {
        Section selected = (Section) sectionCombo.getSelectedItem();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this section?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String result = adminAPI.deleteSection(selected.getSectionId());
                if ("Section deleted successfully".equals(result)) {
                    JOptionPane.showMessageDialog(this, result);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + result);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }


}