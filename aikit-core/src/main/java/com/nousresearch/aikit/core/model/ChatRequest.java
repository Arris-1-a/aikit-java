package com.nousresearch.aikit.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a chat completion request sent to an LLM provider.
 *
 * <p>Encapsulates all parameters needed for a chat completion call:
 * model selection, message history, temperature, token limits,
 * streaming mode, and tool definitions.</p>
 *
 * @see ChatResponse
 * @see ChatMessage
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ChatRequest.Builder.class)
public class ChatRequest {

    private final String model;
    private final List<ChatMessage> messages;
    private final double temperature;
    private final int maxTokens;
    private final double topP;
    private final int n;
    private final boolean stream;
    private final List<String> stop;
    private final double presencePenalty;
    private final double frequencyPenalty;
    private final Map<String, Object> logitBias;
    private final String user;
    private final List<ToolDefinition> tools;
    private final String toolChoice;

    private ChatRequest(Builder builder) {
        this.model = Objects.requireNonNull(builder.model, "model must not be null");
        this.messages = Collections.unmodifiableList(
                new ArrayList<>(Objects.requireNonNull(builder.messages, "messages must not be null")));
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.topP = builder.topP;
        this.n = builder.n;
        this.stream = builder.stream;
        this.stop = builder.stop != null ? Collections.unmodifiableList(new ArrayList<>(builder.stop)) : null;
        this.presencePenalty = builder.presencePenalty;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.logitBias = builder.logitBias;
        this.user = builder.user;
        this.tools = builder.tools != null ? Collections.unmodifiableList(new ArrayList<>(builder.tools)) : null;
        this.toolChoice = builder.toolChoice;
    }

    /** @return the model name (e.g., "gpt-4", "claude-3-opus-20240229") */
    public String getModel() { return model; }

    /** @return the conversation messages */
    public List<ChatMessage> getMessages() { return messages; }

    /** @return sampling temperature (0.0–2.0) */
    public double getTemperature() { return temperature; }

    /** @return maximum tokens in the response */
    public int getMaxTokens() { return maxTokens; }

    /** @return nucleus sampling parameter */
    public double getTopP() { return topP; }

    /** @return number of completions to generate */
    public int getN() { return n; }

    /** @return whether streaming (SSE) is enabled */
    public boolean isStream() { return stream; }

    /** @return stop sequences, or null */
    public List<String> getStop() { return stop; }

    /** @return presence penalty */
    public double getPresencePenalty() { return presencePenalty; }

    /** @return frequency penalty */
    public double getFrequencyPenalty() { return frequencyPenalty; }

    /** @return logit bias map, or null */
    public Map<String, Object> getLogitBias() { return logitBias; }

    /** @return end-user identifier for abuse monitoring */
    public String getUser() { return user; }

    /** @return tool definitions available to the model, or null */
    public List<ToolDefinition> getTools() { return tools; }

    /** @return tool choice mode ("auto", "none", or specific tool) */
    public String getToolChoice() { return toolChoice; }

    public static Builder builder() { return new Builder(); }

    @Override
    public String toString() {
        return "ChatRequest{model='" + model + "', messages=" + messages.size()
                + ", temperature=" + temperature + ", maxTokens=" + maxTokens + ", stream=" + stream + "}";
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String model;
        private List<ChatMessage> messages;
        private double temperature = 0.7;
        private int maxTokens = 2048;
        private double topP = 1.0;
        private int n = 1;
        private boolean stream = false;
        private List<String> stop;
        private double presencePenalty = 0.0;
        private double frequencyPenalty = 0.0;
        private Map<String, Object> logitBias;
        private String user;
        private List<ToolDefinition> tools;
        private String toolChoice;

        @JsonProperty("model")
        public Builder model(String model) { this.model = model; return this; }

        @JsonProperty("messages")
        public Builder messages(List<ChatMessage> messages) { this.messages = messages; return this; }

        @JsonProperty("temperature")
        public Builder temperature(double temperature) { this.temperature = temperature; return this; }

        @JsonProperty("max_tokens")
        public Builder maxTokens(int maxTokens) { this.maxTokens = maxTokens; return this; }

        @JsonProperty("top_p")
        public Builder topP(double topP) { this.topP = topP; return this; }

        @JsonProperty("n")
        public Builder n(int n) { this.n = n; return this; }

        @JsonProperty("stream")
        public Builder stream(boolean stream) { this.stream = stream; return this; }

        @JsonProperty("stop")
        public Builder stop(List<String> stop) { this.stop = stop; return this; }

        @JsonProperty("presence_penalty")
        public Builder presencePenalty(double presencePenalty) { this.presencePenalty = presencePenalty; return this; }

        @JsonProperty("frequency_penalty")
        public Builder frequencyPenalty(double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; return this; }

        @JsonProperty("logit_bias")
        public Builder logitBias(Map<String, Object> logitBias) { this.logitBias = logitBias; return this; }

        @JsonProperty("user")
        public Builder user(String user) { this.user = user; return this; }

        @JsonProperty("tools")
        public Builder tools(List<ToolDefinition> tools) { this.tools = tools; return this; }

        @JsonProperty("tool_choice")
        public Builder toolChoice(String toolChoice) { this.toolChoice = toolChoice; return this; }

        public ChatRequest build() { return new ChatRequest(this); }
    }
}
