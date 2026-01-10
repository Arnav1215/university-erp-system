package edu.univ.erp.domain;

public class Course {
    private int courseId;
    private String code;
    private String title;
    private int credits;
    private String description;

    public Course() {}

    public Course(int courseId, String code, String title, int credits, String description) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.description = description;
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return code + " - " + title;
    }
}

