package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;

public class ClassStatsPanel extends JPanel {
    
    private final InstructorAPI instructorAPI = new InstructorAPI();
    private JComboBox<Section> sectionCombo;
    private JLabel totalStudentsLabel;
    private JLabel avgQuizLabel;
    private JLabel avgMidtermLabel;
    private JLabel avgFinalLabel;
    private JLabel passRateLabel;

    public ClassStatsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initComponents();
        loadSections();
    }

    private void initComponents() {
        
        JLabel title = new JLabel("Class Statistics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(title, BorderLayout.NORTH);

        
        JPanel selectorPanel = new JPanel();
        selectorPanel.add(new JLabel("Select Section:"));
        sectionCombo = new JComboBox<>();
        sectionCombo.addActionListener(e -> updateStats());
        selectorPanel.add(sectionCombo);
        
        
        JPanel statsPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        statsPanel.add(createLabel("Total Students:"));
        totalStudentsLabel = createValueLabel("0");
        statsPanel.add(totalStudentsLabel);

        statsPanel.add(createLabel("Average Quiz Score:"));
        avgQuizLabel = createValueLabel("N/A");
        statsPanel.add(avgQuizLabel);

        statsPanel.add(createLabel("Average Midterm Score:"));
        avgMidtermLabel = createValueLabel("N/A");
        statsPanel.add(avgMidtermLabel);

        statsPanel.add(createLabel("Average Final Score:"));
        avgFinalLabel = createValueLabel("N/A");
        statsPanel.add(avgFinalLabel);

        statsPanel.add(createLabel("Pass Rate:"));
        passRateLabel = createValueLabel("N/A");
        statsPanel.add(passRateLabel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(selectorPanel, BorderLayout.NORTH);
        centerPanel.add(statsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(50, 90, 200));
        return label;
    }

    private void loadSections() {
        try {
            List<Section> sections = instructorAPI.getMySections();
            sectionCombo.removeAllItems();
            for (Section section : sections) {
                sectionCombo.addItem(section);
            }
            if (!sections.isEmpty()) {
                updateStats();
            }
        } catch (Exception e) {
            totalStudentsLabel.setText("Error loading sections");
        }
    }

    private void updateStats() {
        Section selectedSection = (Section) sectionCombo.getSelectedItem();
        if (selectedSection == null) return;

        try {
            List<Enrollment> enrollments = instructorAPI.getSectionEnrollments(selectedSection.getSectionId());
            List<Grade> grades = instructorAPI.getSectionGrades(selectedSection.getSectionId());

            totalStudentsLabel.setText(String.valueOf(enrollments.size()));

            if (grades.isEmpty()) {
                avgQuizLabel.setText("N/A");
                avgMidtermLabel.setText("N/A");
                avgFinalLabel.setText("N/A");
                passRateLabel.setText("N/A");
                return;
            }

            
            double quizSum = 0, midtermSum = 0, finalSum = 0;
            int quizCount = 0, midtermCount = 0, finalCount = 0, passCount = 0;

            for (Grade grade : grades) {
                String component = grade.getComponent().toLowerCase();
                if (grade.getScore() != null) {
                    if (component.contains("quiz")) {
                        quizSum += grade.getScore();
                        quizCount++;
                    } else if (component.contains("midterm")) {
                        midtermSum += grade.getScore();
                        midtermCount++;
                    } else if (component.contains("final")) {
                        finalSum += grade.getScore();
                        finalCount++;
                        if (grade.getScore() >= 50) passCount++; 
                    }
                }
            }

            avgQuizLabel.setText(quizCount > 0 ? String.format("%.1f", quizSum / quizCount) : "N/A");
            avgMidtermLabel.setText(midtermCount > 0 ? String.format("%.1f", midtermSum / midtermCount) : "N/A");
            avgFinalLabel.setText(finalCount > 0 ? String.format("%.1f", finalSum / finalCount) : "N/A");
            
            if (finalCount > 0) {
                double passRate = (passCount * 100.0) / finalCount;
                passRateLabel.setText(String.format("%.1f%%", passRate));
            } else {
                passRateLabel.setText("N/A");
            }

        } catch (Exception e) {
            totalStudentsLabel.setText("Error loading stats");
        }
    }
}