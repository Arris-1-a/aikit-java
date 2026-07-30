package com.nousresearch.aikit.llm.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.model.ChatMessage;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;
import com.nousresearch.aikit.core.model.ToolCall;
import okhttp3.Request;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Anthropic Claude API client implementation.
 *
 * <p>Supports Claude 3 Opus, Sonnet, Haiku, and Claude 3.5 models via the
 * Anthropic Messages API. Translates between AiKit's OpenAI-compatible
 * model and Anthropic's native request/response format.</p>
 *
 * <p>Key differences from OpenAI:</p>
 * <ul>
 *   <li>Uses {@code x-api-key} header instead of Bearer token</li>
 *   <li>Anthropic version header required ({@code anthropic-version})</li>
 *   <li>System prompt is a top-level field, not a message</li>
 *   <li>Messages array has alternating user/assistant roles</li>
 * </ul>
 *
 * @see <a href="https://docs.anthropic.com/en/api/messages">Anthropic Messages API</a>
 */
public class AnthropicClient extends AbstractLLMClient {

    private static final String DEFAULT_BASE_URL = "https://api.anthropic.com/v1";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private AnthropicClient(Builder builder) {
        super(builder);
    }

    @Override
    protected String getChatEndpoint() {
        return baseUrl + "/messages";
    }

    @Override
    protected void addAuthHeaders(Request.Builder requestBuilder) {
        requestBuilder.addHeader("x-api-key", apiKey);
        requestBuilder.addHeader("anthropic-version", ANTHROPIC_VERSION);
    }

    @Override
    protected String serializeRequest(ChatRequest request) throws JsonProcessingException {
        AnthropicRequest ar = convertToAnthropicRequest(request);
        return objectMapper.writeValueAsString(ar);
    }

    @Override
    protected ChatResponse deserializeResponse(String responseBody) throws JsonProcessingException {
        AnthropicResponse ar = objectMapper.readValue(responseBody, AnthropicResponse.class);
        return convertFromAnthropicResponse(ar);
    }

    @Override
    protected ChatResponse parseStreamChunk(String data) throws JsonProcessingException {
        // Anthropic SSE format: event type + JSON data
        // Simplified: treat data as AnthropicResponse delta
        AnthropicStreamEvent event = objectMapper.readValue(data, AnthropicStreamEvent.class);
        if (event == null || event.delta == null) {
            return null;
        }
        String deltaText = event.delta.text != null ? event.delta.text : "";
        return ChatResponse.builder()
                .choices(Collections.singletonList(new ChatResponse.ChatChoice(
                        0, null,
                        ChatMessage.assistant(deltaText), null)))
                .build();
    }

    /**
     * Converts an AiKit ChatRequest to Anthropic's native format.
     */
    private AnthropicRequest convertToAnthropicRequest(ChatRequest request) {
        AnthropicRequest ar = new AnthropicRequest();
        ar.model = request.getModel();
        ar.maxTokens = request.getMaxTokens() > 0 ? request.getMaxTokens() : 1024;
        ar.temperature = request.getTemperature();
        ar.topP = request.getTopP();

        // Extract system message
        List<ChatMessage> nonSystemMessages = new ArrayList<>();
        for (ChatMessage msg : request.getMessages()) {
            if (ChatMessage.ROLE_SYSTEM.equals(msg.getRole())) {
                if (ar.system == null) {
                    ar.system = msg.getContent();
                } else {
                    ar.system += "\n" + msg.getContent();
                }
            } else {
                nonSystemMessages.add(msg);
            }
        }

        // Convert messages to Anthropic format
        ar.messages = new ArrayList<>();
        for (ChatMessage msg : nonSystemMessages) {
            AnthropicMessage am = new AnthropicMessage();
            am.role = convertRole(msg.getRole());

            // Handle tool calls in assistant messages
            if (!msg.getToolCalls().isEmpty()) {
                List<AnthropicContent> contents = new ArrayList<>();
                if (msg.getContent() != null && !msg.getContent().isEmpty()) {
                    AnthropicContent textContent = new AnthropicContent();
                    textContent.type = "text";
                    textContent.text = msg.getContent();
                    contents.add(textContent);
                }
                for (ToolCall tc : msg.getToolCalls()) {
                    AnthropicContent toolContent = new AnthropicContent();
                    toolContent.type = "tool_use";
                    toolContent.id = tc.getId();
                    toolContent.name = tc.getFunction().getName();
                    try {
                        toolContent.input = objectMapper.readValue(
                                tc.getFunction().getArguments(), Map.class);
                    } catch (JsonProcessingException e) {
                        toolContent.input = Collections.singletonMap("raw",
                                tc.getFunction().getArguments());
                    }
                    contents.add(toolContent);
                }
                am.content = contents;
            } else if (msg.getToolCallId() != null) {
                // Tool response
                am.role = "user";
                AnthropicContent toolResult = new AnthropicContent();
                toolResult.type = "tool_result";
                toolResult.toolUseId = msg.getToolCallId();
                toolResult.content = msg.getContent();
                am.content = Collections.singletonList(toolResult);
            } else {
                // Simple text message
                am.content = msg.getContent();
            }

            ar.messages.add(am);
        }

        // Convert tool definitions
        if (request.getTools() != null && !request.getTools().isEmpty()) {
            ar.tools = request.getTools().stream().map(td -> {
                Map<String, Object> tool = new HashMap<>();
                tool.put("name", td.getFunction().getName());
                tool.put("description", td.getFunction().getDescription());
                tool.put("input_schema", td.getFunction().getParameters());
                return tool;
            }).collect(Collectors.toList());
        }

        return ar;
    }

    /**
     * Converts an Anthropic response back to AiKit format.
     */
    private ChatResponse convertFromAnthropicResponse(AnthropicResponse ar) {
        StringBuilder contentText = new StringBuilder();
        List<ToolCall> toolCalls = new ArrayList<>();

        if (ar.content != null) {
            for (AnthropicContentBlock block : ar.content) {
                if ("text".equals(block.type) && block.text != null) {
                    contentText.append(block.text);
                } else if ("tool_use".equals(block.type)) {
                    try {
                        String argsJson = block.input != null
                                ? objectMapper.writeValueAsString(block.input) : "{}";
                        ToolCall.FunctionCall fc = new ToolCall.FunctionCall(block.name, argsJson);
                        toolCalls.add(ToolCall.builder()
                                .id(block.id).function(fc).build());
                    } catch (JsonProcessingException e) {
                        throw new AiKitException("Failed to serialize tool input", e);
                    }
                }
            }
        }

        ChatMessage assistantMsg = ChatMessage.builder()
                .role(ChatMessage.ROLE_ASSISTANT)
                .content(contentText.length() == 0 ? null : contentText.toString())
                .toolCalls(toolCalls.isEmpty() ? null : toolCalls)
                .build();

        return ChatResponse.builder()
                .id(ar.id)
                .model(ar.model)
                .choices(Collections.singletonList(new ChatResponse.ChatChoice(
                        0, assistantMsg, null,
                        "end_turn".equals(ar.stopReason) ? "stop" : ar.stopReason)))
                .usage(ar.usage != null ? new ChatResponse.Usage(
                        ar.usage.inputTokens, ar.usage.outputTokens,
                        ar.usage.inputTokens + ar.usage.outputTokens) : null)
                .build();
    }

    /**
     * Converts AiKit role to Anthropic role.
     */
    private String convertRole(String aikitRole) {
        switch (aikitRole) {
            case ChatMessage.ROLE_ASSISTANT: return "assistant";
            case ChatMessage.ROLE_USER: return "user";
            case ChatMessage.ROLE_TOOL: return "user";
            default: return "user";
        }
    }

    @Override
    public String getProviderType() { return "anthropic"; }

    /** Creates a new Builder for AnthropicClient. */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for AnthropicClient.
     */
    public static class Builder extends AbstractLLMClient.Builder<Builder> {
        @Override
        public AnthropicClient build() {
            if (this.baseUrl == null) {
                this.baseUrl = DEFAULT_BASE_URL;
            }
            if (this.defaultModel == null) {
                this.defaultModel = "claude-3-5-sonnet-20241022";
            }
            return new AnthropicClient(this);
        }
    }

    // --- Anthropic-specific DTOs ---

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicRequest {
        @JsonProperty("model") String model;
        @JsonProperty("max_tokens") int maxTokens;
        @JsonProperty("temperature") double temperature;
        @JsonProperty("top_p") double topP;
        @JsonProperty("system") String system;
        @JsonProperty("messages") List<AnthropicMessage> messages;
        @JsonProperty("tools") List<Map<String, Object>> tools;
        @JsonProperty("stream") boolean stream;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicMessage {
        @JsonProperty("role") String role;
        @JsonProperty("content") Object content;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicContent {
        @JsonProperty("type") String type;
        @JsonProperty("text") String text;
        @JsonProperty("id") String id;
        @JsonProperty("name") String name;
        @JsonProperty("input") Map<String, Object> input;
        @JsonProperty("tool_use_id") String toolUseId;
        @JsonProperty("content") String content;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicResponse {
        @JsonProperty("id") String id;
        @JsonProperty("model") String model;
        @JsonProperty("stop_reason") String stopReason;
        @JsonProperty("content") List<AnthropicContentBlock> content;
        @JsonProperty("usage") AnthropicUsage usage;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicContentBlock {
        @JsonProperty("type") String type;
        @JsonProperty("text") String text;
        @JsonProperty("id") String id;
        @JsonProperty("name") String name;
        @JsonProperty("input") Map<String, Object> input;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicUsage {
        @JsonProperty("input_tokens") int inputTokens;
        @JsonProperty("output_tokens") int outputTokens;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicStreamEvent {
        @JsonProperty("type") String type;
        @JsonProperty("delta") AnthropicDelta delta;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class AnthropicDelta {
        @JsonProperty("type") String type;
        @JsonProperty("text") String text;
    }
}
