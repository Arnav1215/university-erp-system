package edu.univ.erp.domain;

public class Instructor {
    private int userId;
    private String department;
    private String fullName;
    private String email;

    public Instructor() {}

    public Instructor(int userId, String department, String fullName, String email) {
        this.userId = userId;
        this.department = department;
        this.fullName = fullName;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return fullName != null ? fullName : "Instructor " + userId;
    }
}

