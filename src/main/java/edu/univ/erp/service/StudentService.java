package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.util.Logger;
import edu.univ.erp.util.ValidationUtil;

import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private static final Logger logger = Logger.getLogger(StudentService.class);

    public Student getProfile(int userId) {
        if (!AccessControl.canAccessAsStudent(userId)) {
            throw new SecurityException(AccessControl.getAccessDeniedMessage());
        }
        return studentDAO.findByUserId(userId);
    }

    public List<Section> getAvailableSections(String semester, int year) {
        
        List<Section> allSections = sectionDAO.findBySemesterAndYear(semester, year);
        
        
        return allSections.stream()
                .filter(section -> section.getSeatsLeft() > 0)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Section> getAvailableSections(int studentId, String semester, int year) {
        try {
            logger.info("Getting available sections for student {} in semester {} year {}", studentId, semester, year);
            
            
            List<Section> allSections = sectionDAO.findBySemesterAndYear(semester, year);
            logger.info("Found {} total sections for semester {} year {}", allSections.size(), semester, year);
            
            
            List<Enrollment> currentEnrollments = enrollmentDAO.findByStudent(studentId);
            
            java.util.Set<Integer> enrolledSectionIds = currentEnrollments.stream()
                    .filter(e -> "ENROLLED".equalsIgnoreCase(e.getStatus()))
                    .map(Enrollment::getSectionId)
                    .collect(java.util.stream.Collectors.toSet());
            logger.info("Student {} is currently enrolled in {} sections (excluding dropped)", studentId, enrolledSectionIds.size());
            
            
            List<Section> available = allSections.stream()
                    .filter(section -> {
                        
                        boolean notEnrolled = !enrollmentDAO.exists(studentId, section.getSectionId());
                        boolean hasSeats = section.getSeatsLeft() > 0;
                        if (!notEnrolled) {
                            logger.debug("Section {} filtered out: already enrolled (checked in DB)", section.getSectionId());
                        }
                        if (!hasSeats) {
                            logger.debug("Section {} filtered out: no seats left ({} seats)", section.getSectionId(), section.getSeatsLeft());
                        }
                        return notEnrolled && hasSeats;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            logger.info("Returning {} available sections for student {}", available.size(), studentId);
            return available;
        } catch (Exception e) {
            logger.error("Error getting available sections for student {} in semester {} year {}", studentId, semester, year, e);
            throw e;
        }
    }

    public String registerForSection(int studentId, int sectionId) {
        if (!AccessControl.canModify()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        if (!AccessControl.canAccessAsStudent(studentId)) {
            throw new BusinessException("You do not have permission to register.");
        }

        try {
            ValidationUtil.validatePositive(studentId, "Student ID");
            ValidationUtil.validatePositive(sectionId, "Section ID");

            if (!canEnroll(sectionId)) {
                throw new BusinessException("Add window has expired for this section.");
            }

            
            
            if (enrollmentDAO.exists(studentId, sectionId)) {
                
                Enrollment existing = enrollmentDAO.findByStudentAndSection(studentId, sectionId);
                if (existing != null && "ENROLLED".equalsIgnoreCase(existing.getStatus())) {
                    throw new BusinessException("You are already enrolled in this section.");
                }
                
                
            }

            
            Section section = sectionDAO.findById(sectionId);
            if (section == null) {
                throw new BusinessException("Section not found.");
            }
            
            
            if (section.getSeatsLeft() <= 0) {
                throw new BusinessException("Section is full. No seats available.");
            }

            
            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setSectionId(sectionId);
            
            
            if (!enrollmentDAO.create(enrollment)) {
                throw new BusinessException("Failed to create enrollment. Please try again.");
            }
            
            
            if (!sectionDAO.incrementEnrolledCount(sectionId)) {
                
                try {
                    enrollmentDAO.drop(enrollment.getEnrollmentId());
                    logger.warn("Rolled back enrollment {} after failed capacity update", enrollment.getEnrollmentId());
                } catch (Exception rollbackError) {
                    logger.error("Failed to rollback enrollment after capacity update failure", rollbackError);
                }
                throw new BusinessException("Failed to update section capacity. Registration cancelled.");
            }
            
            logger.info("Student {} successfully registered for section {}", studentId, sectionId);
            return "Successfully registered for section.";
        } catch (ValidationException | BusinessException e) {
            logger.warn("Registration error: {}", e.getMessage());
            throw e;
        } catch (edu.univ.erp.exception.DataAccessException e) {
            logger.error("Database error during registration", e);
            
            if (e.getMessage() != null && (e.getMessage().contains("Duplicate") || 
                e.getMessage().contains("unique") || 
                e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException)) {
                
                Enrollment existing = enrollmentDAO.findByStudentAndSection(studentId, sectionId);
                if (existing != null && "DROPPED".equalsIgnoreCase(existing.getStatus())) {
                    
                    try {
                        enrollmentDAO.deletePermanently(existing.getEnrollmentId());
                        logger.info("Deleted DROPPED enrollment {} to allow re-enrollment", existing.getEnrollmentId());
                        
                        Enrollment newEnrollment = new Enrollment();
                        newEnrollment.setStudentId(studentId);
                        newEnrollment.setSectionId(sectionId);
                        if (enrollmentDAO.create(newEnrollment) && sectionDAO.incrementEnrolledCount(sectionId)) {
                            logger.info("Student {} successfully re-registered for section {} after drop", studentId, sectionId);
                            return "Successfully registered for section.";
                        }
                    } catch (Exception retryError) {
                        logger.error("Error retrying enrollment after deleting DROPPED record", retryError);
                    }
                }
                throw new BusinessException("You are already enrolled in this section.");
            }
            throw new BusinessException("Unable to register. Please try again.");
        }
    }

    public String dropSection(int studentId, int enrollmentId) {
        if (!AccessControl.canModify()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        if (!AccessControl.canAccessAsStudent(studentId)) {
            throw new BusinessException("You do not have permission to drop.");
        }

        try {
            ValidationUtil.validatePositive(studentId, "Student ID");
            ValidationUtil.validatePositive(enrollmentId, "Enrollment ID");

            
            Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
            if (enrollment == null) {
                throw new BusinessException("Enrollment not found.");
            }
            if (enrollment.getStudentId() != studentId) {
                throw new BusinessException("You do not have permission to drop this enrollment.");
            }
            
            
            if (!"ENROLLED".equalsIgnoreCase(enrollment.getStatus())) {
                throw new BusinessException("This enrollment has already been dropped.");
            }

            if (!canDrop(enrollmentId)) {
                throw new BusinessException("Drop deadline has passed. You cannot drop this section.");
            }

            
            if (!enrollmentDAO.drop(enrollmentId)) {
                throw new BusinessException("Failed to drop enrollment. Please try again.");
            }
            
            
            if (!sectionDAO.decrementEnrolledCount(enrollment.getSectionId())) {
                logger.warn("Failed to decrement section count after drop for enrollment: {}. Enrollment already dropped.", enrollmentId);
                
            }
            
            logger.info("Student {} successfully dropped enrollment {}", studentId, enrollmentId);
            return "Successfully dropped section.";
        } catch (ValidationException | BusinessException e) {
            logger.warn("Drop section error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error during drop", e);
            throw new BusinessException("Unable to drop section. Please try again.");
        }
    }

    public void reloadStudentState(int studentId) {
        
        
        try {
            logger.info("Reloading student state for student {}", studentId);
            
            
            enrollmentDAO.findByStudent(studentId);
            logger.info("Student state reloaded for student {}", studentId);
        } catch (Exception e) {
            logger.error("Error reloading student state for student {}", studentId, e);
            throw new BusinessException("Unable to reload student state.");
        }
    }

    public List<Enrollment> getEnrollments(int studentId) {
        if (!AccessControl.canAccessAsStudent(studentId)) {
            throw new SecurityException(AccessControl.getAccessDeniedMessage());
        }
        return enrollmentDAO.findByStudent(studentId);
    }

    public List<Grade> getGrades(int studentId) {
        if (!AccessControl.canAccessAsStudent(studentId)) {
            throw new SecurityException(AccessControl.getAccessDeniedMessage());
        }
        return gradeDAO.findByStudent(studentId);
    }

    public boolean canEnroll(int sectionId) {
        ValidationUtil.validatePositive(sectionId, "Section ID");
        try {
            Section section = sectionDAO.findById(sectionId);
            if (section == null) {
                throw new BusinessException("Section not found.");
            }
            return sectionDAO.canEnroll(sectionId);
        } catch (DataAccessException e) {
            logger.error("Unable to evaluate add window for section {}", sectionId, e);
            throw new BusinessException("Unable to evaluate add window. Please try again.");
        }
    }

    public boolean canDrop(int enrollmentId) {
        ValidationUtil.validatePositive(enrollmentId, "Enrollment ID");
        try {
            Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
            if (enrollment == null) {
                throw new BusinessException("Enrollment not found.");
            }
            return enrollmentDAO.canDrop(enrollmentId);
        } catch (DataAccessException e) {
            logger.error("Unable to evaluate drop window for enrollment {}", enrollmentId, e);
            throw new BusinessException("Unable to evaluate drop window. Please try again.");
        }
    }
}

