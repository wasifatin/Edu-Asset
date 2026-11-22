package com.examsphere.exception;

/**
 * Exception thrown when login credentials are invalid
 * (e.g., wrong user ID, wrong password, or inactive account).
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class InvalidLoginException extends Exception {

    /**
     * Constructs a new {@code InvalidLoginException} with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidLoginException(String message) {
        super(message);
    }
}
