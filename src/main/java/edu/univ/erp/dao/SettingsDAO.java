package edu.univ.erp.dao;

import edu.univ.erp.domain.Settings;
import edu.univ.erp.util.DbConnection;

import javax.sql.DataSource;
import java.sql.*;

public class SettingsDAO {
    private final DataSource ds;

    public SettingsDAO() {
        this.ds = DbConnection.getDataSource();
    }

    public Settings findByKey(String key) {
        String sql = "SELECT setting_key, setting_value, updated_at FROM settings WHERE setting_key = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding setting: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean update(String key, String value) {
        String sql = "UPDATE settings SET setting_value = ?, updated_at = NOW() WHERE setting_key = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, key);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating setting: " + e.getMessage(), e);
        }
    }

    public boolean create(String key, String value) {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating setting: " + e.getMessage(), e);
        }
    }

    public boolean isMaintenanceMode() {
        Settings setting = findByKey("maintenance_mode");
        return setting != null && "true".equalsIgnoreCase(setting.getSettingValue());
    }

    public boolean setMaintenanceMode(boolean enabled) {
        String value = enabled ? "true" : "false";
        Settings existing = findByKey("maintenance_mode");
        if (existing != null) {
            return update("maintenance_mode", value);
        } else {
            return create("maintenance_mode", value);
        }
    }

    private Settings map(ResultSet rs) throws SQLException {
        Settings s = new Settings();
        s.setSettingKey(rs.getString("setting_key"));
        s.setSettingValue(rs.getString("setting_value"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}

