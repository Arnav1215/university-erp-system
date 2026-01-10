package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import edu.univ.erp.ui.theme.ERPColors;
import edu.univ.erp.ui.theme.ERPFonts;
import edu.univ.erp.util.StudentGradeCalculator;
import edu.univ.erp.util.StudentGradeCalculator.CourseResult;

public class StudentGradesPanel extends JPanel {

    private static final String ALL_SEMESTERS = "All Semesters";
    private DefaultTableModel model;
    private JComboBox<String> semesterFilter;
    private JLabel semesterGpaValue;
    private JLabel cgpaValue;
    private java.util.List<CourseResult> courseResults = java.util.Collections.emptyList();
    private java.util.Map<String, java.util.List<CourseResult>> semesters = java.util.Collections.emptyMap();
    private java.util.List<String> semesterOrder = java.util.Collections.emptyList();
    private double cgpa = Double.NaN;
    private StudentGradeCalculator.GradeReport gradeReport;

    public StudentGradesPanel() {

        setLayout(new BorderLayout());
        setBackground(ERPColors.BACKGROUND);

        
        JLabel title = new JLabel("My Grades", SwingConstants.LEFT);
        title.setFont(ERPFonts.TITLE);
        title.setForeground(ERPColors.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ERPColors.BACKGROUND);
        header.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(ERPFonts.SUBTITLE);
        semesterLabel.setForeground(ERPColors.TEXT_PRIMARY);
        controls.add(semesterLabel);

        semesterFilter = new JComboBox<>();
        semesterFilter.setPreferredSize(new Dimension(220, 30));
        semesterFilter.addActionListener(e -> refreshTable());
        controls.add(semesterFilter);

        JPanel metrics = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metrics.setOpaque(false);
        JLabel sgpaLabel = new JLabel("SGPA:");
        sgpaLabel.setFont(ERPFonts.SUBTITLE);
        sgpaLabel.setForeground(ERPColors.TEXT_PRIMARY);
        semesterGpaValue = new JLabel("-");
        semesterGpaValue.setFont(ERPFonts.TITLE);
        semesterGpaValue.setForeground(new Color(0, 128, 96));
        metrics.add(sgpaLabel);
        metrics.add(semesterGpaValue);

        JLabel cgpaLabel = new JLabel("  |  CGPA:");
        cgpaLabel.setFont(ERPFonts.SUBTITLE);
        cgpaLabel.setForeground(ERPColors.TEXT_PRIMARY);
        cgpaValue = new JLabel("-");
        cgpaValue.setFont(ERPFonts.TITLE);
        cgpaValue.setForeground(new Color(0, 96, 160));
        metrics.add(cgpaLabel);
        metrics.add(cgpaValue);

        JPanel controlWrapper = new JPanel(new BorderLayout());
        controlWrapper.setOpaque(false);
        controlWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        controlWrapper.add(controls, BorderLayout.WEST);
        controlWrapper.add(metrics, BorderLayout.EAST);

        header.add(controlWrapper, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        
        String[] columns = {
            "Course Code", "Course Title", "Semester", "Credits",
            "Quiz", "Midterm", "End-Sem", "Final %", "Grade"
        };

        model = new DefaultTableModel(columns, 0);
        loadGradeData();

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        add(scroll, BorderLayout.CENTER);
    }

    
    private void styleTable(JTable table) {
        table.setFont(ERPFonts.NORMAL);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(ERPFonts.SUBTITLE);
        header.setBackground(new Color(230, 235, 245));
        header.setForeground(ERPColors.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 40));

        
        table.getColumnModel().getColumn(8).setCellRenderer(new GradeRenderer());
    }

    private void loadGradeData() {
        try {
            edu.univ.erp.api.student.StudentAPI studentAPI = new edu.univ.erp.api.student.StudentAPI();
            java.util.List<edu.univ.erp.domain.Grade> grades = studentAPI.getMyGrades();
            gradeReport = StudentGradeCalculator.buildReport(grades);
            courseResults = gradeReport.getCourses();
            semesters = gradeReport.getCoursesBySemester();
            semesterOrder = gradeReport.getSemesterOrder();
            cgpa = gradeReport.getCgpa();
            updateSemesterFilter();
            refreshTable();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSemesterFilter() {
        semesterFilter.removeAllItems();
        semesterFilter.addItem(ALL_SEMESTERS);
        for (String sem : semesterOrder) {
            semesterFilter.addItem(sem);
        }
        if (semesterFilter.getItemCount() > 1) {
            semesterFilter.setSelectedIndex(semesterFilter.getItemCount() - 1);
        } else {
            semesterFilter.setSelectedIndex(0);
        }
    }

    private java.util.List<CourseResult> getFilteredCourses() {
        String selected = (String) semesterFilter.getSelectedItem();
        if (selected == null || ALL_SEMESTERS.equals(selected)) {
            return courseResults;
        }
        return semesters.getOrDefault(selected, java.util.Collections.emptyList());
    }

    private void refreshTable() {
        if (model == null) return;
        java.util.List<CourseResult> filtered = getFilteredCourses();
        model.setRowCount(0);
        for (CourseResult course : filtered) {
            String quizStr = formatScore(course.getQuizScore(), course.getQuizMax());
            String midtermStr = formatScore(course.getMidtermScore(), course.getMidtermMax());
            String endSemStr = formatScore(course.getEndSemScore(), course.getEndSemMax());
            String finalPercent = course.getFinalPercent() != null ? String.format("%.2f", course.getFinalPercent()) : "-";
            String gradeLetter = course.getLetterGrade() != null ? course.getLetterGrade() : "-";
            model.addRow(new Object[]{
                safeText(course.getCourseCode(), "-"),
                safeText(course.getCourseTitle(), "Course"),
                safeText(course.getSemesterKey(), "-"),
                course.getCredits() != null ? course.getCredits() : "-",
                quizStr,
                midtermStr,
                endSemStr,
                finalPercent,
                gradeLetter
            });
        }
        updateGpaLabels();
    }

    private void updateGpaLabels() {
        if (semesterGpaValue == null || cgpaValue == null) return;
        String selected = (String) semesterFilter.getSelectedItem();
        if (selected != null && !ALL_SEMESTERS.equals(selected)) {
            double sgpa = resolveSemesterGpa(selected);
            semesterGpaValue.setText(formatGpa(sgpa));
        } else {
            semesterGpaValue.setText("-");
        }
        cgpaValue.setText(formatGpa(cgpa));
    }

    private static String formatGpa(double value) {
        if (Double.isNaN(value)) {
            return "-";
        }
        return String.format("%.2f", value);
    }

    private static String formatScore(Double score, Double max) {
        if (score == null || max == null) {
            return "-";
        }
        if (max == 0) {
            return String.format("%.0f", score);
        }
        return String.format("%.0f/%.0f", score, max);
    }

    private static String safeText(String value, String fallback) {
        return value != null && !value.isEmpty() ? value : fallback;
    }

    private double resolveSemesterGpa(String semesterKey) {
        if (gradeReport != null) {
            return gradeReport.getSgpa(semesterKey);
        }
        return StudentGradeCalculator.computeGpa(semesters.getOrDefault(semesterKey, java.util.Collections.emptyList()));
    }

    
    static class GradeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setOpaque(true);
            label.setForeground(Color.WHITE);

            String grade = value.toString();

            switch (grade) {
                case "A+":
                    label.setBackground(new Color(0, 150, 100));
                    break;
                case "A":
                    label.setBackground(new Color(0, 160, 120));
                    break;
                case "B":
                    label.setBackground(new Color(240, 180, 0));
                    break;
                case "C":
                    label.setBackground(new Color(220, 120, 40));
                    break;
                case "D":
                    label.setBackground(new Color(210, 50, 30));
                    break;
                case "E":
                    label.setBackground(new Color(160, 80, 120));
                    break;
                case "F":
                    label.setBackground(new Color(90, 90, 90));
                    break;
                default:
                    label.setBackground(new Color(150, 150, 150));
                    break;
            }

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            return label;
        }
    }
}
