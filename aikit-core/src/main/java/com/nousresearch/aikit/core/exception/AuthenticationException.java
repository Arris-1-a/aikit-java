package com.nousresearch.aikit.core.exception;

/**
 * Thrown when API authentication fails (invalid or missing API key).
 */
public class AuthenticationException extends AiKitException {

    /**
     * Creates a new AuthenticationException.
     * @param message the error message
     */
    public AuthenticationException(String message) {
        super(message, "authentication_error", 401);
    }

    /**
     * Creates a new AuthenticationException with a cause.
     * @param message the error message
     * @param cause the underlying cause
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, 401);
    }
}
