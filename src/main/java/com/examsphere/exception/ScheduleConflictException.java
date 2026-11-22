package com.examsphere.exception;

/**
 * Exception thrown when an exam scheduling conflict is detected
 * (e.g., room or invigilator double-booked for the same time slot).
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ScheduleConflictException extends Exception {

    /**
     * Constructs a new {@code ScheduleConflictException} with the specified detail message.
     *
     * @param message the detail message
     */
    public ScheduleConflictException(String message) {
        super(message);
    }
}
