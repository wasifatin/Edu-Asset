package com.examsphere.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a scheduled examination in the EduAsset system.
 * <p>
 * Tracks the exam's course, type, date/time window, assigned room
 * and invigilator, and current lifecycle status.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Exam implements Serializable {

    private static final long serialVersionUID = 8L;

    /** Unique exam identifier (e.g., "EX-2024-001"). */
    private String examId;

    /** Course code this exam is for. */
    private String courseCode;

    /** Type of exam: "MIDTERM", "FINAL", "CLASS_TEST", "QUIZ". */
    private String examType;

    /** Semester in which this exam takes place. */
    private int semester;

    /** Date of the exam in "YYYY-MM-DD" format. */
    private String examDate;

    /** Start time in "HH:mm" format. */
    private String startTime;

    /** End time in "HH:mm" format. */
    private String endTime;

    /** Room ID where the exam is held. */
    private String roomId;

    /** User ID of the assigned invigilator (teacher). */
    private String invigilatorId;

    /** Lifecycle status: "SCHEDULED", "ONGOING", "COMPLETED", "CANCELLED". */
    private String status;

    /**
     * Default no-argument constructor.
     */
    public Exam() {
        this.status = "SCHEDULED";
    }

    /**
     * Constructs an Exam with the essential scheduling details.
     *
     * @param examId    unique exam identifier
     * @param courseCode course code
     * @param examType  type of exam
     * @param semester  semester number
     * @param examDate  date in "YYYY-MM-DD"
     * @param startTime start time in "HH:mm"
     * @param endTime   end time in "HH:mm"
     */
    public Exam(String examId, String courseCode, String examType, int semester,
                String examDate, String startTime, String endTime) {
        this.examId = examId;
        this.courseCode = courseCode;
        this.examType = examType;
        this.semester = semester;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "SCHEDULED";
    }

    // ─── Getters and Setters ────────────────────────────────────────────

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
     * Returns the exam type.
     *
     * @return exam type
     */
    public String getExamType() {
        return examType;
    }

    /**
     * Sets the exam type.
     *
     * @param examType exam type to set
     */
    public void setExamType(String examType) {
        this.examType = examType;
    }

    /**
     * Returns the semester.
     *
     * @return semester number
     */
    public int getSemester() {
        return semester;
    }

    /**
     * Sets the semester.
     *
     * @param semester semester number to set
     */
    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * Returns the exam date.
     *
     * @return date in "YYYY-MM-DD" format
     */
    public String getExamDate() {
        return examDate;
    }

    /**
     * Sets the exam date.
     *
     * @param examDate date in "YYYY-MM-DD" format
     */
    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    /**
     * Returns the start time.
     *
     * @return start time in "HH:mm" format
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime start time in "HH:mm" format
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the end time.
     *
     * @return end time in "HH:mm" format
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime end time in "HH:mm" format
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the room ID.
     *
     * @return room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets the room ID.
     *
     * @param roomId room ID to set
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns the invigilator ID.
     *
     * @return invigilator (teacher) user ID
     */
    public String getInvigilatorId() {
        return invigilatorId;
    }

    /**
     * Sets the invigilator ID.
     *
     * @param invigilatorId invigilator user ID to set
     */
    public void setInvigilatorId(String invigilatorId) {
        this.invigilatorId = invigilatorId;
    }

    /**
     * Returns the current status.
     *
     * @return status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status.
     *
     * @param status status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a string representation of this exam.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("Exam{examId='%s', courseCode='%s', examType='%s', examDate='%s', status='%s'}",
                examId, courseCode, examType, examDate, status);
    }

    /**
     * Two exams are equal if they share the same examId.
     *
     * @param o the object to compare with
     * @return {@code true} if examIds are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return Objects.equals(examId, exam.examId);
    }

    /**
     * Hash code based on examId.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(examId);
    }
}
