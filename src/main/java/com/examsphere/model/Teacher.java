package com.examsphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a teacher (faculty member / invigilator) in the EduAsset system.
 * <p>
 * Extends {@link User} with teacher-specific fields including department,
 * designation, and assigned course list.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Teacher extends User implements Serializable {

    private static final long serialVersionUID = 3L;

    /** Teacher ID (mirrors userId via super). */
    private String teacherId;

    /** Department the teacher belongs to. */
    private String department;

    /** Designation (e.g., "Lecturer", "Professor"). */
    private String designation;

    /** List of course codes assigned to this teacher. */
    private List<String> assignedCourseIds;

    /**
     * Default no-argument constructor.
     */
    public Teacher() {
        super();
        this.assignedCourseIds = new ArrayList<>();
    }

    /**
     * Constructs a fully-initialised Teacher.
     *
     * @param teacherId    unique teacher identifier (also used as userId)
     * @param fullName     full display name
     * @param email        email address
     * @param passwordHash hashed password
     * @param department   department code
     * @param designation  designation title
     */
    public Teacher(String teacherId, String fullName, String email, String passwordHash,
                   String department, String designation) {
        super(teacherId, fullName, email, passwordHash, "TEACHER");
        this.teacherId = teacherId;
        this.department = department;
        this.designation = designation;
        this.assignedCourseIds = new ArrayList<>();
    }

    // ─── Abstract Method Implementations ────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * @return dashboard description containing the department
     */
    @Override
    public String getDashboardDescription() {
        return "Faculty Dashboard — " + department;
    }

    /**
     * {@inheritDoc}
     *
     * @return "Teacher / Invigilator"
     */
    @Override
    public String getDisplayRole() {
        return "Teacher / Invigilator";
    }

    /**
     * {@inheritDoc}
     * <p>
     * Searches across teacherId, fullName, email, department, and designation
     * (case-insensitive).
     * </p>
     */
    @Override
    public boolean matchesSearch(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        String lowerQuery = query.toLowerCase();
        return (teacherId != null && teacherId.toLowerCase().contains(lowerQuery))
                || (getFullName() != null && getFullName().toLowerCase().contains(lowerQuery))
                || (getEmail() != null && getEmail().toLowerCase().contains(lowerQuery))
                || (department != null && department.toLowerCase().contains(lowerQuery))
                || (designation != null && designation.toLowerCase().contains(lowerQuery));
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the teacher ID.
     *
     * @return teacher ID
     */
    public String getTeacherId() {
        return teacherId;
    }

    /**
     * Sets the teacher ID.
     *
     * @param teacherId teacher ID to set
     */
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Returns the department code.
     *
     * @return department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the department code.
     *
     * @param department department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Returns the designation title.
     *
     * @return designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * Sets the designation title.
     *
     * @param designation designation to set
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Returns the list of assigned course codes.
     *
     * @return list of course codes
     */
    public List<String> getAssignedCourseIds() {
        return assignedCourseIds;
    }

    /**
     * Adds an assigned course code to this teacher's list.
     *
     * @param courseCode course code to add
     */
    public void addAssignedCourse(String courseCode) {
        if (courseCode != null && !assignedCourseIds.contains(courseCode)) {
            assignedCourseIds.add(courseCode);
        }
    }

    /**
     * Removes an assigned course code from this teacher's list.
     *
     * @param courseCode course code to remove
     */
    public void removeAssignedCourse(String courseCode) {
        if (courseCode != null) {
            assignedCourseIds.remove(courseCode);
        }
    }

    /**
     * Sets the list of assigned course codes.
     *
     * @param assignedCourseIds course code list to set
     */
    public void setAssignedCourseIds(List<String> assignedCourseIds) {
        this.assignedCourseIds = assignedCourseIds;
    }

    /**
     * Assigns an additional course to this teacher.
     *
     * @param courseId the course code to add
     */
    public void addCourse(String courseId) {
        if (!assignedCourseIds.contains(courseId)) {
            assignedCourseIds.add(courseId);
        }
    }

    /**
     * Removes a course assignment from this teacher.
     *
     * @param courseId the course code to remove
     */
    public void removeCourse(String courseId) {
        assignedCourseIds.remove(courseId);
    }

    /**
     * Returns a string representation of this teacher.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("Teacher{teacherId='%s', fullName='%s', department='%s', designation='%s'}",
                teacherId, getFullName(), department, designation);
    }
}
