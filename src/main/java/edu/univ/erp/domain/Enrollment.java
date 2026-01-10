package edu.univ.erp.domain;

import java.sql.Timestamp;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status;
    private Timestamp enrollmentDate;
    private Timestamp dropDeadline;
    private String courseCode;
    private String courseTitle;
    private String sectionInfo;
    private String studentName;
    private String instructorName;
    private Integer capacity;
    private Integer enrolledCount;
    private String sectionDay;
    private String sectionTime;
    private String sectionRoom;

    public Enrollment() {}

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Timestamp enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public Timestamp getDropDeadline() { return dropDeadline; }
    public void setDropDeadline(Timestamp dropDeadline) { this.dropDeadline = dropDeadline; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getSectionInfo() { return sectionInfo; }
    public void setSectionInfo(String sectionInfo) { this.sectionInfo = sectionInfo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(Integer enrolledCount) { this.enrolledCount = enrolledCount; }

    public String getSectionDay() { return sectionDay; }
    public void setSectionDay(String sectionDay) { this.sectionDay = sectionDay; }

    public String getSectionTime() { return sectionTime; }
    public void setSectionTime(String sectionTime) { this.sectionTime = sectionTime; }

    public String getSectionRoom() { return sectionRoom; }
    public void setSectionRoom(String sectionRoom) { this.sectionRoom = sectionRoom; }
}

