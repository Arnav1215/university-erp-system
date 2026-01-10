package edu.univ.erp.api.student;

import edu.univ.erp.auth.Session;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.util.Logger;

import java.util.Collections;
import java.util.List;

public class StudentAPI {
    private final StudentService studentService = new StudentService();
    private static final Logger logger = Logger.getLogger(StudentAPI.class);

    public Student getProfile() {
        try {
            int userId = Session.getInstance().getUserId();
            return studentService.getProfile(userId);
        } catch (Exception e) {
            logger.error("Error getting student profile", e);
            return null;
        }
    }

    public List<Section> getAvailableSections(String semester, int year) {
        try {
            int userId = Session.getInstance().getUserId();
            return studentService.getAvailableSections(userId, semester, year);
        } catch (Exception e) {
            logger.error("Error getting available sections", e);
            return Collections.emptyList();
        }
    }

    public String registerForSection(int sectionId) {
        try {
            int userId = Session.getInstance().getUserId();
            if (!studentService.canEnroll(sectionId)) {
                return "Add window has expired for this section.";
            }
            return studentService.registerForSection(userId, sectionId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Registration error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            return "Unable to register. Please try again.";
        }
    }

    public String dropSection(int enrollmentId) {
        try {
            int userId = Session.getInstance().getUserId();
            if (!studentService.canDrop(enrollmentId)) {
                return "Drop deadline has passed for this enrollment.";
            }
            return studentService.dropSection(userId, enrollmentId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Drop section error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error during drop", e);
            return "Unable to drop section. Please try again.";
        }
    }

    public List<Enrollment> getMyEnrollments() {
        try {
            int userId = Session.getInstance().getUserId();
            return studentService.getEnrollments(userId);
        } catch (Exception e) {
            logger.error("Error getting enrollments", e);
            return Collections.emptyList();
        }
    }

    public List<Grade> getMyGrades() {
        try {
            int userId = Session.getInstance().getUserId();
            return studentService.getGrades(userId);
        } catch (Exception e) {
            logger.error("Error getting grades", e);
            return Collections.emptyList();
        }
    }

    public boolean canEnroll(int sectionId) {
        try {
            return studentService.canEnroll(sectionId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Cannot enroll section {}: {}", sectionId, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error evaluating enroll window", e);
            return false;
        }
    }

    public boolean canDrop(int enrollmentId) {
        try {
            return studentService.canDrop(enrollmentId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Cannot drop enrollment {}: {}", enrollmentId, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error evaluating drop window", e);
            return false;
        }
    }

    public void reloadStudentState() {
        try {
            int userId = Session.getInstance().getUserId();
            studentService.reloadStudentState(userId);
        } catch (Exception e) {
            logger.error("Error reloading student state", e);
        }
    }
}

