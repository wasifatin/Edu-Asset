package com.examsphere.exception;

/**
 * Exception thrown when marks entered for a student are invalid
 * (e.g., negative values or exceeding the maximum allowed).
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class InvalidMarksException extends Exception {

    /**
     * Constructs a new {@code InvalidMarksException} with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidMarksException(String message) {
        super(message);
    }
}
