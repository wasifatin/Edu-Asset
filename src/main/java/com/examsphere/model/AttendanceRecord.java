package com.examsphere.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Records a student's attendance for a specific exam.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class AttendanceRecord implements Serializable {

    private static final long serialVersionUID = 11L;

    /** Unique attendance record identifier. */
    private String attendanceId;

    /** Exam this attendance record belongs to. */
    private String examId;

    /** Student whose attendance is being recorded. */
    private String studentId;

    /** Attendance status: "PRESENT", "ABSENT", or "LATE". */
    private String status;

    /** Optional remarks about the attendance. */
    private String remarks;

    /** ISO datetime timestamp of when attendance was recorded. */
    private String timestamp;

    /**
     * Default no-argument constructor.
     */
    public AttendanceRecord() {
    }

    /**
     * Constructs an AttendanceRecord with essential fields.
     *
     * @param attendanceId unique attendance record identifier
     * @param examId       exam identifier
     * @param studentId    student identifier
     * @param status       attendance status ("PRESENT", "ABSENT", "LATE")
     */
    public AttendanceRecord(String attendanceId, String examId, String studentId, String status) {
        this.attendanceId = attendanceId;
        this.examId = examId;
        this.studentId = studentId;
        this.status = status;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the attendance record ID.
     *
     * @return attendance ID
     */
    public String getAttendanceId() {
        return attendanceId;
    }

    /**
     * Sets the attendance record ID.
     *
     * @param attendanceId attendance ID to set
     */
    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    /**
     * Returns the exam ID.
     *
     * @return exam ID
     */
    public String getExamId() {
        return examId;
    }

    /**
     * Sets the exam ID.
     *
     * @param examId exam ID to set
     */
    public void setExamId(String examId) {
        this.examId = examId;
    }

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
     * Returns the attendance status.
     *
     * @return status ("PRESENT", "ABSENT", "LATE")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the attendance status.
     *
     * @param status status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the remarks.
     *
     * @return remarks (may be {@code null})
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks.
     *
     * @param remarks remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Returns the timestamp when attendance was recorded.
     *
     * @return ISO datetime timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp ISO datetime timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a string representation of this attendance record.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("AttendanceRecord{attendanceId='%s', examId='%s', studentId='%s', status='%s'}",
                attendanceId, examId, studentId, status);
    }

    /**
     * Two attendance records are equal if they share the same attendanceId.
     *
     * @param o the object to compare with
     * @return {@code true} if attendanceIds are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return Objects.equals(attendanceId, that.attendanceId);
    }

    /**
     * Hash code based on attendanceId.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(attendanceId);
    }
}
