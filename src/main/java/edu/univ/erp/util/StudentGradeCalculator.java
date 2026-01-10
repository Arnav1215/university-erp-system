package edu.univ.erp.util;

import edu.univ.erp.domain.Grade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class StudentGradeCalculator {
    private static final List<String> SEMESTER_ORDER = Arrays.asList(
            "SPRING", "SUMMER", "FALL", "WINTER", "MONSOON");

    private StudentGradeCalculator() {}

    public static GradeReport buildReport(List<Grade> grades) {
        List<CourseResult> courses = summarize(grades);
        Map<String, List<CourseResult>> bySemester = groupBySemester(courses);
        List<String> semesterOrder = sortSemesterKeys(bySemester.keySet());
        Map<String, Double> sgpaMap = new LinkedHashMap<>();
        for (String sem : semesterOrder) {
            sgpaMap.put(sem, computeGpa(bySemester.getOrDefault(sem, List.of())));
        }
        double cgpa = computeGpa(courses);
        return new GradeReport(courses, bySemester, sgpaMap, semesterOrder, cgpa);
    }

    public static List<CourseResult> summarize(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return List.of();
        }
        Map<Integer, CourseResult> byEnrollment = new LinkedHashMap<>();
        for (Grade grade : grades) {
            if (grade == null) continue;
            int enrollmentId = grade.getEnrollmentId();
            CourseResult result = byEnrollment.computeIfAbsent(enrollmentId,
                    id -> new CourseResult(grade));
            result.applyGrade(grade);
        }
        return new ArrayList<>(byEnrollment.values());
    }

    public static double computeGpa(List<CourseResult> courses) {
        if (courses == null || courses.isEmpty()) {
            return Double.NaN;
        }
        double credits = 0;
        double weighted = 0;
        for (CourseResult course : courses) {
            double gradePoint = GradeScale.toGradePoint(course.getLetterGrade());
            Integer courseCredits = course.getCredits();
            if (course.getFinalPercent() == null || courseCredits == null || courseCredits <= 0) {
                continue;
            }
            if (Double.isNaN(gradePoint)) {
                continue;
            }
            credits += courseCredits;
            weighted += gradePoint * courseCredits;
        }
        if (credits == 0) {
            return Double.NaN;
        }
        return weighted / credits;
    }

    public static Map<String, List<CourseResult>> groupBySemester(List<CourseResult> courses) {
        if (courses == null) {
            return Map.of();
        }
        return courses.stream()
                .collect(Collectors.groupingBy(CourseResult::getSemesterKey, LinkedHashMap::new, Collectors.toList()));
    }

    public static List<String> sortSemesterKeys(Iterable<String> keys) {
        if (keys == null) {
            return List.of();
        }
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (key != null) {
                list.add(key);
            }
        }
        list.sort(semesterComparator());
        return list;
    }

    public static Comparator<String> semesterComparator() {
        return (a, b) -> {
            SemesterMetadata left = parseSemesterKey(a);
            SemesterMetadata right = parseSemesterKey(b);
            int yearCompare = Integer.compare(left.year, right.year);
            if (yearCompare != 0) {
                return yearCompare;
            }
            int orderCompare = Integer.compare(left.order, right.order);
            if (orderCompare != 0) {
                return orderCompare;
            }
            return Optional.ofNullable(a).orElse("").compareTo(Optional.ofNullable(b).orElse(""));
        };
    }

    public static String buildSemesterLabel(String semester, Integer year) {
        if (semester == null && year == null) {
            return "Unknown Semester";
        }
        String normalized = normalizeSemester(semester);
        if (year == null) {
            return normalized != null ? normalized : "Unknown Semester";
        }
        if (normalized == null) {
            return String.valueOf(year);
        }
        return normalized + " " + year;
    }

    private static SemesterMetadata parseSemesterKey(String key) {
        if (key == null) {
            return new SemesterMetadata("", Integer.MIN_VALUE, Integer.MIN_VALUE);
        }
        String trimmed = key.trim();
        String[] parts = trimmed.split(" ");
        int year = Integer.MIN_VALUE;
        if (parts.length >= 2) {
            try {
                year = Integer.parseInt(parts[parts.length - 1]);
                trimmed = String.join(" ", Arrays.copyOf(parts, parts.length - 1));
            } catch (NumberFormatException ignored) {}
        }
        String normalized = normalizeSemester(trimmed);
        int order = SEMESTER_ORDER.indexOf(normalized != null ? normalized.toUpperCase(Locale.ROOT) : "");
        if (order < 0) {
            order = SEMESTER_ORDER.size() + 1;
        }
        return new SemesterMetadata(normalized != null ? normalized : trimmed, year, order);
    }

    private static String normalizeSemester(String semester) {
        if (semester == null) return null;
        String upper = semester.trim().toUpperCase(Locale.ROOT);
        if (upper.isEmpty()) return null;
        for (String known : SEMESTER_ORDER) {
            if (known.equals(upper)) {
                return capitalize(upper.toLowerCase(Locale.ROOT));
            }
        }
        return capitalize(semester.toLowerCase(Locale.ROOT));
    }

    private static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static final class SemesterMetadata {
        private final String semester;
        private final int year;
        private final int order;

        private SemesterMetadata(String semester, int year, int order) {
            this.semester = semester;
            this.year = year;
            this.order = order;
        }
    }

    public static final class CourseResult {
        private final int enrollmentId;
        private final String courseCode;
        private final String courseTitle;
        private final Integer credits;
        private final String semester;
        private final Integer year;
        private final Integer sectionId;
        private Double quizScore;
        private Double quizMax;
        private Double midtermScore;
        private Double midtermMax;
        private Double endSemScore;
        private Double endSemMax;
        private Double finalPercent;

        private CourseResult(Grade grade) {
            this.enrollmentId = grade.getEnrollmentId();
            this.courseCode = grade.getCourseCode();
            this.courseTitle = grade.getCourseTitle();
            this.credits = grade.getCourseCredits();
            this.semester = grade.getSemester();
            this.year = grade.getYear();
            this.sectionId = grade.getSectionId();
        }

        private void applyGrade(Grade grade) {
            String component = grade.getComponent() != null
                    ? grade.getComponent().toUpperCase(Locale.ROOT)
                    : "";
            if (component.contains("QUIZ")) {
                this.quizScore = grade.getScore();
                this.quizMax = grade.getMaxScore();
            } else if (component.contains("MIDTERM")) {
                this.midtermScore = grade.getScore();
                this.midtermMax = grade.getMaxScore();
            } else if (component.contains("END") || component.contains("FINAL") || component.contains("SEM")) {
                this.endSemScore = grade.getScore();
                this.endSemMax = grade.getMaxScore();
            }
            if (grade.getFinalGrade() != null) {
                this.finalPercent = grade.getFinalGrade();
            }
        }

        public int getEnrollmentId() {
            return enrollmentId;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public Integer getCredits() {
            return credits;
        }

        public String getSemesterKey() {
            return buildSemesterLabel(semester, year);
        }

        public Integer getYear() {
            return year;
        }

        public Double getQuizScore() {
            return quizScore;
        }

        public Double getQuizMax() {
            return quizMax;
        }

        public Double getMidtermScore() {
            return midtermScore;
        }

        public Double getMidtermMax() {
            return midtermMax;
        }

        public Double getEndSemScore() {
            return endSemScore;
        }

        public Double getEndSemMax() {
            return endSemMax;
        }

        public Double getFinalPercent() {
            return finalPercent;
        }

        public String getLetterGrade() {
            return GradeScale.toLetter(finalPercent);
        }

        public Integer getSectionId() {
            return sectionId;
        }
    }

    public static final class GradeReport {
        private final List<CourseResult> courses;
        private final Map<String, List<CourseResult>> coursesBySemester;
        private final Map<String, Double> sgpaBySemester;
        private final List<String> semesterOrder;
        private final double cgpa;

        private GradeReport(List<CourseResult> courses,
                            Map<String, List<CourseResult>> coursesBySemester,
                            Map<String, Double> sgpaBySemester,
                            List<String> semesterOrder,
                            double cgpa) {
            this.courses = java.util.Collections.unmodifiableList(new ArrayList<>(courses));
            this.coursesBySemester = java.util.Collections.unmodifiableMap(new LinkedHashMap<>(coursesBySemester));
            this.sgpaBySemester = java.util.Collections.unmodifiableMap(new LinkedHashMap<>(sgpaBySemester));
            this.semesterOrder = java.util.Collections.unmodifiableList(new ArrayList<>(semesterOrder));
            this.cgpa = cgpa;
        }

        public List<CourseResult> getCourses() {
            return courses;
        }

        public Map<String, List<CourseResult>> getCoursesBySemester() {
            return coursesBySemester;
        }

        public List<String> getSemesterOrder() {
            return semesterOrder;
        }

        public double getCgpa() {
            return cgpa;
        }

        public double getSgpa(String semester) {
            return sgpaBySemester.getOrDefault(semester, Double.NaN);
        }

        public Map<String, Double> getSgpaBySemester() {
            return sgpaBySemester;
        }
    }
}


