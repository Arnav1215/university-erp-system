package edu.univ.erp.ui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.api.catalog.CatalogAPI;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

public class AddSection extends JFrame {
    private JTextField courseCodeField, dayField, timeField, roomField, capacityField, semesterField, yearField;
    private final AdminAPI adminAPI = new AdminAPI();
    private final CatalogAPI catalogAPI = new CatalogAPI();

    public AddSection() {
        setTitle("Add New Section");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 2, 10, 10));

        add(new JLabel("Course Code:"));
        courseCodeField = new JTextField();
        add(courseCodeField);

        add(new JLabel("Day:"));
        dayField = new JTextField();
        add(dayField);

        add(new JLabel("Time (e.g., 10:00-11:30):"));
        timeField = new JTextField();
        add(timeField);

        add(new JLabel("Room:"));
        roomField = new JTextField();
        add(roomField);

        add(new JLabel("Capacity:"));
        capacityField = new JTextField();
        add(capacityField);

        add(new JLabel("Semester (Spring/Fall/Summer):"));
        semesterField = new JTextField();
        add(semesterField);

        add(new JLabel("Year:"));
        yearField = new JTextField();
        add(yearField);

        JButton saveBtn = new JButton("Save Section");
        add(new JLabel(""));
        add(saveBtn);
        saveBtn.addActionListener(e -> saveSection());
    }

    private void saveSection() {
        String courseCode = courseCodeField.getText().trim();
        String day = dayField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();
        String capacity = capacityField.getText().trim();
        String semester = semesterField.getText().trim();
        String year = yearField.getText().trim();

        if (courseCode.isEmpty() || day.isEmpty() || time.isEmpty() || room.isEmpty() || 
            capacity.isEmpty() || semester.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Find course by code
            Course course = catalogAPI.getCourseByCode(courseCode);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course not found: " + courseCode, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacityValue = Integer.parseInt(capacity);
            if (capacityValue <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be a positive number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int yearValue = Integer.parseInt(year);
            if (yearValue < 2020 || yearValue > 2100) {
                JOptionPane.showMessageDialog(this, "Year must be between 2020 and 2100!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Section section = new Section();
            section.setCourseId(course.getCourseId());
            section.setDay(day);
            section.setTime(time);
            section.setRoom(room);
            section.setCapacity(capacityValue);
            section.setSemester(semester);
            section.setYear(yearValue);

            String result = adminAPI.createSection(section);
            JOptionPane.showMessageDialog(this, result, result.contains("successfully") ? "Success" : "Error", 
                result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (result.contains("successfully")) {
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity and Year must be valid numbers!", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating section: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
