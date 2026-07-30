package com.nousresearch.aikit.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a chat completion response from an LLM provider.
 *
 * <p>Contains the generated choices, token usage statistics,
 * and provider-specific metadata.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ChatResponse.Builder.class)
public class ChatResponse {

    private final String id;
    private final String object;
    private final long created;
    private final String model;
    private final List<ChatChoice> choices;
    private final Usage usage;
    private final String systemFingerprint;

    private ChatResponse(Builder builder) {
        this.id = builder.id;
        this.object = builder.object;
        this.created = builder.created;
        this.model = builder.model;
        this.choices = builder.choices != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.choices))
                : Collections.emptyList();
        this.usage = builder.usage;
        this.systemFingerprint = builder.systemFingerprint;
    }

    /** @return the unique response identifier */
    public String getId() { return id; }

    /** @return the object type (e.g., "chat.completion") */
    public String getObject() { return object; }

    /** @return Unix timestamp of creation */
    public long getCreated() { return created; }

    /** @return the model used for the completion */
    public String getModel() { return model; }

    /** @return the list of choices (typically one) */
    public List<ChatChoice> getChoices() { return choices; }

    /** @return token usage statistics */
    public Usage getUsage() { return usage; }

    /** @return system fingerprint for reproducibility */
    public String getSystemFingerprint() { return systemFingerprint; }

    /**
     * Convenience method to get the content of the first choice.
     * @return the message content of the first choice, or null
     */
    public String getFirstContent() {
        if (choices.isEmpty() || choices.get(0).getMessage() == null) {
            return null;
        }
        return choices.get(0).getMessage().getContent();
    }

    public static Builder builder() { return new Builder(); }

    @Override
    public String toString() {
        return "ChatResponse{id='" + id + "', model='" + model + "', choices=" + choices.size() + "}";
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String id;
        private String object;
        private long created;
        private String model;
        private List<ChatChoice> choices;
        private Usage usage;
        private String systemFingerprint;

        @JsonProperty("id")
        public Builder id(String id) { this.id = id; return this; }

        @JsonProperty("object")
        public Builder object(String object) { this.object = object; return this; }

        @JsonProperty("created")
        public Builder created(long created) { this.created = created; return this; }

        @JsonProperty("model")
        public Builder model(String model) { this.model = model; return this; }

        @JsonProperty("choices")
        public Builder choices(List<ChatChoice> choices) { this.choices = choices; return this; }

        @JsonProperty("usage")
        public Builder usage(Usage usage) { this.usage = usage; return this; }

        @JsonProperty("system_fingerprint")
        public Builder systemFingerprint(String systemFingerprint) {
            this.systemFingerprint = systemFingerprint; return this;
        }

        public ChatResponse build() { return new ChatResponse(this); }
    }

    /**
     * Represents a single choice within a chat completion response.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChatChoice {
        private final int index;
        private final ChatMessage message;
        private final ChatMessage delta;
        private final String finishReason;

        public ChatChoice(@JsonProperty("index") int index,
                          @JsonProperty("message") ChatMessage message,
                          @JsonProperty("delta") ChatMessage delta,
                          @JsonProperty("finish_reason") String finishReason) {
            this.index = index;
            this.message = message;
            this.delta = delta;
            this.finishReason = finishReason;
        }

        /** @return the index of this choice */
        public int getIndex() { return index; }

        /** @return the full message (for non-streaming responses) */
        public ChatMessage getMessage() { return message; }

        /** @return the delta content (for streaming responses) */
        public ChatMessage getDelta() { return delta; }

        /** @return the reason the model stopped generating */
        public String getFinishReason() { return finishReason; }
    }

    /**
     * Token usage statistics for a chat completion.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Usage {
        private final int promptTokens;
        private final int completionTokens;
        private final int totalTokens;

        public Usage(@JsonProperty("prompt_tokens") int promptTokens,
                     @JsonProperty("completion_tokens") int completionTokens,
                     @JsonProperty("total_tokens") int totalTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = totalTokens;
        }

        /** @return tokens consumed by the prompt */
        public int getPromptTokens() { return promptTokens; }

        /** @return tokens generated in the completion */
        public int getCompletionTokens() { return completionTokens; }

        /** @return total tokens consumed */
        public int getTotalTokens() { return totalTokens; }

        @Override
        public String toString() {
            return "Usage{prompt=" + promptTokens + ", completion=" + completionTokens
                    + ", total=" + totalTokens + "}";
        }
    }
}
