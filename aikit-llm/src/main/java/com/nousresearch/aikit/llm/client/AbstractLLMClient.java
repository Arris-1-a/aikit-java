package com.nousresearch.aikit.llm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nousresearch.aikit.core.LLMProvider;
import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.exception.AuthenticationException;
import com.nousresearch.aikit.core.exception.RateLimitException;
import com.nousresearch.aikit.core.exception.TimeoutException;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;
import com.nousresearch.aikit.llm.http.HttpClientManager;
import com.nousresearch.aikit.llm.retry.RetryPolicy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Abstract base class for LLM provider clients.
 *
 * <p>Handles common concerns: HTTP transport via OkHttp, JSON serialization
 * with Jackson, retry logic via {@link RetryPolicy}, and streaming via SSE.
 * Provider-specific subclasses only need to implement the request/response
 * mapping and authentication header logic.</p>
 *
 * <p>This class is thread-safe. The HTTP client, ObjectMapper, and RetryPolicy
 * are all safe for concurrent use.</p>
 */
public abstract class AbstractLLMClient implements LLMProvider {

    private static final Logger log = LoggerFactory.getLogger(AbstractLLMClient.class);

    /** Standard JSON media type for API requests */
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /** The OkHttpClient for HTTP transport */
    protected final OkHttpClient httpClient;

    /** Jackson ObjectMapper for JSON serialization/deserialization */
    protected final ObjectMapper objectMapper;

    /** Retry policy for transient failures */
    protected final RetryPolicy retryPolicy;

    /** The API base URL for this provider */
    protected final String baseUrl;

    /** The API key for authentication */
    protected final String apiKey;

    /** The default model name */
    protected final String defaultModel;

    /** Request timeout */
    protected final Duration timeout;

    /**
     * Constructs a new AbstractLLMClient.
     *
     * @param builder the builder with configuration
     */
    protected AbstractLLMClient(Builder<?> builder) {
        this.apiKey = builder.apiKey;
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be null or blank");
        }
        this.baseUrl = builder.baseUrl;
        this.defaultModel = builder.defaultModel;
        this.timeout = builder.timeout != null ? builder.timeout : Duration.ofSeconds(60);
        this.httpClient = builder.httpClientManager != null
                ? builder.httpClientManager.getClient()
                : HttpClientManager.getInstance().getClient();
        this.objectMapper = builder.objectMapper != null
                ? builder.objectMapper : createDefaultObjectMapper();
        this.retryPolicy = builder.retryPolicy != null
                ? builder.retryPolicy : RetryPolicy.builder().build();
    }

    /**
     * Creates the default ObjectMapper for JSON handling.
     * Subclasses may override to add provider-specific modules.
     *
     * @return a configured ObjectMapper
     */
    protected ObjectMapper createDefaultObjectMapper() {
        return new ObjectMapper();
    }

    // ---- LLMProvider implementation ----

    @Override
    public ChatResponse chat(ChatRequest request) {
        return retryPolicy.execute(() -> doChat(request));
    }

    @Override
    public CompletableFuture<ChatResponse> chatAsync(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request));
    }

    @Override
    public ChatResponse chatStream(ChatRequest request, Consumer<ChatResponse> onChunk) {
        ChatRequest streamRequest = ChatRequest.builder()
                .model(request.getModel())
                .messages(request.getMessages())
                .temperature(request.getTemperature())
                .maxTokens(request.getMaxTokens())
                .topP(request.getTopP())
                .stream(true)
                .build();

        String jsonBody;
        try {
            jsonBody = serializeRequest(streamRequest);
        } catch (JsonProcessingException e) {
            throw new AiKitException("Failed to serialize streaming request", e);
        }

        String url = getChatEndpoint();
        Request httpRequest = buildHttpRequest(url, jsonBody);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ChatResponse> finalResponseRef = new AtomicReference<>();
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        StringBuilder contentAccumulator = new StringBuilder();

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equals(data.trim())) {
                    return;
                }
                try {
                    ChatResponse chunk = parseStreamChunk(data);
                    if (chunk != null && !chunk.getChoices().isEmpty()) {
                        ChatResponse.ChatChoice choice = chunk.getChoices().get(0);
                        if (choice.getDelta() != null && choice.getDelta().getContent() != null) {
                            contentAccumulator.append(choice.getDelta().getContent());
                        }
                        onChunk.accept(chunk);
                    }
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse stream chunk: {}", e.getMessage());
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                // Build final response similar to non-streaming format
                ChatResponse finalResp = ChatResponse.builder()
                        .model(streamRequest.getModel())
                        .choices(java.util.List.of(new ChatResponse.ChatChoice(
                                0,
                                com.nousresearch.aikit.core.model.ChatMessage.assistant(
                                        contentAccumulator.toString()),
                                null, "stop")))
                        .build();
                finalResponseRef.set(finalResp);
                latch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, okhttp3.Response response) {
                errorRef.set(t);
                latch.countDown();
            }
        };

        EventSource.Factory factory = EventSources.createFactory(httpClient);
        EventSource eventSource = factory.newEventSource(httpRequest, listener);

        try {
            boolean completed = latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!completed) {
                eventSource.cancel();
                throw new TimeoutException("SSE stream timed out after " + timeout.toMillis() + "ms", timeout);
            }
            if (errorRef.get() != null) {
                throw new AiKitException("SSE stream error", errorRef.get());
            }
            return finalResponseRef.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            eventSource.cancel();
            throw new AiKitException("SSE stream interrupted", e);
        } finally {
            // Ensure EventSource is cancelled to prevent resource leaks
            eventSource.cancel();
        }
    }

    // ---- Template methods for subclasses ----

    /**
     * Returns the chat completions API endpoint URL.
     * @return the full URL including base path
     */
    protected abstract String getChatEndpoint();

    /**
     * Builds the provider-specific HTTP request headers.
     * Subclasses must set the Authorization header and any
     * provider-specific headers.
     *
     * @param requestBuilder the OkHttp request builder
     */
    protected abstract void addAuthHeaders(Request.Builder requestBuilder);

    /**
     * Serializes a ChatRequest to the provider-specific JSON format.
     * Most providers follow OpenAI-compatible format, but Anthropic differs.
     *
     * @param request the chat request
     * @return JSON string
     * @throws JsonProcessingException if serialization fails
     */
    protected abstract String serializeRequest(ChatRequest request) throws JsonProcessingException;

    /**
     * Deserializes the HTTP response body into a ChatResponse.
     *
     * @param responseBody the raw JSON response body
     * @return the parsed ChatResponse
     * @throws JsonProcessingException if deserialization fails
     */
    protected abstract ChatResponse deserializeResponse(String responseBody) throws JsonProcessingException;

    /**
     * Parses a single SSE event data chunk into a ChatResponse.
     *
     * @param data the SSE event data (JSON string)
     * @return the parsed chunk as ChatResponse
     * @throws JsonProcessingException if parsing fails
     */
    protected abstract ChatResponse parseStreamChunk(String data) throws JsonProcessingException;

    // ---- Internal helpers ----

    /**
     * Executes a single chat request without retry logic.
     */
    private ChatResponse doChat(ChatRequest request) {
        String jsonBody;
        try {
            jsonBody = serializeRequest(request);
        } catch (JsonProcessingException e) {
            throw new AiKitException("Failed to serialize chat request", e);
        }

        String url = getChatEndpoint();
        Request httpRequest = buildHttpRequest(url, jsonBody);

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            return handleResponse(response, responseBody);
        } catch (IOException e) {
            throw new AiKitException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Builds an HTTP POST request with JSON body and auth headers.
     */
    protected Request buildHttpRequest(String url, String jsonBody) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonBody, JSON))
                .addHeader("Content-Type", "application/json");

        addAuthHeaders(requestBuilder);

        return requestBuilder.build();
    }

    /**
     * Handles the HTTP response, converting errors to appropriate exceptions.
     */
    protected ChatResponse handleResponse(Response response, String responseBody) throws JsonProcessingException {
        int statusCode = response.code();

        if (statusCode == 200) {
            return deserializeResponse(responseBody);
        }

        if (statusCode == 401 || statusCode == 403) {
            throw new AuthenticationException(
                    "API authentication failed (HTTP " + statusCode + "): " + responseBody);
        }

        if (statusCode == 429) {
            String retryAfter = response.header("Retry-After");
            Duration waitDuration;
            if (retryAfter != null) {
                try {
                    // Retry-After can be seconds (integer) or HTTP-date
                    waitDuration = Duration.ofSeconds(Long.parseLong(retryAfter));
                } catch (NumberFormatException e) {
                    waitDuration = Duration.ofSeconds(5);
                }
            } else {
                waitDuration = Duration.ofSeconds(5);
            }
            throw new RateLimitException(
                    "Rate limit exceeded (HTTP 429): " + responseBody, waitDuration);
        }

        if (statusCode >= 500) {
            throw new AiKitException(
                    "Server error (HTTP " + statusCode + "): " + responseBody, statusCode);
        }

        throw new AiKitException(
                "Unexpected response (HTTP " + statusCode + "): " + responseBody, statusCode);
    }

    @Override
    public String getDefaultModel() { return defaultModel; }

    @Override
    public void close() {
        // OkHttpClient lifecycle is managed by HttpClientManager
        log.debug("LLM client closed for provider: {}", getProviderType());
    }

    /**
     * Abstract builder base for LLM client builders.
     *
     * @param <T> the concrete builder type
     */
    public abstract static class Builder<T extends Builder<T>> {
        String apiKey;
        String baseUrl;
        String defaultModel;
        Duration timeout;
        HttpClientManager httpClientManager;
        ObjectMapper objectMapper;
        RetryPolicy retryPolicy;

        /** Sets the API key for authentication. */
        @SuppressWarnings("unchecked")
        public T apiKey(String apiKey) {
            this.apiKey = apiKey;
            return (T) this;
        }

        /** Sets the base URL for the API endpoint. */
        @SuppressWarnings("unchecked")
        public T baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return (T) this;
        }

        /** Sets the default model name. */
        @SuppressWarnings("unchecked")
        public T defaultModel(String model) {
            this.defaultModel = model;
            return (T) this;
        }

        /** Sets the request timeout. */
        @SuppressWarnings("unchecked")
        public T timeout(Duration timeout) {
            this.timeout = timeout;
            return (T) this;
        }

        /** Sets a custom HTTP client manager. */
        @SuppressWarnings("unchecked")
        public T httpClientManager(HttpClientManager manager) {
            this.httpClientManager = manager;
            return (T) this;
        }

        /** Sets a custom ObjectMapper. */
        @SuppressWarnings("unchecked")
        public T objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return (T) this;
        }

        /** Sets a custom retry policy. */
        @SuppressWarnings("unchecked")
        public T retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return (T) this;
        }

        /** Builds the concrete LLM client. */
        public abstract AbstractLLMClient build();
    }
}
