package com.examsphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an academic course offered by a department.
 * <p>
 * Tracks the course code, name, department, credit hours, semester,
 * assigned teacher, and enrolled students.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 7L;

    /** Unique course code (e.g., "CSE221"). */
    private String courseCode;

    /** Human-readable course name. */
    private String courseName;

    /** Department this course belongs to. */
    private String departmentId;

    /** Credit hours for this course (supports fractional values, e.g. 1.5, 3.0). */
    private double credits;

    /** Course evaluation scheme: "THEORY" or "LAB". */
    private String courseType = "THEORY";

    /** Semester in which this course is offered. */
    private int semester;

    /** User ID of the assigned teacher. */
    private String teacherId;

    /** List of enrolled student IDs. */
    private List<String> enrolledStudentIds;

    /**
     * Default no-argument constructor.
     */
    public Course() {
        this.enrolledStudentIds = new ArrayList<>();
    }

    /**
     * Constructs a Course with double credit hours.
     *
     * @param courseCode   unique course code
     * @param courseName   human-readable name
     * @param departmentId department this course belongs to
     * @param credits      credit hours (supports fractional, e.g. 1.5)
     * @param semester     semester number
     */
    public Course(String courseCode, String courseName, String departmentId, double credits, int semester) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.departmentId = departmentId;
        this.credits = credits;
        this.semester = semester;
        this.enrolledStudentIds = new ArrayList<>();
    }

    /**
     * Constructs a Course with integer credit hours.
     *
     * @param courseCode   unique course code
     * @param courseName   human-readable name
     * @param departmentId department this course belongs to
     * @param credits      credit hours
     * @param semester     semester number
     */
    public Course(String courseCode, String courseName, String departmentId, int credits, int semester) {
        this(courseCode, courseName, departmentId, (double) credits, semester);
    }

    /**
     * Enrolls a student in this course if not already enrolled.
     *
     * @param studentId the student ID to enroll
     */
    public void enrollStudent(String studentId) {
        if (!enrolledStudentIds.contains(studentId)) {
            enrolledStudentIds.add(studentId);
        }
    }

    /**
     * Removes a student from this course.
     *
     * @param studentId the student ID to remove
     */
    public void removeStudent(String studentId) {
        enrolledStudentIds.remove(studentId);
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the course code.
     *
     * @return course code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Sets the course code.
     *
     * @param courseCode course code to set
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /**
     * Returns the course name.
     *
     * @return course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the course name.
     *
     * @param courseName course name to set
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Returns the department ID.
     *
     * @return department ID
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * Sets the department ID.
     *
     * @param departmentId department ID to set
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * Returns the credit hours (double).
     *
     * @return credits
     */
    /**
     * Returns the course evaluation type ("THEORY" or "LAB").
     *
     * @return course type
     */
    public String getCourseType() {
        if (courseType == null) {
            if (courseName != null && courseName.toLowerCase().contains("lab")) return "LAB";
            if (courseCode != null && courseCode.toLowerCase().contains("lab")) return "LAB";
            return "THEORY";
        }
        return courseType;
    }

    /**
     * Sets the course evaluation type.
     *
     * @param courseType "THEORY" or "LAB"
     */
    public void setCourseType(String courseType) {
        this.courseType = courseType != null ? courseType.toUpperCase() : "THEORY";
    }

    /**
     * Checks if this course is a laboratory/practical course.
     *
     * @return true if lab
     */
    public boolean isLabCourse() {
        return "LAB".equalsIgnoreCase(getCourseType());
    }

    public double getCredits() {
        return credits;
    }

    /**
     * Sets the credit hours (double).
     *
     * @param credits credits to set
     */
    public void setCredits(double credits) {
        this.credits = credits;
    }

    /**
     * Sets the credit hours (integer overload).
     *
     * @param credits credits to set
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * Returns the semester number.
     *
     * @return semester
     */
    public int getSemester() {
        return semester;
    }

    /**
     * Sets the semester number.
     *
     * @param semester semester to set
     */
    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * Returns the assigned teacher's user ID.
     *
     * @return teacher ID (may be {@code null} if unassigned)
     */
    public String getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the assigned teacher's user ID.
     *
     * @param teacherId teacher ID to set
     */
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Returns the list of enrolled student IDs.
     *
     * @return enrolled student ID list
     */
    public List<String> getEnrolledStudentIds() {
        return enrolledStudentIds;
    }

    /**
     * Sets the list of enrolled student IDs.
     *
     * @param enrolledStudentIds student ID list to set
     */
    public void setEnrolledStudentIds(List<String> enrolledStudentIds) {
        this.enrolledStudentIds = enrolledStudentIds;
    }

    /**
     * Returns a string representation of this course.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("Course{courseCode='%s', courseName='%s', departmentId='%s', credits=%.2f}",
                courseCode, courseName, departmentId, credits);
    }

    /**
     * Two courses are equal if they share the same courseCode.
     *
     * @param o the object to compare with
     * @return {@code true} if courseCodes are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseCode, course.courseCode);
    }

    /**
     * Hash code based on courseCode.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }
}
