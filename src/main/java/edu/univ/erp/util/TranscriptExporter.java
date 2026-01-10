package edu.univ.erp.util;

import com.opencsv.CSVWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.dao.GradeDAO;
import edu.univ.erp.util.StudentGradeCalculator.CourseResult;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class TranscriptExporter {
    private final GradeDAO gradeDAO = new GradeDAO();

    public String exportToCSV(int studentId, java.util.List<Enrollment> enrollments, String studentName) throws IOException {
        String filename = "transcript_" + studentId + "_" + System.currentTimeMillis() + ".csv";
        List<Grade> grades = gradeDAO.findByStudent(studentId);
        StudentGradeCalculator.GradeReport report = StudentGradeCalculator.buildReport(grades);
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(new String[]{"University ERP - Transcript"});
            writer.writeNext(new String[]{"Student: " + studentName});
            writer.writeNext(new String[]{"Generated: " + new Date()});
            writer.writeNext(new String[]{"Current CGPA: " + formatGpa(report.getCgpa())});
            writer.writeNext(new String[]{}); // Empty line

            if (report.getCourses().isEmpty()) {
                writer.writeNext(new String[]{"No grades available"});
                return filename;
            }

            for (String semester : report.getSemesterOrder()) {
                List<CourseResult> semesterCourses = report.getCoursesBySemester().get(semester);
                writer.writeNext(new String[]{semester});
                writer.writeNext(new String[]{"Course Code", "Course Title", "Credits", "Final %", "Grade"});
                for (CourseResult course : semesterCourses) {
                    writer.writeNext(new String[]{
                        safe(course.getCourseCode()),
                        safe(course.getCourseTitle()),
                        course.getCredits() != null ? course.getCredits().toString() : "-",
                        course.getFinalPercent() != null ? String.format("%.2f", course.getFinalPercent()) : "-",
                        course.getLetterGrade() != null ? course.getLetterGrade() : "-"
                    });
                }
                writer.writeNext(new String[]{"", "Semester SGPA", formatGpa(report.getSgpa(semester))});
                writer.writeNext(new String[]{});
            }
        }
        
        return filename;
    }

    public String exportToPDF(int studentId, java.util.List<Enrollment> enrollments, String studentName) throws IOException, DocumentException {
        String filename = "transcript_" + studentId + "_" + System.currentTimeMillis() + ".pdf";
        Document document = new Document();
        PdfWriter.getInstance(document, new java.io.FileOutputStream(filename));
        document.open();
        List<Grade> grades = gradeDAO.findByStudent(studentId);
        StudentGradeCalculator.GradeReport report = StudentGradeCalculator.buildReport(grades);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("University ERP - Transcript", titleFont));
        document.add(new Paragraph("Student: " + studentName, normalFont));
        document.add(new Paragraph("Generated: " + new Date(), normalFont));
        document.add(new Paragraph("Current CGPA: " + formatGpa(report.getCgpa()), normalFont));
        document.add(new Paragraph("\n", normalFont));

        if (report.getCourses().isEmpty()) {
            document.add(new Paragraph("No grades available", normalFont));
        } else {
            for (String semester : report.getSemesterOrder()) {
                document.add(new Paragraph(semester, headerFont));
                com.lowagie.text.Table table = new com.lowagie.text.Table(5);
                table.setWidth(100);
                table.setPadding(5);

                table.addCell(new Phrase("Course Code", headerFont));
                table.addCell(new Phrase("Course Title", headerFont));
                table.addCell(new Phrase("Credits", headerFont));
                table.addCell(new Phrase("Final %", headerFont));
                table.addCell(new Phrase("Grade", headerFont));

                List<CourseResult> semesterCourses = report.getCoursesBySemester().get(semester);
                for (CourseResult course : semesterCourses) {
                    table.addCell(new Phrase(safe(course.getCourseCode()), normalFont));
                    table.addCell(new Phrase(safe(course.getCourseTitle()), normalFont));
                    table.addCell(new Phrase(course.getCredits() != null ? course.getCredits().toString() : "-", normalFont));
                    table.addCell(new Phrase(course.getFinalPercent() != null ? String.format("%.2f", course.getFinalPercent()) : "-", normalFont));
                    table.addCell(new Phrase(course.getLetterGrade() != null ? course.getLetterGrade() : "-", normalFont));
                }

                document.add(table);
                document.add(new Paragraph("SGPA: " + formatGpa(report.getSgpa(semester)), normalFont));
                document.add(new Paragraph("\n", normalFont));
            }
        }

        document.close();

        return filename;
    }

    private static String safe(String text) {
        return text != null ? text : "-";
    }

    private static String formatGpa(double value) {
        return Double.isNaN(value) ? "-" : String.format("%.2f", value);
    }
}

