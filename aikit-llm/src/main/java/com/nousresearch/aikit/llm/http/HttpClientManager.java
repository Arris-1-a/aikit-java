package com.nousresearch.aikit.llm.http;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Manages the OkHttpClient lifecycle and connection pooling for LLM API calls.
 *
 * <p>Provides a singleton-like factory for creating and configuring
 * OkHttp clients with appropriate timeouts, connection pooling, and
 * logging settings.</p>
 *
 * <p>This class is thread-safe and designed for use across multiple
 * provider client instances.</p>
 */
public final class HttpClientManager {

    private static final Logger log = LoggerFactory.getLogger(HttpClientManager.class);

    private static volatile HttpClientManager instance;
    private volatile OkHttpClient sharedClient;

    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final Duration writeTimeout;
    private final Duration callTimeout;
    private final int maxIdleConnections;
    private final Duration keepAliveDuration;
    private final int maxRequests;
    private final int maxRequestsPerHost;
    private final boolean enableLogging;

    private HttpClientManager(Builder builder) {
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.callTimeout = builder.callTimeout;
        this.maxIdleConnections = builder.maxIdleConnections;
        this.keepAliveDuration = builder.keepAliveDuration;
        this.maxRequests = builder.maxRequests;
        this.maxRequestsPerHost = builder.maxRequestsPerHost;
        this.enableLogging = builder.enableLogging;
    }

    /**
     * Returns the singleton instance with default configuration.
     * @return the default HttpClientManager
     */
    public static HttpClientManager getInstance() {
        if (instance == null) {
            synchronized (HttpClientManager.class) {
                if (instance == null) {
                    instance = new Builder().build();
                }
            }
        }
        return instance;
    }

    /**
     * Creates or returns the shared OkHttpClient instance.
     * @return the configured OkHttpClient
     */
    public OkHttpClient getClient() {
        if (sharedClient == null) {
            synchronized (this) {
                if (sharedClient == null) {
                    sharedClient = buildClient();
                    log.info("OkHttpClient initialized: connectTimeout={}ms, readTimeout={}ms, "
                            + "maxRequests={}, maxRequestsPerHost={}",
                            connectTimeout.toMillis(), readTimeout.toMillis(),
                            maxRequests, maxRequestsPerHost);
                }
            }
        }
        return sharedClient;
    }

    /**
     * Creates a new OkHttpClient with the configured settings.
     * Useful when different providers need slightly different settings.
     * @return a new OkHttpClient
     */
    public OkHttpClient buildClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .callTimeout(callTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(
                        maxIdleConnections,
                        keepAliveDuration.toMillis(),
                        TimeUnit.MILLISECONDS))
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true);

        // Configure dispatcher for concurrency control
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxRequests);
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
        clientBuilder.dispatcher(dispatcher);

        // Add logging interceptor if enabled
        if (enableLogging) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                    msg -> log.debug("HTTP: {}", msg));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            clientBuilder.addNetworkInterceptor(loggingInterceptor);
        }

        return clientBuilder.build();
    }

    /**
     * Creates a new builder for customizing HttpClientManager.
     * @return a new Builder
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for HttpClientManager.
     */
    public static class Builder {
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration readTimeout = Duration.ofSeconds(60);
        private Duration writeTimeout = Duration.ofSeconds(30);
        private Duration callTimeout = Duration.ofSeconds(120);
        private int maxIdleConnections = 5;
        private Duration keepAliveDuration = Duration.ofMinutes(5);
        private int maxRequests = 64;
        private int maxRequestsPerHost = 10;
        private boolean enableLogging = false;

        /**
         * Sets the connection timeout.
         */
        public Builder connectTimeout(Duration timeout) {
            this.connectTimeout = timeout; return this;
        }

        /**
         * Sets the read timeout.
         */
        public Builder readTimeout(Duration timeout) {
            this.readTimeout = timeout; return this;
        }

        /**
         * Sets the write timeout.
         */
        public Builder writeTimeout(Duration timeout) {
            this.writeTimeout = timeout; return this;
        }

        /**
         * Sets the overall call timeout.
         */
        public Builder callTimeout(Duration timeout) {
            this.callTimeout = timeout; return this;
        }

        /**
         * Sets the maximum idle connections in the pool.
         */
        public Builder maxIdleConnections(int maxIdleConnections) {
            this.maxIdleConnections = maxIdleConnections; return this;
        }

        /**
         * Sets the connection keep-alive duration.
         */
        public Builder keepAliveDuration(Duration duration) {
            this.keepAliveDuration = duration; return this;
        }

        /**
         * Sets the maximum concurrent requests.
         */
        public Builder maxRequests(int maxRequests) {
            this.maxRequests = maxRequests; return this;
        }

        /**
         * Sets the maximum concurrent requests per host.
         */
        public Builder maxRequestsPerHost(int maxRequestsPerHost) {
            this.maxRequestsPerHost = maxRequestsPerHost; return this;
        }

        /**
         * Enables or disables HTTP request/response logging.
         */
        public Builder enableLogging(boolean enable) {
            this.enableLogging = enable; return this;
        }

        public HttpClientManager build() { return new HttpClientManager(this); }
    }
}
