package com.nousresearch.aikit.core.exception;

/**
 * Base exception class for all AiKit errors.
 *
 * <p>Subclasses provide specific error types for different failure modes.</p>
 */
public class AiKitException extends RuntimeException {

    private final String errorCode;
    private final int httpStatusCode;

    /**
     * Creates a new AiKitException.
     * @param message the error message
     */
    public AiKitException(String message) {
        this(message, null, -1);
    }

    /**
     * Creates a new AiKitException with a cause.
     * @param message the error message
     * @param cause the underlying cause
     */
    public AiKitException(String message, Throwable cause) {
        this(message, cause, -1);
    }

    /**
     * Creates a new AiKitException with full details.
     * @param message the error message
     * @param cause the underlying cause
     * @param httpStatusCode the HTTP status code if applicable
     */
    public AiKitException(String message, Throwable cause, int httpStatusCode) {
        super(message, cause);
        this.errorCode = null;
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Creates a new AiKitException with an error code.
     * @param message the error message
     * @param errorCode the provider error code
     * @param httpStatusCode the HTTP status code
     */
    public AiKitException(String message, String errorCode, int httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }

    /** @return the provider-specific error code, or null */
    public String getErrorCode() { return errorCode; }

    /** @return the HTTP status code, or -1 if not HTTP-related */
    public int getHttpStatusCode() { return httpStatusCode; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{message='" + getMessage()
                + "', errorCode='" + errorCode + "', httpStatus=" + httpStatusCode + "}";
    }
}
