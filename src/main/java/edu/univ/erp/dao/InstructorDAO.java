package edu.univ.erp.dao;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.DbConnection;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {
    private final DataSource ds;

    public InstructorDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public Instructor findByUserId(int userId) {
        String sql = "SELECT user_id, department, full_name, email FROM instructors WHERE user_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding instructor: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean create(Instructor instructor) {
        String sql = "INSERT INTO instructors (user_id, department, full_name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructor.getUserId());
            ps.setString(2, instructor.getDepartment());
            ps.setString(3, instructor.getFullName());
            ps.setString(4, instructor.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating instructor: " + e.getMessage(), e);
        }
    }

    public List<Instructor> findAll() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT user_id, department, full_name, email FROM instructors ORDER BY full_name";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                instructors.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all instructors: " + e.getMessage(), e);
        }
        return instructors;
    }

    private Instructor map(ResultSet rs) throws SQLException {
        Instructor i = new Instructor();
        i.setUserId(rs.getInt("user_id"));
        i.setDepartment(rs.getString("department"));
        i.setFullName(rs.getString("full_name"));
        i.setEmail(rs.getString("email"));
        return i;
    }
}

