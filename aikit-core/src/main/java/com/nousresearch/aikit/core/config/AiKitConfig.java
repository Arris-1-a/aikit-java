package com.nousresearch.aikit.core.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Global configuration for the AiKit framework.
 *
 * <p>Provides default settings for LLM clients, embedding providers,
 * vector stores, and agent behaviors. Configuration values can be
 * overridden via system properties or programmatic calls.</p>
 */
public class AiKitConfig {

    private String defaultLlmProvider = "openai";
    private String defaultEmbeddingProvider = "openai";
    private Duration defaultTimeout = Duration.ofSeconds(60);
    private Duration defaultConnectTimeout = Duration.ofSeconds(10);
    private Duration defaultReadTimeout = Duration.ofSeconds(60);
    private int maxRetries = 3;
    private Duration retryDelay = Duration.ofSeconds(1);
    private int maxConnections = 20;
    private int maxRequestsPerHost = 10;
    private boolean enableCaching = true;
    private int embeddingCacheSize = 1000;
    private Duration embeddingCacheTtl = Duration.ofHours(1);
    private final Map<String, String> providerApiKeys = new HashMap<>();

    private static final AiKitConfig INSTANCE = new AiKitConfig();

    private AiKitConfig() {}

    /**
     * Returns the singleton configuration instance.
     * @return the global configuration
     */
    public static AiKitConfig getInstance() { return INSTANCE; }

    /** @return the default LLM provider type */
    public String getDefaultLlmProvider() { return defaultLlmProvider; }
    public void setDefaultLlmProvider(String provider) { this.defaultLlmProvider = provider; }

    /** @return the default embedding provider type */
    public String getDefaultEmbeddingProvider() { return defaultEmbeddingProvider; }
    public void setDefaultEmbeddingProvider(String provider) { this.defaultEmbeddingProvider = provider; }

    /** @return the default request timeout */
    public Duration getDefaultTimeout() { return defaultTimeout; }
    public void setDefaultTimeout(Duration timeout) { this.defaultTimeout = timeout; }

    /** @return the default connection timeout */
    public Duration getDefaultConnectTimeout() { return defaultConnectTimeout; }
    public void setDefaultConnectTimeout(Duration timeout) { this.defaultConnectTimeout = timeout; }

    /** @return the default read timeout */
    public Duration getDefaultReadTimeout() { return defaultReadTimeout; }
    public void setDefaultReadTimeout(Duration timeout) { this.defaultReadTimeout = timeout; }

    /** @return the maximum number of retries on failure */
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    /** @return the delay between retries */
    public Duration getRetryDelay() { return retryDelay; }
    public void setRetryDelay(Duration delay) { this.retryDelay = delay; }

    /** @return the maximum HTTP connections in the pool */
    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }

    /** @return maximum concurrent requests per host */
    public int getMaxRequestsPerHost() { return maxRequestsPerHost; }
    public void setMaxRequestsPerHost(int maxRequestsPerHost) { this.maxRequestsPerHost = maxRequestsPerHost; }

    /** @return whether response caching is enabled */
    public boolean isEnableCaching() { return enableCaching; }
    public void setEnableCaching(boolean enableCaching) { this.enableCaching = enableCaching; }

    /** @return maximum embedding cache entries */
    public int getEmbeddingCacheSize() { return embeddingCacheSize; }
    public void setEmbeddingCacheSize(int size) { this.embeddingCacheSize = size; }

    /** @return embedding cache time-to-live */
    public Duration getEmbeddingCacheTtl() { return embeddingCacheTtl; }
    public void setEmbeddingCacheTtl(Duration ttl) { this.embeddingCacheTtl = ttl; }

    /**
     * Sets the API key for a specific provider.
     * @param provider the provider name (e.g., "openai", "anthropic")
     * @param apiKey the API key
     */
    public void setApiKey(String provider, String apiKey) {
        this.providerApiKeys.put(provider.toLowerCase(), apiKey);
    }

    /**
     * Gets the API key for a specific provider.
     * @param provider the provider name
     * @return the API key, or null if not set
     */
    public String getApiKey(String provider) {
        return providerApiKeys.get(provider.toLowerCase());
    }

    /**
     * Creates a builder for constructing an AiKitConfig.
     * @return a new Builder
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for AiKitConfig.
     */
    public static class Builder {
        private final AiKitConfig config = new AiKitConfig();

        public Builder defaultLlmProvider(String provider) {
            config.defaultLlmProvider = provider; return this;
        }

        public Builder defaultEmbeddingProvider(String provider) {
            config.defaultEmbeddingProvider = provider; return this;
        }

        public Builder timeout(Duration timeout) {
            config.defaultTimeout = timeout; return this;
        }

        public Builder maxRetries(int maxRetries) {
            config.maxRetries = maxRetries; return this;
        }

        public Builder maxConnections(int maxConnections) {
            config.maxConnections = maxConnections; return this;
        }

        public Builder apiKey(String provider, String apiKey) {
            config.providerApiKeys.put(provider.toLowerCase(), apiKey); return this;
        }

        public Builder enableCaching(boolean enable) {
            config.enableCaching = enable; return this;
        }

        public AiKitConfig build() { return config; }
    }
}
