package edu.univ.erp.dao;

import edu.univ.erp.domain.Student;
import edu.univ.erp.util.DbConnection;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final DataSource ds;

    public StudentDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public Student findByUserId(int userId) {
        String sql = "SELECT user_id, roll_no, program, year, full_name, email FROM students WHERE user_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding student: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean create(Student student) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year, full_name, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, student.getUserId());
            ps.setString(2, student.getRollNo());
            ps.setString(3, student.getProgram());
            ps.setInt(4, student.getYear());
            ps.setString(5, student.getFullName());
            ps.setString(6, student.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating student: " + e.getMessage(), e);
        }
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT user_id, roll_no, program, year, full_name, email FROM students ORDER BY roll_no";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all students: " + e.getMessage(), e);
        }
        return students;
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setUserId(rs.getInt("user_id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setProgram(rs.getString("program"));
        s.setYear(rs.getInt("year"));
        s.setFullName(rs.getString("full_name"));
        s.setEmail(rs.getString("email"));
        return s;
    }
}

