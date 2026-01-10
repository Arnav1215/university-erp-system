package edu.univ.erp.api.admin;

import edu.univ.erp.domain.*;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.util.Logger;

import java.util.Collections;
import java.util.List;

public class AdminAPI {
    private final AdminService adminService = new AdminService();
    private static final Logger logger = Logger.getLogger(AdminAPI.class);

    public String createUser(User user, String password, String profileType, Object profile) {
        try {
            return adminService.createUser(user, password, profileType, profile);
        } catch (ValidationException | BusinessException e) {
            logger.warn("User creation error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error creating user", e);
            return "Unable to create user. Please try again.";
        }
    }

    public String createCourse(Course course) {
        try {
            return adminService.createCourse(course);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Course creation error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error creating course", e);
            return "Unable to create course. Please try again.";
        }
    }

    public String createSection(Section section) {
        try {
            return adminService.createSection(section);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section creation error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error creating section", e);
            return "Unable to create section. Please try again.";
        }
    }

    public String assignInstructor(int sectionId, Integer instructorId) {
        try {
            return adminService.assignInstructor(sectionId, instructorId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Instructor assignment error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error assigning instructor", e);
            return "Unable to assign instructor. Please try again.";
        }
    }

    public boolean toggleMaintenanceMode(boolean enabled) {
        try {
            return adminService.toggleMaintenanceMode(enabled);
        } catch (BusinessException e) {
            logger.warn("Maintenance mode toggle error: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error toggling maintenance mode", e);
            return false;
        }
    }

    public boolean isMaintenanceMode() {
        try {
            return adminService.isMaintenanceMode();
        } catch (Exception e) {
            logger.error("Error checking maintenance mode", e);
            return false; 
        }
    }

    public List<Course> getAllCourses() {
        try {
            return adminService.getAllCourses();
        } catch (BusinessException e) {
            logger.warn("Error retrieving courses: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving courses", e);
            return Collections.emptyList();
        }
    }

    public List<Section> getAllSections() {
        try {
            return adminService.getAllSections();
        } catch (BusinessException e) {
            logger.warn("Error retrieving sections: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving sections", e);
            return Collections.emptyList();
        }
    }

    public List<Student> getAllStudents() {
        try {
            return adminService.getAllStudents();
        } catch (BusinessException e) {
            logger.warn("Error retrieving students: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving students", e);
            return Collections.emptyList();
        }
    }

    public List<Instructor> getAllInstructors() {
        try {
            return adminService.getAllInstructors();
        } catch (BusinessException e) {
            logger.warn("Error retrieving instructors: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving instructors", e);
            return Collections.emptyList();
        }
    }

    public List<Enrollment> getSectionEnrollments(int sectionId) {
        try {
            return adminService.getSectionEnrollments(sectionId);
        } catch (BusinessException e) {
            logger.warn("Error retrieving section enrollments: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving section enrollments", e);
            return Collections.emptyList();
        }
    }

    public String updateCourse(int courseId, String code, String title, int credits, String description) {
        try {
            return adminService.updateCourse(courseId, code, title, credits, description);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Course update error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error updating course", e);
            return "Unable to update course. Please try again.";
        }
    }

    public String deleteCourse(int courseId) {
        try {
            return adminService.deleteCourse(courseId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Course deletion error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error deleting course", e);
            return "Unable to delete course. Please try again.";
        }
    }

    public String updateSection(int sectionId, int courseId, Integer instructorId, String day, String time, String room, int capacity, String semester, int year) {
        try {
            return adminService.updateSection(sectionId, courseId, instructorId, day, time, room, capacity, semester, year);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section update error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error updating section", e);
            return "Unable to update section. Please try again.";
        }
    }

    public String deleteSection(int sectionId) {
        try {
            return adminService.deleteSection(sectionId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section deletion error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error deleting section", e);
            return "Unable to delete section. Please try again.";
        }
    }
}

