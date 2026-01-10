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

public class InstructorService {
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final AssessmentWeightDAO assessmentWeightDAO = new AssessmentWeightDAO();
    private static final Logger logger = Logger.getLogger(InstructorService.class);
    private static final double DEFAULT_QUIZ_WEIGHT = 20.0;
    private static final double DEFAULT_MIDTERM_WEIGHT = 30.0;
    private static final double DEFAULT_ENDSEM_WEIGHT = 50.0;

    public Instructor getProfile(int userId) {
        if (!AccessControl.canAccessAsInstructor(userId)) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            return instructorDAO.findByUserId(userId);
        } catch (DataAccessException e) {
            logger.error("Database error getting instructor profile: {}", userId, e);
            throw new BusinessException("Unable to retrieve instructor profile.");
        }
    }

    public List<Section> getMySections(int instructorId) {
        if (!AccessControl.canAccessAsInstructor(instructorId)) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            return sectionDAO.findByInstructor(instructorId);
        } catch (DataAccessException e) {
            logger.error("Database error getting instructor sections: {}", instructorId, e);
            throw new BusinessException("Unable to retrieve sections.");
        }
    }

    public List<Enrollment> getSectionEnrollments(int instructorId, int sectionId) {
        if (!AccessControl.canAccessAsInstructor(instructorId)) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            requireInstructorSectionOwnership(instructorId, sectionId);
            return enrollmentDAO.findBySection(sectionId);
        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error getting section enrollments: {}", sectionId, e);
            throw new BusinessException("Unable to retrieve enrollments.");
        }
    }

    public String enterScore(int instructorId, int enrollmentId, String component, Double score, Double maxScore) {
        if (!AccessControl.canModify()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        
        try {
            ValidationUtil.validatePositive(enrollmentId, "Enrollment ID");
            ValidationUtil.validateNotEmpty(component, "Component");
            
            ValidationUtil.validateNonNegative(score, "Marks", "Please enter marks greater than or equal to 0.");
            if (maxScore != null && maxScore <= 0) {
                throw new ValidationException("Max score must be positive");
            }
            
            Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
            if (enrollment == null) {
                throw new BusinessException("Enrollment not found.");
            }
            
            requireInstructorSectionOwnership(instructorId, enrollment.getSectionId());

            Grade grade = new Grade();
            grade.setEnrollmentId(enrollmentId);
            grade.setComponent(component);
            grade.setScore(score);
            grade.setMaxScore(maxScore != null ? maxScore : 100.0);

            if (gradeDAO.saveOrUpdate(grade)) {
                logger.info("Score entered for enrollment {}: {} = {}", enrollmentId, component, score);
                return "Score saved successfully.";
            }
            throw new BusinessException("Failed to save score.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Enter score error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error entering score", e);
            throw new BusinessException("Unable to save score. Please try again.");
        }
    }

    public String computeFinalGrade(int instructorId, int enrollmentId, double quizWeight, double midtermWeight, double endSemWeight) {
        if (!AccessControl.canModify()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        
        try {
            ValidationUtil.validatePositive(enrollmentId, "Enrollment ID");
            ValidationUtil.validateRange(quizWeight, 0, 100, "Quiz weight");
            ValidationUtil.validateRange(midtermWeight, 0, 100, "Midterm weight");
            ValidationUtil.validateRange(endSemWeight, 0, 100, "End semester weight");
            
            double totalWeight = quizWeight + midtermWeight + endSemWeight;
            if (Math.abs(totalWeight - 100.0) > 0.01) {
                throw new ValidationException("Weights must sum to 100%");
            }
            
            Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
            if (enrollment == null) {
                throw new BusinessException("Enrollment not found.");
            }
            
            requireInstructorSectionOwnership(instructorId, enrollment.getSectionId());

            List<Grade> grades = gradeDAO.findByEnrollment(enrollmentId);
            double quizScore = 0, midtermScore = 0, endSemScore = 0;
            boolean hasQuiz = false, hasMidterm = false, hasEndSem = false;

            for (Grade g : grades) {
                String comp = g.getComponent().toUpperCase();
                if (comp.contains("QUIZ")) {
                    double score = g.getScore() != null ? g.getScore() : 0;
                    double max = g.getMaxScore() != null ? g.getMaxScore() : 100;
                    if (max > 0) {
                        quizScore = (score / max) * 100;
                        hasQuiz = true;
                    }
                } else if (comp.contains("MIDTERM")) {
                    double score = g.getScore() != null ? g.getScore() : 0;
                    double max = g.getMaxScore() != null ? g.getMaxScore() : 100;
                    if (max > 0) {
                        midtermScore = (score / max) * 100;
                        hasMidterm = true;
                    }
                } else if (comp.contains("END") || comp.contains("FINAL") || comp.contains("SEM")) {
                    double score = g.getScore() != null ? g.getScore() : 0;
                    double max = g.getMaxScore() != null ? g.getMaxScore() : 100;
                    if (max > 0) {
                        endSemScore = (score / max) * 100;
                        hasEndSem = true;
                    }
                }
            }

            double finalGrade = 0;
            if (hasQuiz) finalGrade += quizScore * (quizWeight / 100);
            if (hasMidterm) finalGrade += midtermScore * (midtermWeight / 100);
            if (hasEndSem) finalGrade += endSemScore * (endSemWeight / 100);

            if (gradeDAO.updateFinalGrade(enrollmentId, finalGrade)) {
                logger.info("Final grade computed for enrollment {}: {}", enrollmentId, finalGrade);
                return String.format("Final grade computed: %.2f", finalGrade);
            }
            throw new BusinessException("Failed to compute final grade.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Compute final grade error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error computing final grade", e);
            throw new BusinessException("Unable to compute final grade. Please try again.");
        }
    }

    public List<Grade> getSectionGrades(int instructorId, int sectionId) {
        if (!AccessControl.canAccessAsInstructor(instructorId)) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            requireInstructorSectionOwnership(instructorId, sectionId);
            return gradeDAO.findBySection(sectionId);
        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error getting section grades: {}", sectionId, e);
            throw new BusinessException("Unable to retrieve grades.");
        }
    }

    public AssessmentWeights getAssessmentWeights(int instructorId, int sectionId) {
        if (!AccessControl.canAccessAsInstructor(instructorId)) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            ValidationUtil.validatePositive(sectionId, "Section ID");
            requireInstructorSectionOwnership(instructorId, sectionId);
            AssessmentWeights weights = assessmentWeightDAO.findBySectionId(sectionId);
            return weights != null ? weights : defaultWeights(sectionId);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Assessment weights retrieval error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error getting assessment weights for section {}", sectionId, e);
            throw new BusinessException("Unable to load assessment weights.");
        }
    }

    public String saveAssessmentWeights(int instructorId, int sectionId,
                                        double quizWeight, double midtermWeight, double endSemWeight) {
        if (!AccessControl.canModify()) {
            throw new BusinessException(AccessControl.getAccessDeniedMessage());
        }
        try {
            ValidationUtil.validatePositive(sectionId, "Section ID");
            ValidationUtil.validateRange(quizWeight, 0, 100, "Quiz weight");
            ValidationUtil.validateRange(midtermWeight, 0, 100, "Midterm weight");
            ValidationUtil.validateRange(endSemWeight, 0, 100, "End semester weight");

            double total = quizWeight + midtermWeight + endSemWeight;
            if (Math.abs(total - 100.0) > 0.01) {
                throw new ValidationException("Weights must sum to 100%");
            }

            requireInstructorSectionOwnership(instructorId, sectionId);

            AssessmentWeights weights = new AssessmentWeights();
            weights.setSectionId(sectionId);
            weights.setQuizWeight(quizWeight);
            weights.setMidtermWeight(midtermWeight);
            weights.setEndSemWeight(endSemWeight);

            if (assessmentWeightDAO.upsert(weights)) {
                logger.info("Assessment weights updated for section {}", sectionId);
                return "Assessment weights saved successfully.";
            }
            throw new BusinessException("Failed to save assessment weights.");
        } catch (ValidationException | BusinessException e) {
            logger.warn("Assessment weight save error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            logger.error("Database error saving assessment weights for section {}", sectionId, e);
            throw new BusinessException("Unable to save assessment weights.");
        }
    }

    private void requireInstructorSectionOwnership(int instructorId, int sectionId) {
        Section section = sectionDAO.findById(sectionId);
        if (section == null) {
            throw new BusinessException("Section not found.");
        }
        Integer assignedInstructor = section.getInstructorId();
        if (assignedInstructor == null || assignedInstructor != instructorId) {
            throw new BusinessException("This is not your section.");
        }
    }

    private AssessmentWeights defaultWeights(int sectionId) {
        AssessmentWeights weights = new AssessmentWeights();
        weights.setSectionId(sectionId);
        weights.setQuizWeight(DEFAULT_QUIZ_WEIGHT);
        weights.setMidtermWeight(DEFAULT_MIDTERM_WEIGHT);
        weights.setEndSemWeight(DEFAULT_ENDSEM_WEIGHT);
        return weights;
    }

}
