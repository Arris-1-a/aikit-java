package com.nousresearch.aikit.llm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;
import okhttp3.Request;

import java.time.Duration;

/**
 * OpenAI API client implementation.
 *
 * <p>Supports all OpenAI chat completion endpoints including GPT-4, GPT-4o,
 * GPT-3.5-turbo, and reasoning models. Implements the standard OpenAI REST
 * API with JSON request/response bodies and SSE streaming.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * OpenAIClient client = OpenAIClient.builder()
 *     .apiKey("sk-...")
 *     .model("gpt-4o")
 *     .build();
 *
 * String reply = client.chat("You are helpful.", "Hello!");
 * }</pre>
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/chat">OpenAI Chat API</a>
 */
public class OpenAIClient extends AbstractLLMClient {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";

    /**
     * Creates a new OpenAI client from a builder.
     */
    private OpenAIClient(Builder builder) {
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
    public String getProviderType() { return "openai"; }

    /**
     * Creates a new builder for OpenAIClient.
     * @return a new Builder
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for OpenAIClient.
     */
    public static class Builder extends AbstractLLMClient.Builder<Builder> {

        /**
         * Sets the OpenAI API key. Shorthand for {@code apiKey()}.
         */
        public Builder openaiApiKey(String apiKey) {
            return this.apiKey(apiKey);
        }

        /**
         * Sets the model name.
         */
        public Builder model(String model) {
            return this.defaultModel(model);
        }

        /**
         * Configures for GPT-4o (gpt-4o).
         */
        public Builder gpt4o() {
            this.defaultModel = "gpt-4o";
            return this;
        }

        /**
         * Configures for GPT-4 Turbo (gpt-4-turbo).
         */
        public Builder gpt4Turbo() {
            this.defaultModel = "gpt-4-turbo";
            return this;
        }

        /**
         * Configures for GPT-3.5 Turbo (gpt-3.5-turbo).
         */
        public Builder gpt35Turbo() {
            this.defaultModel = "gpt-3.5-turbo";
            return this;
        }

        @Override
        public OpenAIClient build() {
            if (this.baseUrl == null) {
                this.baseUrl = DEFAULT_BASE_URL;
            }
            if (this.defaultModel == null) {
                this.defaultModel = "gpt-4o";
            }
            return new OpenAIClient(this);
        }
    }
}
