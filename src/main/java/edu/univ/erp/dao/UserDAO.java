package edu.univ.erp.dao;

import edu.univ.erp.domain.User;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.util.DbConnectionAuth;
import edu.univ.erp.util.Logger;

import javax.sql.DataSource;
import java.sql.*;

public class UserDAO {
    private final DataSource ds;
    private static final Logger logger = Logger.getLogger(UserDAO.class);

    public UserDAO() {
        this.ds = DbConnectionAuth.getDataSource();
    }

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        String sql = "SELECT user_id, username, password_hash, role, status, last_login FROM users_auth WHERE username = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = map(rs);
                    logger.debug("Found user: {}", username);
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user: {}", username, e);
            throw new DataAccessException("Error finding user: " + username, e);
        }
        logger.debug("User not found: {}", username);
        return null;
    }

    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                logger.debug("Updated last login for user: {}", userId);
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Error updating last login for user: {}", userId, e);
            return false;
        }
    }

    public boolean create(User user, String passwordHash) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        
        String sql = "INSERT INTO users_auth (username, password_hash, role, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, passwordHash);
            ps.setString(3, user.getRole());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                logger.warn("Failed to create user: {}", user.getUsername());
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                    logger.info("Created user: {} with ID: {}", user.getUsername(), user.getId());
                }
            }
            return true;
        } catch (SQLException e) {
            logger.error("Error creating user: {}", user.getUsername(), e);
            if (e.getSQLState().equals("23000")) { // Duplicate key
                throw new DataAccessException("Username already exists: " + user.getUsername(), e);
            }
            throw new DataAccessException("Error creating user: " + user.getUsername(), e);
        }
    }

    public boolean updatePassword(int userId, String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                logger.info("Updated password for user: {}", userId);
            } else {
                logger.warn("User not found for password update: {}", userId);
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Error updating password for user: {}", userId, e);
            throw new DataAccessException("Error updating password for user: " + userId, e);
        }
    }

    public boolean exists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM users_auth WHERE username = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking if user exists: {}", username, e);
            throw new DataAccessException("Error checking if user exists: " + username, e);
        }
        return false;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password_hash")); // Store hash, not plain password
        u.setRole(rs.getString("role"));
        return u;
    }
}


