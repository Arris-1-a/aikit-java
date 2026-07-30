package com.nousresearch.aikit.core.exception;

import java.time.Duration;

/**
 * Thrown when a request times out.
 */
public class TimeoutException extends AiKitException {

    private final Duration timeout;

    /**
     * Creates a new TimeoutException.
     * @param message the error message
     * @param timeout the timeout duration that was exceeded
     */
    public TimeoutException(String message, Duration timeout) {
        super(message, "timeout", -1);
        this.timeout = timeout;
    }

    /** @return the timeout duration that was exceeded */
    public Duration getTimeout() { return timeout; }
}
