package com.nousresearch.aikit.llm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;
import okhttp3.Request;

import java.time.Duration;

/**
 * DeepSeek API client implementation.
 *
 * <p>DeepSeek provides an OpenAI-compatible API, so this client extends
 * the same JSON serialization patterns. Supports DeepSeek-V3, DeepSeek-R1,
 * and DeepSeek-Coder models.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * DeepSeekClient client = DeepSeekClient.builder()
 *     .apiKey("sk-...")
 *     .model("deepseek-chat")
 *     .build();
 *
 * String reply = client.chat("You are helpful.", "Hello!");
 * }</pre>
 *
 * @see <a href="https://platform.deepseek.com/api-docs">DeepSeek API Docs</a>
 */
public class DeepSeekClient extends AbstractLLMClient {

    private static final String DEFAULT_BASE_URL = "https://api.deepseek.com/v1";

    private DeepSeekClient(Builder builder) {
        super(builder);
    }

    @Override
    protected String getChatEndpoint() {
        return baseUrl + "/chat/completions";
    }

    @Override
    protected void addAuthHeaders(Request.Builder requestBuilder) {
        requestBuilder.addHeader("Authorization", "Bearer " + apiKey);
    }

    @Override
    protected String serializeRequest(ChatRequest request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }

    @Override
    protected ChatResponse deserializeResponse(String responseBody) throws JsonProcessingException {
        return objectMapper.readValue(responseBody, ChatResponse.class);
    }

    @Override
    protected ChatResponse parseStreamChunk(String data) throws JsonProcessingException {
        return objectMapper.readValue(data, ChatResponse.class);
    }

    @Override
    public String getProviderType() { return "deepseek"; }

    /**
     * Creates a new builder for DeepSeekClient.
     * @return a new Builder
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for DeepSeekClient.
     */
    public static class Builder extends AbstractLLMClient.Builder<Builder> {
        @Override
        public DeepSeekClient build() {
            if (this.baseUrl == null) {
                this.baseUrl = DEFAULT_BASE_URL;
            }
            if (this.defaultModel == null) {
                this.defaultModel = "deepseek-chat";
            }
            return new DeepSeekClient(this);
        }
    }
}
