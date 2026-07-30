package com.nousresearch.aikit.llm.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.model.ChatMessage;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;
import okhttp3.Request;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Google Gemini API client implementation.
 *
 * <p>Supports Gemini 2.0 Flash, Gemini 1.5 Pro/Flash, and other models
 * via Google's Generative Language API. Translates between AiKit's
 * OpenAI-compatible format and Gemini's native request/response schema.</p>
 *
 * <p>Key differences from OpenAI:</p>
 * <ul>
 *   <li>Uses query-parameter API key ({@code ?key=...})</li>
 *   <li>Uses {@code contents} with {@code parts} instead of {@code messages}</li>
 *   <li>System instructions are a separate field</li>
 *   <li>Uses {@code generationConfig} for parameters</li>
 * </ul>
 *
 * @see <a href="https://ai.google.dev/gemini-api/docs">Gemini API Docs</a>
 */
public class GeminiClient extends AbstractLLMClient {

    private static final String DEFAULT_BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    private GeminiClient(Builder builder) {
        super(builder);
    }

    @Override
    protected String getChatEndpoint() {
        // Gemini models endpoint includes the model name in the URL path
        return baseUrl + "/models/" + defaultModel + ":generateContent?key=" + apiKey;
    }

    @Override
    protected void addAuthHeaders(Request.Builder requestBuilder) {
        // Gemini uses query param for auth, no auth header needed
    }

    @Override
    protected Request buildHttpRequest(String url, String jsonBody) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(okhttp3.RequestBody.create(jsonBody, JSON))
                .addHeader("Content-Type", "application/json");
        // No auth header for Gemini
        return requestBuilder.build();
    }

    @Override
    protected String serializeRequest(ChatRequest request) throws JsonProcessingException {
        GeminiRequest gr = convertToGeminiRequest(request);
        return objectMapper.writeValueAsString(gr);
    }

    @Override
    protected ChatResponse deserializeResponse(String responseBody) throws JsonProcessingException {
        GeminiResponse gr = objectMapper.readValue(responseBody, GeminiResponse.class);
        return convertFromGeminiResponse(gr);
    }

    @Override
    protected ChatResponse parseStreamChunk(String data) throws JsonProcessingException {
        // Gemini streaming: each chunk is a full GeminiResponse-like JSON
        // Strip the leading '[' and trailing ']' that wrap SSE chunks
        String cleaned = data.replaceAll("^\[", "").replaceAll("\]$", "");
        if (cleaned.isEmpty() || cleaned.equals("]") || cleaned.equals("[")) {
            return null;
        }
        // Handle comma-separated array elements
        if (cleaned.startsWith(",")) {
            cleaned = cleaned.substring(1);
        }
        try {
            GeminiResponse gr = objectMapper.readValue(cleaned, GeminiResponse.class);
            return convertFromGeminiResponse(gr);
        } catch (JsonProcessingException e) {
            return null; // Skip malformed chunks
        }
    }

    /**
     * Converts an AiKit ChatRequest to a Gemini request.
     */
    private GeminiRequest convertToGeminiRequest(ChatRequest request) {
        GeminiRequest gr = new GeminiRequest();
        gr.generationConfig = new GeminiGenerationConfig();
        gr.generationConfig.temperature = request.getTemperature();
        gr.generationConfig.maxOutputTokens = request.getMaxTokens();
        gr.generationConfig.topP = request.getTopP();

        gr.contents = new ArrayList<>();
        String systemInstruction = null;

        for (ChatMessage msg : request.getMessages()) {
            if (ChatMessage.ROLE_SYSTEM.equals(msg.getRole())) {
                // Accumulate system instructions
                systemInstruction = systemInstruction == null
                        ? msg.getContent()
                        : systemInstruction + "\n" + msg.getContent();
            } else {
                GeminiContent gc = new GeminiContent();
                gc.role = convertRole(msg.getRole());
                GeminiPart part = new GeminiPart();
                part.text = msg.getContent();
                gc.parts = Collections.singletonList(part);
                gr.contents.add(gc);
            }
        }

        if (systemInstruction != null) {
            gr.systemInstruction = new GeminiSystemInstruction();
            GeminiPart sysPart = new GeminiPart();
            sysPart.text = systemInstruction;
            gr.systemInstruction.parts = Collections.singletonList(sysPart);
        }

        return gr;
    }

    /**
     * Converts a Gemini response to an AiKit ChatResponse.
     */
    private ChatResponse convertFromGeminiResponse(GeminiResponse gr) {
        String contentText = "";
        AtomicInteger promptTokens = new AtomicInteger(0);
        AtomicInteger completionTokens = new AtomicInteger(0);

        if (gr.candidates != null) {
            for (GeminiCandidate candidate : gr.candidates) {
                if (candidate.content != null && candidate.content.parts != null) {
                    for (GeminiPart part : candidate.content.parts) {
                        if (part.text != null) {
                            contentText += part.text;
                        }
                    }
                }
            }
        }

        if (gr.usageMetadata != null) {
            promptTokens.set(gr.usageMetadata.promptTokenCount);
            completionTokens.set(gr.usageMetadata.candidatesTokenCount);
        }

        ChatMessage message = ChatMessage.assistant(contentText);

        return ChatResponse.builder()
                .model(defaultModel)
                .choices(Collections.singletonList(new ChatResponse.ChatChoice(
                        0, message, null, "STOP")))
                .usage(new ChatResponse.Usage(
                        promptTokens.get(), completionTokens.get(),
                        promptTokens.get() + completionTokens.get()))
                .build();
    }

    /**
     * Converts AiKit role to Gemini role ("user" or "model").
     */
    private String convertRole(String aikitRole) {
        switch (aikitRole) {
            case ChatMessage.ROLE_ASSISTANT: return "model";
            case ChatMessage.ROLE_USER: return "user";
            default: return "user";
        }
    }

    @Override
    public String getProviderType() { return "gemini"; }

    /** Creates a new Builder for GeminiClient. */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for GeminiClient.
     */
    public static class Builder extends AbstractLLMClient.Builder<Builder> {
        @Override
        public GeminiClient build() {
            if (this.baseUrl == null) {
                this.baseUrl = DEFAULT_BASE_URL;
            }
            if (this.defaultModel == null) {
                this.defaultModel = "gemini-2.0-flash";
            }
            return new GeminiClient(this);
        }
    }

    // --- Gemini-specific DTOs ---

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiRequest {
        @JsonProperty("contents") List<GeminiContent> contents;
        @JsonProperty("systemInstruction") GeminiSystemInstruction systemInstruction;
        @JsonProperty("generationConfig") GeminiGenerationConfig generationConfig;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiContent {
        @JsonProperty("role") String role;
        @JsonProperty("parts") List<GeminiPart> parts;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiPart {
        @JsonProperty("text") String text;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiSystemInstruction {
        @JsonProperty("parts") List<GeminiPart> parts;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiGenerationConfig {
        @JsonProperty("temperature") double temperature = 0.7;
        @JsonProperty("maxOutputTokens") int maxOutputTokens = 2048;
        @JsonProperty("topP") double topP = 1.0;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiResponse {
        @JsonProperty("candidates") List<GeminiCandidate> candidates;
        @JsonProperty("usageMetadata") GeminiUsageMetadata usageMetadata;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiCandidate {
        @JsonProperty("content") GeminiContent content;
        @JsonProperty("finishReason") String finishReason;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class GeminiUsageMetadata {
        @JsonProperty("promptTokenCount") int promptTokenCount;
        @JsonProperty("candidatesTokenCount") int candidatesTokenCount;
        @JsonProperty("totalTokenCount") int totalTokenCount;
    }
}
