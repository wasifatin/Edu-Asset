package com.examsphere.model;

import com.examsphere.interfaces.Approvable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a student academic application or grievance (e.g. recheck, retake, missing marks).
 * Implements {@link Approvable} for the administrative review and decision workflow.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ApplicationRequest implements Serializable, Approvable {

    private static final long serialVersionUID = 14L;

    private String applicationId;
    private String studentId;
    private String applicationType; // "RECHECK", "RETAKE", "MISSING_MARKS", "COMPLAINT"
    private String courseCode;
    private String description;
    private String status;          // "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED", "COMPLETED"
    private String adminComments;
    private String submissionDate;
    private String lastUpdated;

    public ApplicationRequest() {
        this.status = "PENDING";
        this.submissionDate = LocalDate.now().toString();
        this.lastUpdated = LocalDate.now().toString();
    }

    public ApplicationRequest(String applicationId, String studentId, String applicationType, String description) {
        this();
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.applicationType = applicationType;
        this.description = description;
    }

    public ApplicationRequest(String applicationId, String studentId, String applicationType,
                              String courseCode, String description) {
        this(applicationId, studentId, applicationType, description);
        this.courseCode = courseCode;
    }

    // ─── Approvable Implementation ──────────────────────────────────────

    @Override
    public void approve(String comments) {
        this.status = "APPROVED";
        this.adminComments = comments;
        this.lastUpdated = LocalDate.now().toString();
    }

    @Override
    public void reject(String comments) {
        this.status = "REJECTED";
        this.adminComments = comments;
        this.lastUpdated = LocalDate.now().toString();
    }

    @Override
    public String getApprovalStatus() {
        return this.status;
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDate.now().toString();
    }

    public String getAdminComments() {
        return adminComments;
    }

    public void setAdminComments(String adminComments) {
        this.adminComments = adminComments;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationRequest that = (ApplicationRequest) o;
        return Objects.equals(applicationId, that.applicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId);
    }

    @Override
    public String toString() {
        return "ApplicationRequest{" +
                "applicationId='" + applicationId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", applicationType='" + applicationType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

