package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
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
import javax.swing.SwingUtilities;

import edu.univ.erp.api.admin.AdminAPI;
import edu.univ.erp.api.catalog.CatalogAPI;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class AddSectionPanel extends JPanel {

    private final AdminAPI adminAPI = new AdminAPI();
    private final CatalogAPI catalogAPI = new CatalogAPI();

    public AddSectionPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("Add Course Section", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        JPanel card = ERPComponents.createCard();
        card.setLayout(new GridLayout(9, 2, 12, 12));

        JTextField courseCode = new JTextField();
        JTextField day = new JTextField();
        JTextField time = new JTextField();
        JTextField room = new JTextField();
        JTextField capacity = new JTextField();
        JComboBox<String> semester = new JComboBox<>();
        populateSemesters(semester);
        semester.setEditable(true); 
        JTextField year = new JTextField();
        JComboBox<InstructorOption> instructorCombo = new JComboBox<>();
        instructorCombo.addItem(new InstructorOption("Unassigned", null));
        SwingUtilities.invokeLater(() -> populateInstructors(instructorCombo));

        card.add(new JLabel("Course Code:"));
        card.add(courseCode);
        card.add(new JLabel("Day:"));
        card.add(day);
        card.add(new JLabel("Time (e.g., 10:00-11:30):"));
        card.add(time);
        card.add(new JLabel("Room:"));
        card.add(room);
        card.add(new JLabel("Capacity:"));
        card.add(capacity);
        card.add(new JLabel("Semester:"));
        card.add(semester);
        card.add(new JLabel("Year:"));
        card.add(year);
        card.add(new JLabel("Instructor:"));
        card.add(instructorCombo);

        JButton save = ERPComponents.createPrimaryButton("Add Section");
        card.add(new JLabel(""));
        card.add(save);

        save.addActionListener(e -> {
            String courseCodeText = courseCode.getText().trim();
            String dayText = day.getText().trim();
            String timeText = time.getText().trim();
            String roomText = room.getText().trim();
            String capacityText = capacity.getText().trim();
            String semesterText = semester.getSelectedItem() != null ? semester.getSelectedItem().toString().trim() : "";
            String yearText = year.getText().trim();

            if (courseCodeText.isEmpty() || dayText.isEmpty() || timeText.isEmpty() || 
                roomText.isEmpty() || capacityText.isEmpty() || semesterText.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                
                Course course = catalogAPI.getCourseByCode(courseCodeText);
                if (course == null) {
                    JOptionPane.showMessageDialog(this, "Course not found: " + courseCodeText, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int capacityValue = Integer.parseInt(capacityText);
                if (capacityValue <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be a positive number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int yearValue = Integer.parseInt(yearText);
                if (yearValue < 2020 || yearValue > 2100) {
                    JOptionPane.showMessageDialog(this, "Year must be between 2020 and 2100!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Section section = new Section();
                section.setCourseId(course.getCourseId());
                section.setDay(dayText);
                section.setTime(timeText);
                section.setRoom(roomText);
                section.setCapacity(capacityValue);
                section.setSemester(semesterText);
                section.setYear(yearValue);
                InstructorOption selected = (InstructorOption) instructorCombo.getSelectedItem();
                if (selected != null && selected.userId() != null) {
                    section.setInstructorId(selected.userId());
                }

                String result = adminAPI.createSection(section);
                JOptionPane.showMessageDialog(this, result, result.contains("successfully") ? "Success" : "Error", 
                    result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                
                if (result.contains("successfully")) {
                    
                    courseCode.setText("");
                    day.setText("");
                    time.setText("");
                    room.setText("");
                    capacity.setText("");
                    year.setText("");
                    instructorCombo.setSelectedIndex(0);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity and Year must be valid numbers!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating section: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ERPColors.BACKGROUND);
        wrapper.add(card, BorderLayout.NORTH);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(wrapper, BorderLayout.CENTER);
    }

    private void populateSemesters(JComboBox<String> combo) {
        try {
            List<String> semesters = catalogAPI.getAvailableSemesters();
            for (String sem : semesters) {
                combo.addItem(sem);
            }
        } catch (Exception e) {
            
            combo.addItem("Spring");
            combo.addItem("Fall");
            combo.addItem("Summer");
            combo.addItem("Winter");
            combo.addItem("Monsoon");
        }
    }

    private void populateInstructors(JComboBox<InstructorOption> combo) {
        combo.setEnabled(false);
        try {
            List<Instructor> instructors = adminAPI.getAllInstructors();
            for (Instructor instructor : instructors) {
                combo.addItem(new InstructorOption(instructor));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load instructors: " + e.getMessage(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
        } finally {
            combo.setEnabled(true);
        }
    }

    private record InstructorOption(String label, Integer userId) {
        InstructorOption(String label, Integer userId) {
            this.label = label;
            this.userId = userId;
        }

        InstructorOption(Instructor instructor) {
            this(instructor.getFullName() != null ? instructor.getFullName() : instructor.getEmail(),
                    instructor.getUserId());
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
