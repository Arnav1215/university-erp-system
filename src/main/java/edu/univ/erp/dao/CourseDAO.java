package edu.univ.erp.dao;

import edu.univ.erp.domain.Course;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.util.DbConnection;
import edu.univ.erp.util.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private final DataSource ds;
    private static final Logger logger = Logger.getLogger(CourseDAO.class);

    public CourseDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public Course findById(int courseId) {
        String sql = "SELECT course_id, code, title, credits, description FROM courses WHERE course_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding course by ID: {}", courseId, e);
            throw new DataAccessException("Error finding course: " + courseId, e);
        }
        return null;
    }

    public Course findByCode(String code) {
        String sql = "SELECT course_id, code, title, credits, description FROM courses WHERE code = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding course by code: {}", code, e);
            throw new DataAccessException("Error finding course: " + code, e);
        }
        return null;
    }

    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, code, title, credits, description FROM courses ORDER BY code";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.add(map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all courses", e);
            throw new DataAccessException("Error finding all courses", e);
        }
        return courses;
    }

    public boolean create(Course course) {
        String sql = "INSERT INTO courses (code, title, credits, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getDescription());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) course.setCourseId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error creating course: {}", course != null ? course.getCode() : "null", e);
            if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                throw new DataAccessException("Course code already exists: " + (course != null ? course.getCode() : ""), e);
            }
            throw new DataAccessException("Error creating course", e);
        }
    }

    public boolean update(int courseId, String code, String title, int credits, String description) {
        String sql = "UPDATE courses SET code = ?, title = ?, credits = ?, description = ? WHERE course_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.setString(4, description);
            ps.setInt(5, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating course: {}", courseId, e);
            throw new DataAccessException("Error updating course: " + courseId, e);
        }
    }

    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting course: {}", courseId, e);
            throw new DataAccessException("Error deleting course: " + courseId, e);
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCode(rs.getString("code"));
        c.setTitle(rs.getString("title"));
        c.setCredits(rs.getInt("credits"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}

