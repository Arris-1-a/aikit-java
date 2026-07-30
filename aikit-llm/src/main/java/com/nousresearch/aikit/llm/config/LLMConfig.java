package com.nousresearch.aikit.llm.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration holder for LLM clients.
 *
 * <p>Use {@link Builder} for fluent construction or set properties directly.
 * Supports all provider types with per-provider API key storage.</p>
 */
public class LLMConfig {

    private String provider = "openai";
    private String model;
    private String apiKey;
    private String baseUrl;
    private Duration timeout = Duration.ofSeconds(60);
    private Duration connectTimeout = Duration.ofSeconds(10);
    private Duration readTimeout = Duration.ofSeconds(60);
    private int maxRetries = 3;
    private Duration retryDelay = Duration.ofSeconds(1);
    private int maxConnections = 20;
    private int maxRequestsPerHost = 10;
    private boolean streamByDefault = false;
    private final Map<String, String> customHeaders = new HashMap<>();

    // Getters and setters

    /** @return the LLM provider type */
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    /** @return the model name */
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    /** @return the API key */
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    /** @return the API base URL */
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    /** @return the request timeout */
    public Duration getTimeout() { return timeout; }
    public void setTimeout(Duration timeout) { this.timeout = timeout; }

    /** @return the connection timeout */
    public Duration getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(Duration connectTimeout) { this.connectTimeout = connectTimeout; }

    /** @return the read timeout */
    public Duration getReadTimeout() { return readTimeout; }
    public void setReadTimeout(Duration readTimeout) { this.readTimeout = readTimeout; }

    /** @return max retries on failure */
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    /** @return delay between retries */
    public Duration getRetryDelay() { return retryDelay; }
    public void setRetryDelay(Duration retryDelay) { this.retryDelay = retryDelay; }

    /** @return max connections in the pool */
    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }

    /** @return max requests per host */
    public int getMaxRequestsPerHost() { return maxRequestsPerHost; }
    public void setMaxRequestsPerHost(int maxRequestsPerHost) { this.maxRequestsPerHost = maxRequestsPerHost; }

    /** @return whether to use streaming by default */
    public boolean isStreamByDefault() { return streamByDefault; }
    public void setStreamByDefault(boolean streamByDefault) { this.streamByDefault = streamByDefault; }

    /** @return custom HTTP headers */
    public Map<String, String> getCustomHeaders() { return customHeaders; }

    /** Adds a custom HTTP header. */
    public void addCustomHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    /**
     * Creates a new Builder.
     * @return a Builder with default values
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for LLMConfig.
     */
    public static class Builder {
        private final LLMConfig config = new LLMConfig();

        /** Sets the provider type. */
        public Builder provider(String provider) { config.provider = provider; return this; }

        /** Sets the model name. */
        public Builder model(String model) { config.model = model; return this; }

        /** Sets the API key. */
        public Builder apiKey(String apiKey) { config.apiKey = apiKey; return this; }

        /** Sets the base URL. */
        public Builder baseUrl(String baseUrl) { config.baseUrl = baseUrl; return this; }

        /** Sets the request timeout. */
        public Builder timeout(Duration timeout) { config.timeout = timeout; return this; }

        /** Sets max retries. */
        public Builder maxRetries(int maxRetries) { config.maxRetries = maxRetries; return this; }

        /** Sets max connections. */
        public Builder maxConnections(int maxConnections) { config.maxConnections = maxConnections; return this; }

        /** Sets streaming default. */
        public Builder streamByDefault(boolean stream) { config.streamByDefault = stream; return this; }

        /** Adds a custom header. */
        public Builder customHeader(String name, String value) {
            config.customHeaders.put(name, value); return this;
        }

        public LLMConfig build() { return config; }
    }
}
