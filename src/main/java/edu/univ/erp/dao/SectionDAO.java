package edu.univ.erp.dao;

import edu.univ.erp.domain.Section;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.util.DbConnection;
import edu.univ.erp.util.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {
    private final DataSource ds;
    private static final Logger logger = Logger.getLogger(SectionDAO.class);
    private static final long ADD_WINDOW_MILLIS = Duration.ofDays(7).toMillis();

    public SectionDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public Section findById(int sectionId) {
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day, s.time, s.room, " +
                     "s.capacity, s.semester, s.year, s.enrolled_count, s.created_at, " +
                     "c.code as course_code, c.title as course_title, " +
                     "i.full_name as instructor_name " +
                     "FROM sections s " +
                     "LEFT JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                     "WHERE s.section_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding section: {}", sectionId, e);
            throw new DataAccessException("Error finding section: " + sectionId, e);
        }
        return null;
    }

    public List<Section> findAll() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day, s.time, s.room, " +
                     "s.capacity, s.semester, s.year, s.enrolled_count, s.created_at, " +
                     "c.code as course_code, c.title as course_title, " +
                     "i.full_name as instructor_name " +
                     "FROM sections s " +
                     "LEFT JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                     "ORDER BY s.year DESC, s.semester, c.code";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sections.add(map(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all sections", e);
            throw new DataAccessException("Error finding all sections", e);
        }
        return sections;
    }

    public List<Section> findBySemesterAndYear(String semester, int year) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day, s.time, s.room, " +
                     "s.capacity, s.semester, s.year, s.enrolled_count, s.created_at, " +
                     "c.code as course_code, c.title as course_title, " +
                     "i.full_name as instructor_name " +
                     "FROM sections s " +
                     "LEFT JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                     "WHERE s.semester = ? AND s.year = ? " +
                     "ORDER BY c.code";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, semester);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sections.add(map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding sections for semester {} and year {}", semester, year, e);
            throw new DataAccessException("Error finding sections", e);
        }
        return sections;
    }

    public List<Section> findByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, s.course_id, s.instructor_id, s.day, s.time, s.room, " +
                     "s.capacity, s.semester, s.year, s.enrolled_count, s.created_at, " +
                     "c.code as course_code, c.title as course_title, " +
                     "i.full_name as instructor_name " +
                     "FROM sections s " +
                     "LEFT JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.user_id " +
                     "WHERE s.instructor_id = ? " +
                     "ORDER BY s.year DESC, s.semester, c.code";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sections.add(map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding instructor sections: {}", instructorId, e);
            throw new DataAccessException("Error finding instructor sections: " + instructorId, e);
        }
        return sections;
    }

    public boolean create(Section section) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day, time, room, capacity, semester, year) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, section.getCourseId());
            if (section.getInstructorId() != null) {
                ps.setInt(2, section.getInstructorId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, section.getDay());
            ps.setString(4, section.getTime());
            ps.setString(5, section.getRoom());
            ps.setInt(6, section.getCapacity());
            ps.setString(7, section.getSemester());
            ps.setInt(8, section.getYear());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) section.setSectionId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error creating section for course {}", section != null ? section.getCourseId() : "null", e);
            throw new DataAccessException("Error creating section", e);
        }
    }

    public boolean updateInstructor(int sectionId, Integer instructorId) {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (instructorId != null) {
                ps.setInt(1, instructorId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating section instructor for section {}", sectionId, e);
            throw new DataAccessException("Error updating section instructor", e);
        }
    }

    public boolean incrementEnrolledCount(int sectionId) {
        
        String sql = "UPDATE sections SET enrolled_count = enrolled_count + 1 " +
                     "WHERE section_id = ? AND enrolled_count < capacity";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                logger.warn("Failed to increment enrollment count for section {} - may be at capacity", sectionId);
                return false;
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error incrementing enrollment count for section {}", sectionId, e);
            throw new DataAccessException("Error incrementing enrollment count", e);
        }
    }

    public boolean decrementEnrolledCount(int sectionId) {
        String sql = "UPDATE sections SET enrolled_count = enrolled_count - 1 WHERE section_id = ? AND enrolled_count > 0";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error decrementing enrollment count for section {}", sectionId, e);
            throw new DataAccessException("Error decrementing enrollment count", e);
        }
    }

    public java.util.List<String> getDistinctSemesters() {
        java.util.List<String> semesters = new ArrayList<>();
        String sql = "SELECT DISTINCT semester FROM sections ORDER BY semester";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                semesters.add(rs.getString("semester"));
            }
        } catch (SQLException e) {
            logger.error("Error getting distinct semesters", e);
            throw new DataAccessException("Error getting distinct semesters", e);
        }
        return semesters;
    }

    public java.util.List<Integer> getDistinctYears() {
        java.util.List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT year FROM sections ORDER BY year DESC";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            logger.error("Error getting distinct years", e);
            throw new DataAccessException("Error getting distinct years", e);
        }
        return years;
    }

    private Section map(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setSectionId(rs.getInt("section_id"));
        s.setCourseId(rs.getInt("course_id"));
        int instructorId = rs.getInt("instructor_id");
        if (!rs.wasNull()) {
            s.setInstructorId(instructorId);
        }
        s.setDay(rs.getString("day"));
        s.setTime(rs.getString("time"));
        s.setRoom(rs.getString("room"));
        s.setCapacity(rs.getInt("capacity"));
        s.setSemester(rs.getString("semester"));
        s.setYear(rs.getInt("year"));
        s.setEnrolledCount(rs.getInt("enrolled_count"));
        s.setCourseCode(rs.getString("course_code"));
        s.setCourseTitle(rs.getString("course_title"));
        s.setInstructorName(rs.getString("instructor_name"));
        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (!rs.wasNull()) {
                s.setCreatedAt(createdAt);
            } else {
                
                
                s.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                logger.warn("Section {} has null created_at, defaulting to current time", s.getSectionId());
            }
        } catch (SQLException e) {
            
            s.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            logger.warn("Error reading created_at for section {}, defaulting to current time: {}", s.getSectionId(), e.getMessage());
        }
        return s;
    }

    public Timestamp getCreatedAt(int sectionId) {
        String sql = "SELECT created_at FROM sections WHERE section_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("created_at");
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching created_at for section {}", sectionId, e);
            throw new DataAccessException("Unable to fetch section creation time", e);
        }
        return null;
    }

    public boolean canEnroll(int sectionId) {
        Timestamp createdAt = getCreatedAt(sectionId);
        if (createdAt == null) {
            logger.warn("Section {} has no created_at timestamp; allowing enrollment by default", sectionId);
            return true;
        }
        long deadline = createdAt.getTime() + ADD_WINDOW_MILLIS;
        return System.currentTimeMillis() <= deadline;
    }

    public boolean update(int sectionId, int courseId, Integer instructorId, String day, String time, String room, int capacity, String semester, int year) {
        String sql = "UPDATE sections SET course_id = ?, instructor_id = ?, day = ?, time = ?, room = ?, capacity = ?, semester = ?, year = ? WHERE section_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            if (instructorId != null) {
                ps.setInt(2, instructorId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, day);
            ps.setString(4, time);
            ps.setString(5, room);
            ps.setInt(6, capacity);
            ps.setString(7, semester);
            ps.setInt(8, year);
            ps.setInt(9, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating section: {}", sectionId, e);
            throw new DataAccessException("Error updating section: " + sectionId, e);
        }
    }

    public boolean delete(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting section: {}", sectionId, e);
            throw new DataAccessException("Error deleting section: " + sectionId, e);
        }
    }
}

