package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.PasswordHash;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.util.Logger;
import edu.univ.erp.util.ValidationUtil;

import java.util.List;

public class AdminService {
    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();
    private static final Logger logger = Logger.getLogger(AdminService.class);

    public String createUser(User user, String password, String profileType, Object profile) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can create users.");
        }

        try {
            
            ValidationUtil.validateNotNull(user, "User");
            ValidationUtil.validateUsername(user.getUsername());
            ValidationUtil.validatePassword(password);
            ValidationUtil.validateRole(user.getRole());

            
            if (userDAO.exists(user.getUsername())) {
                throw new BusinessException("Username already exists: " + user.getUsername());
            }

            
            String hash = PasswordHash.hash(password);
            if (!userDAO.create(user, hash)) {
                throw new BusinessException("Failed to create user account.");
            }

            
            if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                if (profile == null || !(profile instanceof Student)) {
                    throw new BusinessException("Student profile is required for STUDENT role.");
                }
                Student student = (Student) profile;
                ValidationUtil.validateNotNull(student.getRollNo(), "Roll number");
                ValidationUtil.validateNotEmpty(student.getFullName(), "Full name");
                ValidationUtil.validateEmail(student.getEmail());
                
                student.setUserId(user.getId());
                if (!studentDAO.create(student)) {
                    
                    
                    logger.warn("User {} created but student profile creation failed", user.getUsername());
                    throw new BusinessException("User created but failed to create student profile.");
                }
            } else if ("INSTRUCTOR".equalsIgnoreCase(user.getRole())) {
                if (profile == null || !(profile instanceof Instructor)) {
                    throw new BusinessException("Instructor profile is required for INSTRUCTOR role.");
                }
                Instructor instructor = (Instructor) profile;
                ValidationUtil.validateNotEmpty(instructor.getFullName(), "Full name");
                ValidationUtil.validateEmail(instructor.getEmail());
                ValidationUtil.validateNotEmpty(instructor.getDepartment(), "Department");
                
                instructor.setUserId(user.getId());
                if (!instructorDAO.create(instructor)) {
                    logger.warn("User {} created but instructor profile creation failed", user.getUsername());
                    throw new BusinessException("User created but failed to create instructor profile.");
                }
            }

            logger.info("User created successfully: {}", user.getUsername());
            return "User created successfully.";
        } catch (ValidationException | BusinessException e) {
            logger.warn("User creation error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error creating user: {}", user != null ? user.getUsername() : "null", e);
            throw new BusinessException("Unable to create user. Please try again.");
        }
    }

    public String createCourse(Course course) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can create courses.");
        }
        
        try {
            ValidationUtil.validateNotNull(course, "Course");
            ValidationUtil.validateNotEmpty(course.getCode(), "Course code");
            ValidationUtil.validateNotEmpty(course.getTitle(), "Course title");
            ValidationUtil.validatePositive(course.getCredits(), "Credits");
            
            if (courseDAO.findByCode(course.getCode()) != null) {
                throw new BusinessException("Course code already exists: " + course.getCode());
            }
            
            if (courseDAO.create(course)) {
                logger.info("Course created successfully: {}", course.getCode());
                return "Course created successfully.";
            }
            throw new BusinessException("Failed to create course.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Course creation error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error creating course: {}", course != null ? course.getCode() : "null", e);
            throw new BusinessException("Unable to create course. Please try again.");
        }
    }

    public String createSection(Section section) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can create sections.");
        }
        
        try {
            ValidationUtil.validateNotNull(section, "Section");
            ValidationUtil.validatePositive(section.getCourseId(), "Course ID");
            ValidationUtil.validatePositive(section.getCapacity(), "Capacity");
            ValidationUtil.validateNotEmpty(section.getSemester(), "Semester");
            ValidationUtil.validatePositive(section.getYear(), "Year");
            
            
            if (courseDAO.findById(section.getCourseId()) == null) {
                throw new BusinessException("Course not found: " + section.getCourseId());
            }
            
            if (sectionDAO.create(section)) {
                logger.info("Section created successfully: {}", section.getSectionId());
                return "Section created successfully.";
            }
            throw new BusinessException("Failed to create section.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section creation error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error creating section", e);
            throw new BusinessException("Unable to create section. Please try again.");
        }
    }

    public String assignInstructor(int sectionId, Integer instructorId) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can assign instructors.");
        }
        
        try {
            ValidationUtil.validatePositive(sectionId, "Section ID");
            
            
            if (sectionDAO.findById(sectionId) == null) {
                throw new BusinessException("Section not found: " + sectionId);
            }
            
            
            if (instructorId != null && instructorDAO.findByUserId(instructorId) == null) {
                throw new BusinessException("Instructor not found: " + instructorId);
            }
            
            if (sectionDAO.updateInstructor(sectionId, instructorId)) {
                logger.info("Instructor {} assigned to section {}", instructorId, sectionId);
                return "Instructor assigned successfully.";
            }
            throw new BusinessException("Failed to assign instructor.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Instructor assignment error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error assigning instructor", e);
            throw new BusinessException("Unable to assign instructor. Please try again.");
        }
    }

    public List<Enrollment> getSectionEnrollments(int sectionId) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }

        try {
            ValidationUtil.validatePositive(sectionId, "Section ID");
            return enrollmentDAO.findBySection(sectionId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section enrollment fetch error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Error retrieving enrollments for section {}", sectionId, e);
            throw new BusinessException("Unable to retrieve enrollments for section.");
        }
    }

    public boolean toggleMaintenanceMode(boolean enabled) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can toggle maintenance mode.");
        }
        try {
            boolean result = settingsDAO.setMaintenanceMode(enabled);
            logger.info("Maintenance mode {} by admin", enabled ? "enabled" : "disabled");
            return result;
        } catch (DataAccessException e) {
            logger.error("Error toggling maintenance mode", e);
            throw new BusinessException("Unable to toggle maintenance mode.");
        }
    }

    public boolean isMaintenanceMode() {
        try {
            return settingsDAO.isMaintenanceMode();
        } catch (DataAccessException e) {
            logger.error("Error checking maintenance mode", e);
            return false; 
        }
    }

    public List<Course> getAllCourses() {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            return courseDAO.findAll();
        } catch (DataAccessException e) {
            logger.error("Error retrieving courses", e);
            throw new BusinessException("Unable to retrieve courses.");
        }
    }

    public List<Section> getAllSections() {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            return sectionDAO.findAll();
        } catch (DataAccessException e) {
            logger.error("Error retrieving sections", e);
            throw new BusinessException("Unable to retrieve sections.");
        }
    }

    public List<Student> getAllStudents() {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            return studentDAO.findAll();
        } catch (DataAccessException e) {
            logger.error("Error retrieving students", e);
            throw new BusinessException("Unable to retrieve students.");
        }
    }

    public List<Instructor> getAllInstructors() {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            return instructorDAO.findAll();
        } catch (DataAccessException e) {
            logger.error("Error retrieving instructors", e);
            throw new BusinessException("Unable to retrieve instructors.");
        }
    }

    public String updateCourse(int courseId, String code, String title, int credits, String description) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can update courses.");
        }
        try {
            ValidationUtil.validatePositive(courseId, "Course ID");
            ValidationUtil.validateNotEmpty(code, "Course code");
            ValidationUtil.validateNotEmpty(title, "Course title");
            ValidationUtil.validatePositive(credits, "Credits");
            
            if (courseDAO.update(courseId, code, title, credits, description)) {
                logger.info("Course updated successfully: {}", code);
                return "Course updated successfully";
            }
            throw new BusinessException("Failed to update course.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Course update error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error updating course: {}", courseId, e);
            throw new BusinessException("Unable to update course. Please try again.");
        }
    }

    public String deleteCourse(int courseId) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can delete courses.");
        }
        try {
            ValidationUtil.validatePositive(courseId, "Course ID");
            
            if (courseDAO.delete(courseId)) {
                logger.info("Course deleted successfully: {}", courseId);
                return "Course deleted successfully";
            }
            throw new BusinessException("Failed to delete course.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Course deletion error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error deleting course: {}", courseId, e);
            throw new BusinessException("Unable to delete course. Please try again.");
        }
    }

    public String updateSection(int sectionId, int courseId, Integer instructorId, String day, String time, String room, int capacity, String semester, int year) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can update sections.");
        }
        try {
            ValidationUtil.validatePositive(sectionId, "Section ID");
            ValidationUtil.validatePositive(courseId, "Course ID");
            ValidationUtil.validatePositive(capacity, "Capacity");
            ValidationUtil.validateNotEmpty(semester, "Semester");
            ValidationUtil.validatePositive(year, "Year");
            
            if (sectionDAO.update(sectionId, courseId, instructorId, day, time, room, capacity, semester, year)) {
                logger.info("Section updated successfully: {}", sectionId);
                return "Section updated successfully";
            }
            throw new BusinessException("Failed to update section.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section update error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error updating section: {}", sectionId, e);
            throw new BusinessException("Unable to update section. Please try again.");
        }
    }

    public String deleteSection(int sectionId) {
        if (!AccessControl.canAccessAsAdmin()) {
            throw new BusinessException("Only administrators can delete sections.");
        }
        try {
            ValidationUtil.validatePositive(sectionId, "Section ID");
            
            if (sectionDAO.delete(sectionId)) {
                logger.info("Section deleted successfully: {}", sectionId);
                return "Section deleted successfully";
            }
            throw new BusinessException("Failed to delete section.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Section deletion error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error deleting section: {}", sectionId, e);
            throw new BusinessException("Unable to delete section. Please try again.");
        }
    }
}

