package com.nousresearch.aikit.llm;

import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.exception.RateLimitException;
import com.nousresearch.aikit.llm.retry.RetryPolicy;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetryPolicyTest {

    @Test
    void shouldRetryOnRateLimit() {
        RetryPolicy policy = RetryPolicy.builder()
                .maxRetries(2)
                .baseDelay(Duration.ofMillis(10))
                .useJitter(false)
                .build();

        AtomicInteger attempts = new AtomicInteger(0);
        assertThatThrownBy(() -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new RateLimitException("Too many", Duration.ofSeconds(1));
        })).isInstanceOf(RateLimitException.class);

        assertThat(attempts.get()).isEqualTo(3); // 1 initial + 2 retries
    }

    @Test
    void shouldSucceedOnRecovery() {
        RetryPolicy policy = RetryPolicy.builder()
                .maxRetries(3)
                .baseDelay(Duration.ofMillis(5))
                .useJitter(false)
                .build();

        AtomicInteger attempts = new AtomicInteger(0);
        String result = policy.execute(() -> {
            int a = attempts.incrementAndGet();
            if (a < 3) {
                throw new AiKitException("Transient error", 503);
            }
            return "success";
        });

        assertThat(result).isEqualTo("success");
        assertThat(attempts.get()).isEqualTo(3);
    }

    @Test
    void shouldNotRetryNonRetryable() {
        RetryPolicy policy = RetryPolicy.builder()
                .maxRetries(2)
                .baseDelay(Duration.ofMillis(5))
                .build();

        AtomicInteger attempts = new AtomicInteger(0);
        assertThatThrownBy(() -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new AiKitException("Bad request", 400);
        })).isInstanceOf(AiKitException.class);

        assertThat(attempts.get()).isEqualTo(1); // No retries
    }

    @Test
    void shouldUseDefaultValues() {
        RetryPolicy policy = RetryPolicy.builder().build();
        assertThat(policy.getMaxRetries()).isEqualTo(3);
        assertThat(policy.getBaseDelay()).isEqualTo(Duration.ofSeconds(1));
    }
}
