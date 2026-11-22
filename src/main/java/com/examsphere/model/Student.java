package com.examsphere.model;

import java.io.Serializable;

/**
 * Represents a student in the EduAsset system.
 * <p>
 * Extends {@link User} with student-specific fields such as department,
 * program, semester, contact number, and enrollment date.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Student extends User implements Serializable {

    private static final long serialVersionUID = 2L;

    /** Student ID (mirrors userId via super). */
    private String studentId;

    /** Department the student belongs to (e.g., "CSE"). */
    private String department;

    /** Academic program (e.g., "B.Sc. in CSE"). */
    private String program;

    /** Current semester number. */
    private int semester;

    /** Contact phone number. */
    private String contactNumber;

    /** Enrollment date in "YYYY-MM-DD" format. */
    private String enrollmentDate;

    /**
     * Default no-argument constructor.
     */
    public Student() {
        super();
    }

    /**
     * Constructs a fully-initialised Student.
     *
     * @param studentId     unique student identifier (also used as userId)
     * @param fullName      full display name
     * @param email         email address
     * @param passwordHash  hashed password
     * @param department    department code
     * @param program       academic program name
     * @param semester      current semester number
     * @param contactNumber contact phone number
     */
    public Student(String studentId, String fullName, String email, String passwordHash,
                   String department, String program, int semester, String contactNumber) {
        super(studentId, fullName, email, passwordHash, "STUDENT");
        this.studentId = studentId;
        this.department = department;
        this.program = program;
        this.semester = semester;
        this.contactNumber = contactNumber;
        this.enrollmentDate = java.time.LocalDate.now().toString();
    }

    // ─── Abstract Method Implementations ────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * @return dashboard description containing the department
     */
    @Override
    public String getDashboardDescription() {
        return "Student Dashboard — " + department;
    }

    /**
     * {@inheritDoc}
     *
     * @return "Student"
     */
    @Override
    public String getDisplayRole() {
        return "Student";
    }

    /**
     * {@inheritDoc}
     * <p>
     * Searches across studentId, fullName, email, and department
     * (case-insensitive).
     * </p>
     */
    @Override
    public boolean matchesSearch(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        String lowerQuery = query.toLowerCase();
        return (studentId != null && studentId.toLowerCase().contains(lowerQuery))
                || (getFullName() != null && getFullName().toLowerCase().contains(lowerQuery))
                || (getEmail() != null && getEmail().toLowerCase().contains(lowerQuery))
                || (department != null && department.toLowerCase().contains(lowerQuery));
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the student ID.
     *
     * @return student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     *
     * @param studentId student ID to set
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
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
     * Returns the academic program.
     *
     * @return program name
     */
    public String getProgram() {
        return program;
    }

    /**
     * Sets the academic program.
     *
     * @param program program name to set
     */
    public void setProgram(String program) {
        this.program = program;
    }

    /**
     * Returns the current semester number.
     *
     * @return semester
     */
    public int getSemester() {
        return semester;
    }

    /**
     * Sets the current semester number.
     *
     * @param semester semester to set
     */
    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * Returns the contact phone number.
     *
     * @return contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the contact phone number.
     *
     * @param contactNumber contact number to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Returns the enrollment date.
     *
     * @return enrollment date in "YYYY-MM-DD" format
     */
    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    /**
     * Sets the enrollment date.
     *
     * @param enrollmentDate enrollment date to set
     */
    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * Returns a string representation of this student.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("Student{studentId='%s', fullName='%s', department='%s', semester=%d}",
                studentId, getFullName(), department, semester);
    }
}
