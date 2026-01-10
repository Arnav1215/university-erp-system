package edu.univ.erp.domain;

import java.sql.Timestamp;

public class Settings {
    private String settingKey;
    private String settingValue;
    private Timestamp updatedAt;

    public Settings() {}

    public Settings(String settingKey, String settingValue) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }

    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isMaintenanceMode() {
        return "maintenance_mode".equals(settingKey) && "true".equalsIgnoreCase(settingValue);
    }
}

