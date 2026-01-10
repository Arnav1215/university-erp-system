package edu.univ.erp.api.catalog;

import edu.univ.erp.dao.CourseDAO;
import edu.univ.erp.dao.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;

import java.util.List;

public class CatalogAPI {
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public List<Section> getSectionsForTerm(String semester, int year) {
        return sectionDAO.findBySemesterAndYear(semester, year);
    }

    public List<Section> getAllSections() {
        return sectionDAO.findAll();
    }

    public Section getSection(int sectionId) {
        return sectionDAO.findById(sectionId);
    }

    public Course getCourse(int courseId) {
        return courseDAO.findById(courseId);
    }

    public Course getCourseByCode(String code) {
        return courseDAO.findByCode(code);
    }

    public java.util.List<String> getAvailableSemesters() {
        return sectionDAO.getDistinctSemesters();
    }

    public java.util.List<Integer> getAvailableYears() {
        return sectionDAO.getDistinctYears();
    }
}

