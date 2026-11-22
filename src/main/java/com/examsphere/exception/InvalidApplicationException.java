package com.examsphere.exception;

/**
 * Exception thrown when an application request is invalid
 * (e.g., missing required fields, duplicate submission).
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class InvalidApplicationException extends Exception {

    /**
     * Constructs a new {@code InvalidApplicationException} with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidApplicationException(String message) {
        super(message);
    }
}
