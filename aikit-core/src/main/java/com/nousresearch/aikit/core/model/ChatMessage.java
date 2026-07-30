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
 * Represents a single message in a chat conversation, supporting the standard
 * roles used by LLM providers: system, user, assistant, and tool.
 *
 * <p>Each message can optionally carry a name, tool call requests, and
 * a tool call ID for tool response messages.</p>
 *
 * @see ChatRequest
 * @see ToolCall
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ChatMessage.Builder.class)
public class ChatMessage {

    private final String role;
    private final String content;
    private final String name;
    private final List<ToolCall> toolCalls;
    private final String toolCallId;

    private ChatMessage(Builder builder) {
        this.role = Objects.requireNonNull(builder.role, "role must not be null");
        this.content = builder.content;
        this.name = builder.name;
        this.toolCalls = builder.toolCalls != null
                ? Collections.unmodifiableList(new ArrayList<>(builder.toolCalls))
                : Collections.emptyList();
        this.toolCallId = builder.toolCallId;
    }

    /** Role constants for common message types. */
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_TOOL = "tool";

    /**
     * Returns the role of the message sender.
     * @return one of "system", "user", "assistant", or "tool"
     */
    public String getRole() { return role; }

    /**
     * Returns the text content of the message, may be null for tool-call-only messages.
     * @return the message content, or null
     */
    public String getContent() { return content; }

    /**
     * Returns the optional name of the message sender.
     * @return the name, or null
     */
    public String getName() { return name; }

    /**
     * Returns the tool calls requested by the assistant.
     * @return an unmodifiable list of tool calls, never null
     */
    public List<ToolCall> getToolCalls() { return toolCalls; }

    /**
     * Returns the tool call ID for tool response messages.
     * @return the tool call ID, or null
     */
    public String getToolCallId() { return toolCallId; }

    /**
     * Creates a system message.
     * @param content the system prompt content
     * @return a new ChatMessage with role "system"
     */
    public static ChatMessage system(String content) {
        return builder().role(ROLE_SYSTEM).content(content).build();
    }

    /**
     * Creates a user message.
     * @param content the user message content
     * @return a new ChatMessage with role "user"
     */
    public static ChatMessage user(String content) {
        return builder().role(ROLE_USER).content(content).build();
    }

    /**
     * Creates an assistant message.
     * @param content the assistant response content
     * @return a new ChatMessage with role "assistant"
     */
    public static ChatMessage assistant(String content) {
        return builder().role(ROLE_ASSISTANT).content(content).build();
    }

    /**
     * Creates a tool response message.
     * @param toolCallId the ID of the tool call this responds to
     * @param content the tool output content
     * @return a new ChatMessage with role "tool"
     */
    public static ChatMessage tool(String toolCallId, String content) {
        return builder().role(ROLE_TOOL).toolCallId(toolCallId).content(content).build();
    }

    public static Builder builder() { return new Builder(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;
        ChatMessage that = (ChatMessage) o;
        return role.equals(that.role) && Objects.equals(content, that.content)
                && Objects.equals(name, that.name) && Objects.equals(toolCalls, that.toolCalls)
                && Objects.equals(toolCallId, that.toolCallId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content, name, toolCalls, toolCallId);
    }

    @Override
    public String toString() {
        return "ChatMessage{role='" + role + "', content='" + (content != null ? content.substring(0, Math.min(content.length(), 100)) : "null") + "', toolCalls=" + toolCalls.size() + "}";
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String role;
        private String content;
        private String name;
        private List<ToolCall> toolCalls;
        private String toolCallId;

        @JsonProperty("role")
        public Builder role(String role) { this.role = role; return this; }

        @JsonProperty("content")
        public Builder content(String content) { this.content = content; return this; }

        @JsonProperty("name")
        public Builder name(String name) { this.name = name; return this; }

        @JsonProperty("tool_calls")
        public Builder toolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; return this; }

        @JsonProperty("tool_call_id")
        public Builder toolCallId(String toolCallId) { this.toolCallId = toolCallId; return this; }

        public ChatMessage build() { return new ChatMessage(this); }
    }
}
