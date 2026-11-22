package com.examsphere.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the assignment of a specific seat to a student for an exam.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class SeatAssignment implements Serializable {

    private static final long serialVersionUID = 10L;

    /** Unique seat assignment identifier. */
    private String seatId;

    /** Exam this seat assignment belongs to. */
    private String examId;

    /** Room where the seat is located. */
    private String roomId;

    /** Student assigned to this seat. */
    private String studentId;

    /** Seat number within the room. */
    private int seatNumber;

    /**
     * Default no-argument constructor.
     */
    public SeatAssignment() {
    }

    /**
     * Constructs a SeatAssignment with all fields.
     *
     * @param seatId     unique seat assignment identifier
     * @param examId     exam identifier
     * @param roomId     room identifier
     * @param studentId  student identifier
     * @param seatNumber seat number within the room
     */
    public SeatAssignment(String seatId, String examId, String roomId, String studentId, int seatNumber) {
        this.seatId = seatId;
        this.examId = examId;
        this.roomId = roomId;
        this.studentId = studentId;
        this.seatNumber = seatNumber;
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the seat assignment ID.
     *
     * @return seat ID
     */
    public String getSeatId() {
        return seatId;
    }

    /**
     * Sets the seat assignment ID.
     *
     * @param seatId seat ID to set
     */
    public void setSeatId(String seatId) {
        this.seatId = seatId;
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
     * Returns the seat number.
     *
     * @return seat number
     */
    public int getSeatNumber() {
        return seatNumber;
    }

    /**
     * Sets the seat number.
     *
     * @param seatNumber seat number to set
     */
    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    /**
     * Returns a string representation of this seat assignment.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("SeatAssignment{seatId='%s', examId='%s', roomId='%s', studentId='%s', seat=%d}",
                seatId, examId, roomId, studentId, seatNumber);
    }

    /**
     * Two seat assignments are equal if they share the same seatId.
     *
     * @param o the object to compare with
     * @return {@code true} if seatIds are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatAssignment that = (SeatAssignment) o;
        return Objects.equals(seatId, that.seatId);
    }

    /**
     * Hash code based on seatId.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(seatId);
    }
}
