package edu.univ.erp.dao;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.util.DbConnection;
import edu.univ.erp.util.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private final DataSource ds;
    private static final Logger logger = Logger.getLogger(EnrollmentDAO.class);
    private static final long DROP_WINDOW_MILLIS = Duration.ofDays(7).toMillis();

    public EnrollmentDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public Enrollment findById(int enrollmentId) {
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrollment_date, e.drop_deadline, " +
                     "c.code as course_code, c.title as course_title " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE e.enrollment_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding enrollment: {}", enrollmentId, e);
            throw new DataAccessException("Error finding enrollment: " + enrollmentId, e);
        }
        return null;
    }

    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrollment_date, e.drop_deadline, " +
                     "c.code as course_code, c.title as course_title, " +
                     "CONCAT(sec.day, ' ', sec.time, ' ', sec.room) as section_info, " +
                     "sec.day as section_day, sec.time as section_time, sec.room as section_room, " +
                     "sec.capacity, sec.enrolled_count, sec.created_at as section_created_at, " +
                     "i.full_name as instructor_name " +
                     "FROM enrollments e " +
                     "JOIN sections sec ON e.section_id = sec.section_id " +
                     "JOIN courses c ON sec.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON sec.instructor_id = i.user_id " +
                     "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
                     "ORDER BY e.enrollment_date DESC";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapWithSectionCreatedAt(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding student enrollments: {}", studentId, e);
            throw new DataAccessException("Error finding student enrollments: " + studentId, e);
        }
        return enrollments;
    }

    public List<Enrollment> findBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrollment_date, e.drop_deadline, " +
                     "c.code as course_code, c.title as course_title, " +
                     "sec.created_at as section_created_at, " +
                     "stu.full_name as student_name " +
                     "FROM enrollments e " +
                     "JOIN sections sec ON e.section_id = sec.section_id " +
                     "JOIN courses c ON sec.course_id = c.course_id " +
                     "JOIN students stu ON e.student_id = stu.user_id " +
                     "WHERE e.section_id = ? AND e.status = 'ENROLLED' " +
                     "ORDER BY stu.full_name";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapWithSectionCreatedAt(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding section enrollments: {}", sectionId, e);
            throw new DataAccessException("Error finding section enrollments: " + sectionId, e);
        }
        return enrollments;
    }

    public boolean exists(int studentId, int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ENROLLED'";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking enrollment for student {} and section {}", studentId, sectionId, e);
            throw new DataAccessException("Error checking enrollment", e);
        }
        return false;
    }

    public boolean create(Enrollment enrollment) {
        
        
        String getSectionSql = "SELECT created_at FROM sections WHERE section_id = ?";
        Timestamp sectionCreatedAt = null;
        
        try (Connection conn = ds.getConnection();
             PreparedStatement psSection = conn.prepareStatement(getSectionSql)) {
            psSection.setInt(1, enrollment.getSectionId());
            try (ResultSet rs = psSection.executeQuery()) {
                if (rs.next()) {
                    sectionCreatedAt = rs.getTimestamp("created_at");
                    if (rs.wasNull()) {
                        sectionCreatedAt = null;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching section.created_at for section {}", enrollment.getSectionId(), e);
            throw new DataAccessException("Error fetching section creation time", e);
        }
        
        
        Timestamp dropDeadline;
        if (sectionCreatedAt != null) {
            
            dropDeadline = new Timestamp(sectionCreatedAt.getTime() + DROP_WINDOW_MILLIS);
            logger.info("Calculated drop_deadline from section.created_at: {} + 7 days = {}", sectionCreatedAt, dropDeadline);
        } else {
            
            dropDeadline = new Timestamp(System.currentTimeMillis() + DROP_WINDOW_MILLIS);
            logger.warn("Section {} has null created_at, using current time for drop_deadline", enrollment.getSectionId());
        }
        
        String sql = "INSERT INTO enrollments (student_id, section_id, status, drop_deadline) VALUES (?, ?, 'ENROLLED', ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getSectionId());
            ps.setTimestamp(3, dropDeadline);
            
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) enrollment.setEnrollmentId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error creating enrollment for student {} and section {}", 
                enrollment != null ? enrollment.getStudentId() : "null", 
                enrollment != null ? enrollment.getSectionId() : "null", e);
            if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                throw new DataAccessException("Duplicate enrollment: Student is already enrolled in this section", e);
            }
            throw new DataAccessException("Error creating enrollment", e);
        }
    }

    public boolean drop(int enrollmentId) {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE enrollment_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            boolean dropped = ps.executeUpdate() > 0;
            if (dropped) {
                logger.info("Enrollment {} dropped successfully", enrollmentId);
            } else {
                logger.warn("Enrollment {} not found for drop", enrollmentId);
            }
            return dropped;
        } catch (SQLException e) {
            logger.error("Error dropping enrollment: {}", enrollmentId, e);
            throw new DataAccessException("Error dropping enrollment: " + enrollmentId, e);
        }
    }

    public boolean deletePermanently(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            boolean deleted = ps.executeUpdate() > 0;
            if (deleted) {
                logger.info("Enrollment {} permanently deleted", enrollmentId);
            } else {
                logger.warn("Enrollment {} not found for deletion", enrollmentId);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Error deleting enrollment: {}", enrollmentId, e);
            throw new DataAccessException("Error deleting enrollment: " + enrollmentId, e);
        }
    }

    public Enrollment findByStudentAndSection(int studentId, int sectionId) {
        
        
        String sql = "SELECT e.enrollment_id, e.student_id, e.section_id, e.status, e.enrollment_date, e.drop_deadline, " +
                     "c.code as course_code, c.title as course_title, " +
                     "s.created_at as section_created_at " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE e.student_id = ? AND e.section_id = ? " +
                     "ORDER BY e.enrollment_date DESC LIMIT 1";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapWithSectionCreatedAt(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding enrollment for student {} and section {}", studentId, sectionId, e);
            throw new DataAccessException("Error finding enrollment for student " + studentId + " and section " + sectionId, e);
        }
        return null;
    }

    private Enrollment map(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setSectionId(rs.getInt("section_id"));
        e.setStatus(rs.getString("status"));
        e.setEnrollmentDate(rs.getTimestamp("enrollment_date"));
        
        
        Timestamp dropDeadline = null;
        try {
            dropDeadline = rs.getTimestamp("drop_deadline");
            if (rs.wasNull()) {
                dropDeadline = null;
            }
        } catch (SQLException ignored) {}
        
        
        if (dropDeadline != null) {
            e.setDropDeadline(dropDeadline);
        } else {
            
            try {
                Timestamp sectionCreatedAt = rs.getTimestamp("section_created_at");
                if (sectionCreatedAt != null && !rs.wasNull()) {
                    
                    dropDeadline = new Timestamp(sectionCreatedAt.getTime() + DROP_WINDOW_MILLIS);
                    e.setDropDeadline(dropDeadline);
                    logger.debug("Recalculated drop_deadline for enrollment {} from section.created_at", e.getEnrollmentId());
                } else if (e.getEnrollmentDate() != null) {
                    
                    dropDeadline = new Timestamp(e.getEnrollmentDate().getTime() + DROP_WINDOW_MILLIS);
                    e.setDropDeadline(dropDeadline);
                    logger.warn("Enrollment {} has null drop_deadline and section.created_at, calculated from enrollment_date", e.getEnrollmentId());
                }
            } catch (SQLException ex) {
                
                if (e.getEnrollmentDate() != null) {
                    dropDeadline = new Timestamp(e.getEnrollmentDate().getTime() + DROP_WINDOW_MILLIS);
                    e.setDropDeadline(dropDeadline);
                    logger.warn("Error reading drop_deadline for enrollment {}, calculated from enrollment_date: {}", e.getEnrollmentId(), ex.getMessage());
                }
            }
        }
        
        e.setCourseCode(rs.getString("course_code"));
        e.setCourseTitle(rs.getString("course_title"));
        try {
            e.setSectionInfo(rs.getString("section_info"));
        } catch (SQLException ignored) {}
        try {
            e.setStudentName(rs.getString("student_name"));
        } catch (SQLException ignored) {}
        try {
            e.setInstructorName(rs.getString("instructor_name"));
        } catch (SQLException ignored) {}
        try {
            e.setSectionDay(rs.getString("section_day"));
            e.setSectionTime(rs.getString("section_time"));
            e.setSectionRoom(rs.getString("section_room"));
        } catch (SQLException ignored) {}
        try {
            int capacity = rs.getInt("capacity");
            if (!rs.wasNull()) e.setCapacity(capacity);
        } catch (SQLException ignored) {}
        try {
            int enrolled = rs.getInt("enrolled_count");
            if (!rs.wasNull()) e.setEnrolledCount(enrolled);
        } catch (SQLException ignored) {}
        return e;
    }

    private Enrollment mapWithSectionCreatedAt(ResultSet rs) throws SQLException {
        Enrollment e = map(rs);
        
        
        try {
            Timestamp sectionCreatedAt = rs.getTimestamp("section_created_at");
            if (sectionCreatedAt != null && !rs.wasNull()) {
                
                Timestamp correctDropDeadline = new Timestamp(sectionCreatedAt.getTime() + DROP_WINDOW_MILLIS);
                
                
                
                if (e.getDropDeadline() == null || 
                    Math.abs(e.getDropDeadline().getTime() - correctDropDeadline.getTime()) > 60000) {
                    e.setDropDeadline(correctDropDeadline);
                    logger.debug("Corrected drop_deadline for enrollment {} from section.created_at", e.getEnrollmentId());
                }
            }
        } catch (SQLException ignored) {
            
        }
        
        return e;
    }

    public boolean canDrop(int enrollmentId) {
        
        String sql = "SELECT e.drop_deadline, s.created_at as section_created_at " +
                     "FROM enrollments e " +
                     "JOIN sections s ON e.section_id = s.section_id " +
                     "WHERE e.enrollment_id = ? AND e.status = 'ENROLLED'";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp deadline = rs.getTimestamp("drop_deadline");
                    Timestamp sectionCreatedAt = rs.getTimestamp("section_created_at");
                    
                    
                    if (sectionCreatedAt != null && !rs.wasNull()) {
                        deadline = new Timestamp(sectionCreatedAt.getTime() + DROP_WINDOW_MILLIS);
                        logger.debug("Recalculated drop deadline for enrollment {} from section.created_at", enrollmentId);
                    }
                    
                    if (deadline == null) {
                        return true; 
                    }
                    return System.currentTimeMillis() <= deadline.getTime();
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking drop window for enrollment {}", enrollmentId, e);
            throw new DataAccessException("Unable to evaluate drop window", e);
        }
        return false;
    }
}

