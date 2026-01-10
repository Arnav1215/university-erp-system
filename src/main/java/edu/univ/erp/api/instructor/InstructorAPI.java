package edu.univ.erp.api.instructor;

import edu.univ.erp.auth.Session;
import edu.univ.erp.domain.AssessmentWeights;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.exception.BusinessException;
import edu.univ.erp.exception.ValidationException;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.util.Logger;

import java.util.Collections;
import java.util.List;

public class InstructorAPI {
    private final InstructorService instructorService = new InstructorService();
    private static final Logger logger = Logger.getLogger(InstructorAPI.class);

    public Instructor getProfile() {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.getProfile(userId);
        } catch (Exception e) {
            logger.error("Error getting instructor profile", e);
            return null;
        }
    }

    public List<Section> getMySections() {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.getMySections(userId);
        } catch (Exception e) {
            logger.error("Error getting instructor sections", e);
            return Collections.emptyList();
        }
    }

    public List<Enrollment> getSectionEnrollments(int sectionId) {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.getSectionEnrollments(userId, sectionId);
        } catch (Exception e) {
            logger.error("Error getting section enrollments", e);
            return Collections.emptyList();
        }
    }

    public String enterScore(int enrollmentId, String component, Double score, Double maxScore) {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.enterScore(userId, enrollmentId, component, score, maxScore);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Enter score error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error entering score", e);
            return "Unable to enter score. Please try again.";
        }
    }

    public String computeFinalGrade(int enrollmentId, double quizWeight, double midtermWeight, double endSemWeight) {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.computeFinalGrade(userId, enrollmentId, quizWeight, midtermWeight, endSemWeight);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Compute final grade error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error computing final grade", e);
            return "Unable to compute final grade. Please try again.";
        }
    }

    public List<Grade> getSectionGrades(int sectionId) {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.getSectionGrades(userId, sectionId);
        } catch (Exception e) {
            logger.error("Error getting section grades", e);
            return Collections.emptyList();
        }
    }

    public AssessmentWeights getAssessmentWeights(int sectionId) {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.getAssessmentWeights(userId, sectionId);
        } catch (BusinessException e) {
            logger.warn("Assessment weights unavailable: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error getting assessment weights", e);
            return null;
        }
    }

    public String saveAssessmentWeights(int sectionId, double quizWeight, double midtermWeight, double endSemWeight) {
        try {
            int userId = Session.getInstance().getUserId();
            return instructorService.saveAssessmentWeights(userId, sectionId, quizWeight, midtermWeight, endSemWeight);
        } catch (ValidationException | BusinessException e) {
            logger.warn("Assessment weight save error: {}", e.getMessage());
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error saving assessment weights", e);
            return "Unable to save assessment weights. Please try again.";
        }
    }
}

