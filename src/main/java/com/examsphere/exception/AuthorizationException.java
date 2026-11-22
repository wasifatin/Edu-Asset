package com.examsphere.exception;

/**
 * Exception thrown when a user attempts an action they are not authorized to perform.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class AuthorizationException extends Exception {

    /**
     * Constructs a new {@code AuthorizationException} with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthorizationException(String message) {
        super(message);
    }
}
