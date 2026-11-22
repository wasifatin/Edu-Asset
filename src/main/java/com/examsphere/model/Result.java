package com.examsphere.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents processed semester academic results for a student.
 * Tracks course grades, grade points, credits, computed GPA, and publication status.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 13L;

    private String resultId;
    private String studentId;
    private int semester;
    private double gpa;
    private double cgpa;
    private boolean published;
    private String publishedBy;
    private String publishDate;

    /** Map of courseCode -> letter grade (e.g. "A+", "B") */
    private Map<String, String> courseGrades;

    /** Map of courseCode -> grade points (e.g. 4.00, 3.00) */
    private Map<String, Double> courseGradePoints;

    /** Map of courseCode -> credit hours (supports fractional, e.g. 1.5, 3.0) */
    private Map<String, Double> courseCredits;

    public Result() {
        this.courseGrades = new HashMap<>();
        this.courseGradePoints = new HashMap<>();
        this.courseCredits = new HashMap<>();
        this.published = false;
    }

    public Result(String resultId, String studentId, int semester) {
        this();
        this.resultId = resultId;
        this.studentId = studentId;
        this.semester = semester;
    }

    /**
     * Calculates GPA from registered course credits and grade points.
     *
     * @return calculated GPA value rounded to two decimal places
     */
    public double calculateGPA() {
        if (courseCredits.isEmpty() || courseGradePoints.isEmpty()) {
            this.gpa = 0.0;
            return 0.0;
        }

        double totalPoints = 0.0;
        double totalCredits = 0.0;

        for (Map.Entry<String, Double> entry : courseGradePoints.entrySet()) {
            String course = entry.getKey();
            double gp = entry.getValue();
            double credits = courseCredits.getOrDefault(course, 3.0);
            totalPoints += (gp * credits);
            totalCredits += credits;
        }

        if (totalCredits > 0.0) {
            this.gpa = Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
        } else {
            this.gpa = 0.0;
        }
        return this.gpa;
    }

    /**
     * Adds course grade details with double credit hours and automatically recalculates GPA.
     *
     * @param courseCode course code
     * @param grade      letter grade
     * @param gradePoint grade point value
     * @param credits    credit hours (supports fractional, e.g. 1.5)
     */
    public void addCourseResult(String courseCode, String grade, double gradePoint, double credits) {
        courseGrades.put(courseCode, grade);
        courseGradePoints.put(courseCode, gradePoint);
        courseCredits.put(courseCode, credits);
        calculateGPA();
    }

    /**
     * Adds course grade details with integer credit hours (overload).
     *
     * @param courseCode course code
     * @param grade      letter grade
     * @param gradePoint grade point value
     * @param credits    credit hours
     */
    public void addCourseResult(String courseCode, String grade, double gradePoint, int credits) {
        addCourseResult(courseCode, grade, gradePoint, (double) credits);
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Map<String, String> getCourseGrades() {
        return courseGrades;
    }

    public void setCourseGrades(Map<String, String> courseGrades) {
        this.courseGrades = courseGrades;
    }

    public Map<String, Double> getCourseGradePoints() {
        return courseGradePoints;
    }

    public void setCourseGradePoints(Map<String, Double> courseGradePoints) {
        this.courseGradePoints = courseGradePoints;
    }

    public Map<String, Double> getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(Map<String, Double> courseCredits) {
        this.courseCredits = courseCredits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(resultId, result.resultId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultId);
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultId='" + resultId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", semester=" + semester +
                ", gpa=" + gpa +
                ", cgpa=" + cgpa +
                ", published=" + published +
                '}';
    }
}

