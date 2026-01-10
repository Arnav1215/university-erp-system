package edu.univ.erp.auth;

import edu.univ.erp.domain.User;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;

public class Session {
    private static Session instance;
    private User currentUser;
    private Student studentProfile;
    private Instructor instructorProfile;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(User user) {
        this.currentUser = user;
        this.studentProfile = null;
        this.instructorProfile = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : 0;
    }

    public String getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getRole());
    }

    public boolean isInstructor() {
        return "INSTRUCTOR".equalsIgnoreCase(getRole());
    }

    public boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(getRole());
    }

    public void setStudentProfile(Student student) {
        this.studentProfile = student;
    }

    public Student getStudentProfile() {
        return studentProfile;
    }

    public void setInstructorProfile(Instructor instructor) {
        this.instructorProfile = instructor;
    }

    public Instructor getInstructorProfile() {
        return instructorProfile;
    }

    public void clear() {
        this.currentUser = null;
        this.studentProfile = null;
        this.instructorProfile = null;
    }
}

