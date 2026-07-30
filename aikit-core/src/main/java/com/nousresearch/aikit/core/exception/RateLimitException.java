package com.nousresearch.aikit.core.exception;

import java.time.Duration;

/**
 * Thrown when an API rate limit is exceeded.
 *
 * <p>Contains the recommended retry-after duration from the provider.</p>
 */
public class RateLimitException extends AiKitException {

    private final Duration retryAfter;

    /**
     * Creates a new RateLimitException.
     * @param message the error message
     * @param retryAfter the recommended wait time before retrying
     */
    public RateLimitException(String message, Duration retryAfter) {
        super(message, "rate_limit_exceeded", 429);
        this.retryAfter = retryAfter;
    }

    /**
     * Returns the recommended wait time before retrying.
     * @return the retry-after duration
     */
    public Duration getRetryAfter() { return retryAfter; }

    @Override
    public String toString() {
        return super.toString() + " retryAfter=" + retryAfter;
    }
}
