package com.examsphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an academic department within the institution.
 * <p>
 * Maintains a list of programs offered and courses belonging to this department.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Department implements Serializable {

    private static final long serialVersionUID = 6L;

    /** Unique department identifier (e.g., "CSE"). */
    private String departmentId;

    /** Full department name (e.g., "Computer Science and Engineering"). */
    private String departmentName;

    /** Name or ID of the head of department. */
    private String headOfDepartment;

    /** Programs offered by this department (e.g., "B.Sc. in CSE"). */
    private List<String> programs;

    /** Course codes belonging to this department. */
    private List<String> courseIds;

    /**
     * Default no-argument constructor.
     */
    public Department() {
        this.programs = new ArrayList<>();
        this.courseIds = new ArrayList<>();
    }

    /**
     * Constructs a Department with the given ID and name.
     *
     * @param departmentId   unique department identifier
     * @param departmentName full department name
     */
    public Department(String departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.programs = new ArrayList<>();
        this.courseIds = new ArrayList<>();
    }

    // ─── Getters and Setters ────────────────────────────────────────────

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
     * Returns the full department name.
     *
     * @return department name
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the full department name.
     *
     * @param departmentName department name to set
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * Returns the head of department.
     *
     * @return head of department name or ID
     */
    public String getHeadOfDepartment() {
        return headOfDepartment;
    }

    /**
     * Sets the head of department.
     *
     * @param headOfDepartment head of department name or ID to set
     */
    public void setHeadOfDepartment(String headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }

    /**
     * Returns the list of programs offered.
     *
     * @return list of program names
     */
    public List<String> getPrograms() {
        return programs;
    }

    /**
     * Sets the list of programs offered.
     *
     * @param programs program list to set
     */
    public void setPrograms(List<String> programs) {
        this.programs = programs;
    }

    /**
     * Returns the list of course codes in this department.
     *
     * @return list of course codes
     */
    public List<String> getCourseIds() {
        return courseIds;
    }

    /**
     * Sets the list of course codes in this department.
     *
     * @param courseIds course code list to set
     */
    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }

    /**
     * Adds a program to this department if not already present.
     *
     * @param program the program name to add
     */
    public void addProgram(String program) {
        if (!programs.contains(program)) {
            programs.add(program);
        }
    }

    /**
     * Removes a program from this department.
     *
     * @param program the program name to remove
     */
    public void removeProgram(String program) {
        programs.remove(program);
    }

    /**
     * Adds a course to this department if not already present.
     *
     * @param courseId the course code to add
     */
    public void addCourse(String courseId) {
        if (!courseIds.contains(courseId)) {
            courseIds.add(courseId);
        }
    }

    /**
     * Removes a course from this department.
     *
     * @param courseId the course code to remove
     */
    public void removeCourse(String courseId) {
        courseIds.remove(courseId);
    }

    /**
     * Returns a string representation of this department.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("Department{departmentId='%s', departmentName='%s'}", departmentId, departmentName);
    }

    /**
     * Two departments are equal if they share the same departmentId.
     *
     * @param o the object to compare with
     * @return {@code true} if departmentIds are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(departmentId, that.departmentId);
    }

    /**
     * Hash code based on departmentId.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(departmentId);
    }
}
