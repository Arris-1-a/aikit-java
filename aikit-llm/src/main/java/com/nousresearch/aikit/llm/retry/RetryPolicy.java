package com.nousresearch.aikit.llm.retry;

import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.exception.RateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Implements exponential backoff retry logic for LLM API calls.
 *
 * <p>Handles transient failures such as rate limiting (429), server
 * errors (5xx), and network timeouts. Uses full jitter to prevent
 * thundering herd problems in distributed systems.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * RetryPolicy policy = RetryPolicy.builder()
 *     .maxRetries(3)
 *     .baseDelay(Duration.ofSeconds(1))
 *     .build();
 *
 * ChatResponse response = policy.execute(() -> client.chat(request));
 * }</pre>
 */
public class RetryPolicy {

    private static final Logger log = LoggerFactory.getLogger(RetryPolicy.class);

    /** Default: 3 retries (4 total attempts) */
    private static final int DEFAULT_MAX_RETRIES = 3;

    /** Default: 1 second base delay */
    private static final Duration DEFAULT_BASE_DELAY = Duration.ofSeconds(1);

    /** Default: 60 seconds maximum delay */
    private static final Duration DEFAULT_MAX_DELAY = Duration.ofSeconds(60);

    /** HTTP status codes that are retryable */
    private static final Set<Integer> RETRYABLE_STATUS_CODES = new HashSet<>(
            Arrays.asList(429, 500, 502, 503, 504));

    private final int maxRetries;
    private final Duration baseDelay;
    private final Duration maxDelay;
    private final double backoffMultiplier;
    private final boolean useJitter;
    private final Predicate<Throwable> retryablePredicate;

    private RetryPolicy(Builder builder) {
        this.maxRetries = builder.maxRetries;
        this.baseDelay = builder.baseDelay;
        this.maxDelay = builder.maxDelay;
        this.backoffMultiplier = builder.backoffMultiplier;
        this.useJitter = builder.useJitter;
        this.retryablePredicate = builder.retryablePredicate;
    }

    /**
     * Executes a callable with retry logic.
     *
     * @param <T> the return type
     * @param callable the operation to execute
     * @return the result of the successful execution
     * @throws AiKitException if all retries are exhausted
     */
    public <T> T execute(Callable<T> callable) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    long delayMs = computeDelay(attempt);
                    log.debug("Retry attempt {}/{} after {}ms", attempt, maxRetries, delayMs);
                    Thread.sleep(delayMs);
                }
                return callable.call();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AiKitException("Retry interrupted", e);
            } catch (Exception e) {
                lastException = e;
                if (!isRetryable(e) || attempt >= maxRetries) {
                    break;
                }
                attempt++;
                log.warn("Attempt {} failed: {} — {}/{} retries left",
                        attempt, e.getMessage(), maxRetries - attempt, maxRetries);
            }
        }

        if (lastException instanceof AiKitException) {
            throw (AiKitException) lastException;
        }
        throw new AiKitException(
                "Operation failed after " + (maxRetries + 1) + " attempts", lastException);
    }

    /**
     * Determines if an exception is retryable.
     *
     * @param e the exception to check
     * @return true if the operation should be retried
     */
    public boolean isRetryable(Throwable e) {
        if (retryablePredicate != null) {
            return retryablePredicate.test(e);
        }

        // Rate limit exceptions are always retryable
        if (e instanceof RateLimitException) {
            return true;
        }

        // Check for HTTP status codes
        if (e instanceof AiKitException) {
            int status = ((AiKitException) e).getHttpStatusCode();
            return RETRYABLE_STATUS_CODES.contains(status);
        }

        // Network/timeout errors are retryable
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        return msg.contains("timeout") || msg.contains("connection")
                || msg.contains("network") || msg.contains("reset");
    }

    /**
     * Computes the delay for a given retry attempt using exponential backoff
     * with optional full jitter.
     *
     * @param attempt the current attempt number (1-based)
     * @return delay in milliseconds
     */
    private long computeDelay(int attempt) {
        double exponentialDelay = baseDelay.toMillis() * Math.pow(backoffMultiplier, attempt - 1);
        long cappedDelay = Math.min((long) exponentialDelay, maxDelay.toMillis());

        if (useJitter) {
            // Full jitter: random value between 0 and cappedDelay
            return (long) (Math.random() * cappedDelay);
        }
        return cappedDelay;
    }

    /** @return the maximum number of retries */
    public int getMaxRetries() { return maxRetries; }

    /** @return the base delay between retries */
    public Duration getBaseDelay() { return baseDelay; }

    /**
     * Creates a new Builder.
     * @return a Builder with default values
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for RetryPolicy.
     */
    public static class Builder {
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private Duration baseDelay = DEFAULT_BASE_DELAY;
        private Duration maxDelay = DEFAULT_MAX_DELAY;
        private double backoffMultiplier = 2.0;
        private boolean useJitter = true;
        private Predicate<Throwable> retryablePredicate;

        /** Sets the maximum number of retry attempts. */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries; return this;
        }

        /** Sets the initial delay before the first retry. */
        public Builder baseDelay(Duration baseDelay) {
            this.baseDelay = baseDelay; return this;
        }

        /** Sets the maximum delay cap for exponential backoff. */
        public Builder maxDelay(Duration maxDelay) {
            this.maxDelay = maxDelay; return this;
        }

        /** Sets the multiplier for exponential backoff (default 2.0). */
        public Builder backoffMultiplier(double multiplier) {
            this.backoffMultiplier = multiplier; return this;
        }

        /** Enables/disables jitter in backoff delays. */
        public Builder useJitter(boolean useJitter) {
            this.useJitter = useJitter; return this;
        }

        /** Sets a custom predicate for determining retryability. */
        public Builder retryablePredicate(Predicate<Throwable> predicate) {
            this.retryablePredicate = predicate; return this;
        }

        public RetryPolicy build() { return new RetryPolicy(this); }
    }
}
