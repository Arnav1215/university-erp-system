package edu.univ.erp.dao;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.util.DbConnection;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    private final DataSource ds;

    public GradeDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public List<Grade> findByEnrollment(int enrollmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT grade_id, enrollment_id, component, score, max_score, final_grade " +
                     "FROM grades WHERE enrollment_id = ? ORDER BY component";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding grades: " + e.getMessage(), e);
        }
        return grades;
    }

    public List<Grade> findBySection(int sectionId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.grade_id, g.enrollment_id, g.component, g.score, g.max_score, g.final_grade, " +
                     "s.full_name as student_name, c.code as course_code " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN students s ON e.student_id = s.user_id " +
                     "JOIN sections sec ON e.section_id = sec.section_id " +
                     "JOIN courses c ON sec.course_id = c.course_id " +
                     "WHERE e.section_id = ? AND e.status = 'ENROLLED' " +
                     "ORDER BY s.full_name, g.component";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding section grades: " + e.getMessage(), e);
        }
        return grades;
    }

    public boolean saveOrUpdate(Grade grade) {
        String sql = "INSERT INTO grades (enrollment_id, component, score, max_score, final_grade) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = ?, max_score = ?, final_grade = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grade.getEnrollmentId());
            ps.setString(2, grade.getComponent());
            ps.setObject(3, grade.getScore());
            ps.setObject(4, grade.getMaxScore());
            ps.setObject(5, grade.getFinalGrade());
            ps.setObject(6, grade.getScore());
            ps.setObject(7, grade.getMaxScore());
            ps.setObject(8, grade.getFinalGrade());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            
            String insertSql = "INSERT INTO grades (enrollment_id, component, score, max_score, final_grade) " +
                              "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = ds.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, grade.getEnrollmentId());
                ps.setString(2, grade.getComponent());
                ps.setObject(3, grade.getScore());
                ps.setObject(4, grade.getMaxScore());
                ps.setObject(5, grade.getFinalGrade());
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                throw new RuntimeException("Error saving grade: " + e2.getMessage(), e2);
            }
        }
    }

    public boolean updateFinalGrade(int enrollmentId, double finalGrade) {
        String sql = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ? AND component = 'FINAL'";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, finalGrade);
            ps.setInt(2, enrollmentId);
            if (ps.executeUpdate() == 0) {
                
                String insertSql = "INSERT INTO grades (enrollment_id, component, final_grade) VALUES (?, 'FINAL', ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                    ps2.setInt(1, enrollmentId);
                    ps2.setDouble(2, finalGrade);
                    return ps2.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating final grade: " + e.getMessage(), e);
        }
    }

    public List<Grade> findByStudent(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.grade_id, g.enrollment_id, g.component, g.score, g.max_score, g.final_grade, " +
                     "c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                     "s.semester, s.year, e.section_id " +
                     "FROM grades g " +
                     "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
                     "ORDER BY c.code, g.component";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding student grades: " + e.getMessage(), e);
        }
        return grades;
    }

    private Grade map(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setGradeId(rs.getInt("grade_id"));
        g.setEnrollmentId(rs.getInt("enrollment_id"));
        g.setComponent(rs.getString("component"));
        Object score = rs.getObject("score");
        if (score != null) g.setScore(((Number) score).doubleValue());
        Object maxScore = rs.getObject("max_score");
        if (maxScore != null) g.setMaxScore(((Number) maxScore).doubleValue());
        Object finalGrade = rs.getObject("final_grade");
        if (finalGrade != null) g.setFinalGrade(((Number) finalGrade).doubleValue());
        
        try {
            g.setStudentName(rs.getString("student_name"));
        } catch (SQLException ignored) {}
        try {
            g.setCourseCode(rs.getString("course_code"));
        } catch (SQLException ignored) {}
        try {
            g.setCourseTitle(rs.getString("course_title"));
        } catch (SQLException ignored) {}
        try {
            int credits = rs.getInt("course_credits");
            if (!rs.wasNull()) g.setCourseCredits(credits);
        } catch (SQLException ignored) {}
        try {
            g.setSemester(rs.getString("semester"));
        } catch (SQLException ignored) {}
        try {
            int year = rs.getInt("year");
            if (!rs.wasNull()) g.setYear(year);
        } catch (SQLException ignored) {}
        try {
            int sectionId = rs.getInt("section_id");
            if (!rs.wasNull()) g.setSectionId(sectionId);
        } catch (SQLException ignored) {}
        
        return g;
    }
}

