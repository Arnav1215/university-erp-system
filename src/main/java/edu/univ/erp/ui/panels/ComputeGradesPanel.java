package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.domain.AssessmentWeights;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class ComputeGradesPanel extends JPanel {

    private final InstructorAPI instructorAPI = new InstructorAPI();
    private JComboBox<Section> sectionCombo;
    private JTable table;
    private DefaultTableModel model;
    private java.util.List<Enrollment> currentEnrollments;
    private JTextField quizWeightField, midtermWeightField, endSemWeightField;

    public ComputeGradesPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("Compute Final Grades", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(ERPColors.BACKGROUND);
        topPanel.add(new JLabel("Select Section:"));
        
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(300, 28));
        sectionCombo.addActionListener(e -> refreshForSelectedSection());
        topPanel.add(sectionCombo);

        topPanel.add(new JLabel("Quiz Weight (%):"));
        quizWeightField = new JTextField("20", 5);
        topPanel.add(quizWeightField);

        topPanel.add(new JLabel("Midterm Weight (%):"));
        midtermWeightField = new JTextField("30", 5);
        topPanel.add(midtermWeightField);

        topPanel.add(new JLabel("End-Sem Weight (%):"));
        endSemWeightField = new JTextField("50", 5);
        topPanel.add(endSemWeightField);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadSections());
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Enrollment ID", "Student Name", "Quiz", "Midterm", "Final", "Computed Grade"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; 
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton computeBtn = ERPComponents.createPrimaryButton("Compute Grades");
        computeBtn.addActionListener(e -> computeGrades());

        JButton exportBtn = ERPComponents.createPrimaryButton("Export CSV");
        exportBtn.addActionListener(e -> exportCSV());

        JPanel bottom = new JPanel();
        bottom.setBackground(ERPColors.BACKGROUND);
        bottom.add(computeBtn);
        bottom.add(exportBtn);

        add(bottom, BorderLayout.SOUTH);

        
        SwingUtilities.invokeLater(this::loadSections);
    }

    private void loadSections() {
        try {
            java.util.List<Section> sections = instructorAPI.getMySections();
            sectionCombo.removeAllItems();
            for (Section s : sections) {
                sectionCombo.addItem(s);
            }
            if (sections.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No sections assigned to you.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                sectionCombo.setSelectedIndex(0);
                refreshForSelectedSection();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshForSelectedSection() {
        loadWeightsForSelectedSection();
        loadEnrollments();
    }

    private void loadWeightsForSelectedSection() {
        Section selectedSection = (Section) sectionCombo.getSelectedItem();
        if (selectedSection == null) {
            quizWeightField.setText("20");
            midtermWeightField.setText("30");
            endSemWeightField.setText("50");
            return;
        }
        try {
            AssessmentWeights weights = instructorAPI.getAssessmentWeights(selectedSection.getSectionId());
            if (weights != null) {
                quizWeightField.setText(String.format("%.2f", weights.getQuizWeight()));
                midtermWeightField.setText(String.format("%.2f", weights.getMidtermWeight()));
                endSemWeightField.setText(String.format("%.2f", weights.getEndSemWeight()));
            } else {
                quizWeightField.setText("20");
                midtermWeightField.setText("30");
                endSemWeightField.setText("50");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to load assessment weights: " + e.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadEnrollments() {
        Section selectedSection = (Section) sectionCombo.getSelectedItem();
        if (selectedSection == null) {
            model.setRowCount(0);
            return;
        }

        model.setRowCount(0);
        try {
            currentEnrollments = instructorAPI.getSectionEnrollments(selectedSection.getSectionId());
            java.util.List<Grade> grades = instructorAPI.getSectionGrades(selectedSection.getSectionId());
            
            for (Enrollment e : currentEnrollments) {
                
                Grade quiz = null, midterm = null, finalGrade = null;
                Double computedFinal = null;
                
                for (Grade g : grades) {
                    if (g.getEnrollmentId() == e.getEnrollmentId()) {
                        String comp = g.getComponent().toUpperCase();
                        if (comp.contains("QUIZ")) quiz = g;
                        else if (comp.contains("MIDTERM")) midterm = g;
                        else if (comp.contains("FINAL") || comp.contains("END") || comp.contains("SEM")) finalGrade = g;
                        if (g.getFinalGrade() != null) computedFinal = g.getFinalGrade();
                    }
                }

                String quizStr = quiz != null && quiz.getScore() != null && quiz.getMaxScore() != null 
                    ? String.format("%.0f/%.0f", quiz.getScore(), quiz.getMaxScore()) : "-";
                String midtermStr = midterm != null && midterm.getScore() != null && midterm.getMaxScore() != null 
                    ? String.format("%.0f/%.0f", midterm.getScore(), midterm.getMaxScore()) : "-";
                String finalStr = finalGrade != null && finalGrade.getScore() != null && finalGrade.getMaxScore() != null 
                    ? String.format("%.0f/%.0f", finalGrade.getScore(), finalGrade.getMaxScore()) : "-";
                String computedStr = computedFinal != null ? String.format("%.2f", computedFinal) : "-";

                model.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentName() != null ? e.getStudentName() : "Student " + e.getEnrollmentId(),
                    quizStr,
                    midtermStr,
                    finalStr,
                    computedStr
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void computeGrades() {
        if (currentEnrollments == null || currentEnrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No enrollments to compute grades for.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            double quizWeight = Double.parseDouble(quizWeightField.getText().trim());
            double midtermWeight = Double.parseDouble(midtermWeightField.getText().trim());
            double endSemWeight = Double.parseDouble(endSemWeightField.getText().trim());

            if (Math.abs(quizWeight + midtermWeight + endSemWeight - 100.0) > 0.01) {
                JOptionPane.showMessageDialog(this, "Weights must sum to 100%", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int computed = 0;
            int errors = 0;

            for (int row = 0; row < model.getRowCount(); row++) {
                int enrollmentId = (Integer) model.getValueAt(row, 0);
                String result = instructorAPI.computeFinalGrade(enrollmentId, quizWeight, midtermWeight, endSemWeight);
                if (result.contains("computed")) computed++;
                else errors++;
            }

            String message = String.format("Computed grades for %d students. %d errors.", computed, errors);
            JOptionPane.showMessageDialog(this, message, computed > 0 ? "Success" : "Error", 
                computed > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (computed > 0) {
                loadEnrollments(); 
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Weights must be valid numbers", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error computing grades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCSV() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String filePath = System.getProperty("user.home") + "/Desktop/Grades_" + 
                (sectionCombo.getSelectedItem() != null ? 
                    ((Section) sectionCombo.getSelectedItem()).getCourseCode() : "All") + ".csv";
            
            com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new java.io.FileWriter(filePath));
            
            
            String[] header = new String[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                header[i] = model.getColumnName(i);
            }
            writer.writeNext(header);
            
            
            for (int row = 0; row < model.getRowCount(); row++) {
                String[] rowData = new String[model.getColumnCount()];
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    rowData[col] = value != null ? value.toString() : "";
                }
                writer.writeNext(rowData);
            }
            
            writer.close();
            
            JOptionPane.showMessageDialog(this,
                "✔ Grades exported to Desktop",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error exporting: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
