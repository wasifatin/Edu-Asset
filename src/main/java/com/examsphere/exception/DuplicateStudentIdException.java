package com.examsphere.exception;

/**
 * Exception thrown when attempting to register a student with an ID that already exists.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class DuplicateStudentIdException extends Exception {

    /**
     * Constructs a new {@code DuplicateStudentIdException} with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicateStudentIdException(String message) {
        super(message);
    }
}
