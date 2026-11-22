package com.examsphere.exception;

/**
 * Exception thrown when a data storage operation fails.
 * <p>
 * This exception wraps underlying I/O errors that occur during
 * serialization, deserialization, or file system operations.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class DataStorageException extends Exception {

    /**
     * Constructs a new {@code DataStorageException} with the specified detail message.
     *
     * @param message the detail message
     */
    public DataStorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code DataStorageException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of this exception
     */
    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
