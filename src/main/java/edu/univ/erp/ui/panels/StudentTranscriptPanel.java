package edu.univ.erp.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import com.opencsv.CSVWriter;

import edu.univ.erp.ui.theme.ERPComponents;
import edu.univ.erp.ui.theme.ERPFonts;
import edu.univ.erp.util.StudentGradeCalculator;
import edu.univ.erp.util.StudentGradeCalculator.CourseResult;

public class StudentTranscriptPanel extends JPanel {

    public StudentTranscriptPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JLabel title = new JLabel("Download Transcript (CSV)", SwingConstants.CENTER);
        title.setFont(ERPFonts.TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton downloadBtn = ERPComponents.createPrimaryButton("Download Transcript CSV");
        downloadBtn.setAlignmentX(CENTER_ALIGNMENT);
        downloadBtn.addActionListener(e -> saveTranscript());

        JPanel card = ERPComponents.createCard();
        card.add(downloadBtn, BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    private void saveTranscript() {
        try {
            edu.univ.erp.api.student.StudentAPI studentAPI = new edu.univ.erp.api.student.StudentAPI();
            edu.univ.erp.domain.Student profile = studentAPI.getProfile();
            List<edu.univ.erp.domain.Grade> grades = studentAPI.getMyGrades();
            StudentGradeCalculator.GradeReport report = StudentGradeCalculator.buildReport(grades);
            List<CourseResult> courses = report.getCourses();

            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No finalized grades available yet.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Map<String, List<CourseResult>> bySemester = report.getCoursesBySemester();
            List<String> semesterOrder = report.getSemesterOrder();
            double cgpa = report.getCgpa();

            String filePath = System.getProperty("user.home") + "/Desktop/Transcript.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));

            writer.writeNext(new String[]{"University ERP - Transcript"});
            if (profile != null && profile.getFullName() != null) {
                writer.writeNext(new String[]{"Student: " + profile.getFullName()});
            }
            writer.writeNext(new String[]{"Generated: " + new Date()});
            writer.writeNext(new String[]{"Current CGPA: " + formatGpa(cgpa)});
            writer.writeNext(new String[]{});

            for (String semester : semesterOrder) {
                List<CourseResult> semesterCourses = bySemester.get(semester);
                writer.writeNext(new String[]{semester});
                writer.writeNext(new String[]{"Course Code", "Course Title", "Credits", "Final %", "Grade"});
                for (CourseResult course : semesterCourses) {
                    writer.writeNext(new String[]{
                            valueOr(course.getCourseCode()),
                            valueOr(course.getCourseTitle()),
                            course.getCredits() != null ? course.getCredits().toString() : "-",
                            course.getFinalPercent() != null ? String.format("%.2f", course.getFinalPercent()) : "-",
                            course.getLetterGrade() != null ? course.getLetterGrade() : "-"
                    });
                }
                double sgpa = report.getSgpa(semester);
                writer.writeNext(new String[]{"", "Semester SGPA", formatGpa(sgpa)});
                writer.writeNext(new String[]{});
            }

            writer.close();

            JOptionPane.showMessageDialog(this,
                    "✔ Transcript saved to Desktop as Transcript.csv",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error saving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error loading transcript data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String valueOr(String text) {
        return text != null ? text : "-";
    }

    private static String formatGpa(double value) {
        return Double.isNaN(value) ? "-" : String.format("%.2f", value);
    }
}
