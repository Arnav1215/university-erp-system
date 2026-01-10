package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import edu.univ.erp.api.instructor.InstructorAPI;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;

public class EnterScoresPanel extends JPanel {

    private final InstructorAPI instructorAPI = new InstructorAPI();
    private JComboBox<Section> sectionCombo;
    private JTable table;
    private DefaultTableModel model;
    private java.util.List<Enrollment> currentEnrollments;

    public EnterScoresPanel() {
        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        JLabel title = new JLabel("📝 Enter Scores", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));
        add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(ERPColors.BACKGROUND);

        top.add(new JLabel("Select Section:"));

        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(300, 28));
        sectionCombo.addActionListener(e -> loadEnrollments());
        top.add(sectionCombo);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadSections());
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);

        
        String[] cols = {"Enrollment ID", "Student Name", "Quiz Score", "Quiz Max", "Midterm Score", "Midterm Max", "Final Score", "Final Max"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 2; 
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        
        JButton saveBtn = ERPComponents.createPrimaryButton("Save Scores");
        saveBtn.addActionListener(e -> saveScores());

        JPanel bottom = new JPanel();
        bottom.setBackground(ERPColors.BACKGROUND);
        bottom.add(saveBtn);

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
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            for (Enrollment e : currentEnrollments) {
                
                java.util.List<edu.univ.erp.domain.Grade> grades = instructorAPI.getSectionGrades(selectedSection.getSectionId());
                edu.univ.erp.domain.Grade quiz = null, midterm = null, finalGrade = null;
                
                for (edu.univ.erp.domain.Grade g : grades) {
                    if (g.getEnrollmentId() == e.getEnrollmentId()) {
                        String comp = g.getComponent().toUpperCase();
                        if (comp.contains("QUIZ")) quiz = g;
                        else if (comp.contains("MIDTERM")) midterm = g;
                        else if (comp.contains("FINAL") || comp.contains("END") || comp.contains("SEM")) finalGrade = g;
                    }
                }

                model.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentName() != null ? e.getStudentName() : "Student " + e.getEnrollmentId(),
                    quiz != null && quiz.getScore() != null ? quiz.getScore() : "",
                    quiz != null && quiz.getMaxScore() != null ? quiz.getMaxScore() : "",
                    midterm != null && midterm.getScore() != null ? midterm.getScore() : "",
                    midterm != null && midterm.getMaxScore() != null ? midterm.getMaxScore() : "",
                    finalGrade != null && finalGrade.getScore() != null ? finalGrade.getScore() : "",
                    finalGrade != null && finalGrade.getMaxScore() != null ? finalGrade.getMaxScore() : ""
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveScores() {
        if (currentEnrollments == null || currentEnrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No enrollments to save scores for.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int saved = 0;
        int errors = 0;

        for (int row = 0; row < model.getRowCount(); row++) {
            int enrollmentId = (Integer) model.getValueAt(row, 0);
            
            
            Object quizScore = model.getValueAt(row, 2);
            Object quizMax = model.getValueAt(row, 3);
            if (quizScore != null && !quizScore.toString().trim().isEmpty()) {
                try {
                    Double score = Double.parseDouble(quizScore.toString());
                    if (score < 0) {
                        showNegativeScoreWarning();
                        return;
                    }
                    Double max = quizMax != null && !quizMax.toString().trim().isEmpty() 
                        ? Double.parseDouble(quizMax.toString()) : 100.0;
                    String result = instructorAPI.enterScore(enrollmentId, "Quiz", score, max);
                    if (result.contains("successfully")) saved++;
                    else errors++;
                } catch (NumberFormatException e) {
                    errors++;
                }
            }

            
            Object midtermScore = model.getValueAt(row, 4);
            Object midtermMax = model.getValueAt(row, 5);
            if (midtermScore != null && !midtermScore.toString().trim().isEmpty()) {
                try {
                    Double score = Double.parseDouble(midtermScore.toString());
                    if (score < 0) {
                        showNegativeScoreWarning();
                        return;
                    }
                    Double max = midtermMax != null && !midtermMax.toString().trim().isEmpty() 
                        ? Double.parseDouble(midtermMax.toString()) : 100.0;
                    String result = instructorAPI.enterScore(enrollmentId, "Midterm", score, max);
                    if (result.contains("successfully")) saved++;
                    else errors++;
                } catch (NumberFormatException e) {
                    errors++;
                }
            }

            
            Object finalScore = model.getValueAt(row, 6);
            Object finalMax = model.getValueAt(row, 7);
            if (finalScore != null && !finalScore.toString().trim().isEmpty()) {
                try {
                    Double score = Double.parseDouble(finalScore.toString());
                    if (score < 0) {
                        showNegativeScoreWarning();
                        return;
                    }
                    Double max = finalMax != null && !finalMax.toString().trim().isEmpty() 
                        ? Double.parseDouble(finalMax.toString()) : 100.0;
                    String result = instructorAPI.enterScore(enrollmentId, "Final", score, max);
                    if (result.contains("successfully")) saved++;
                    else errors++;
                } catch (NumberFormatException e) {
                    errors++;
                }
            }
        }

        String message = String.format("Saved %d scores. %d errors.", saved, errors);
        JOptionPane.showMessageDialog(this, message, saved > 0 ? "Success" : "Error", 
            saved > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        
        if (saved > 0) {
            loadEnrollments(); 
        }
    }

    private void showNegativeScoreWarning() {
        JOptionPane.showMessageDialog(this,
                "Please enter marks greater than or equal to 0.",
                "Validation Warning",
                JOptionPane.WARNING_MESSAGE);
    }
}
