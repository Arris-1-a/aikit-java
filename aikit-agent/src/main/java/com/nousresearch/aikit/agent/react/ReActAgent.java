package com.nousresearch.aikit.agent.react;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nousresearch.aikit.agent.tool.Tool;
import com.nousresearch.aikit.agent.tool.ToolRegistry;
import com.nousresearch.aikit.core.LLMProvider;
import com.nousresearch.aikit.core.model.ChatMessage;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;
import com.nousresearch.aikit.core.model.ToolCall;
import com.nousresearch.aikit.core.model.ToolDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements the ReAct (Reasoning + Acting) agent pattern.
 *
 * <p>The ReAct agent interleaves reasoning steps with tool-calling actions,
 * following the Think → Act → Observe loop:</p>
 *
 * <ol>
 *   <li>The LLM receives the user query and available tools</li>
 *   <li>If a tool call is needed, the agent executes it</li>
 *   <li>Tool output is fed back to the LLM as a tool message</li>
 *   <li>Steps 2-3 repeat until the LLM provides a final answer</li>
 * </ol>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * ReActAgent agent = ReActAgent.builder()
 *     .llmProvider(openaiClient)
 *     .toolRegistry(registry)
 *     .maxIterations(10)
 *     .build();
 *
 * AgentResult result = agent.run("What's the weather in Tokyo?");
 * System.out.println(result.getAnswer());
 * }</pre>
 */
public class ReActAgent {

    private static final Logger log = LoggerFactory.getLogger(ReActAgent.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Default system prompt for ReAct agents */
    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are a helpful AI assistant with access to tools. "
            + "Use the available tools to answer the user's question. "
            + "Think step by step: analyze what information you need, "
            + "call tools to get it, then synthesize a final answer. "
            + "Always respond in the user's language.";

    private final LLMProvider llmProvider;
    private final ToolRegistry toolRegistry;
    private final int maxIterations;
    private final String systemPrompt;
    private final String model;
    private final double temperature;

    private final List<AgentStep> steps;

    private ReActAgent(Builder builder) {
        this.llmProvider = builder.llmProvider;
        this.toolRegistry = builder.toolRegistry;
        this.maxIterations = builder.maxIterations;
        this.systemPrompt = builder.systemPrompt != null
                ? builder.systemPrompt : DEFAULT_SYSTEM_PROMPT;
        this.model = builder.model;
        this.temperature = builder.temperature;
        this.steps = new CopyOnWriteArrayList<>();
    }

    /**
     * Runs the ReAct agent with a user query.
     *
     * @param userQuery the user's question or instruction
     * @return the agent result containing the answer and execution trace
     */
    public AgentResult run(String userQuery) {
        steps.clear();

        // Build initial message list
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.system(systemPrompt));
        messages.add(ChatMessage.user(userQuery));

        // Get tool definitions
        List<ToolDefinition> toolDefs = toolRegistry.getToolDefinitions();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            log.debug("ReAct iteration {}/{}", iteration + 1, maxIterations);

            // Build request
            ChatRequest.Builder requestBuilder = ChatRequest.builder()
                    .model(model != null ? model : llmProvider.getDefaultModel())
                    .messages(messages)
                    .temperature(temperature);

            if (!toolDefs.isEmpty()) {
                requestBuilder.tools(toolDefs);
                requestBuilder.toolChoice("auto");
            }

            ChatResponse response = llmProvider.chat(requestBuilder.build());

            if (response.getChoices().isEmpty()) {
                return new AgentResult("No response generated", steps, iteration + 1);
            }

            ChatMessage assistantMsg = response.getChoices().get(0).getMessage();
            if (assistantMsg == null) {
                return new AgentResult("No message in response", steps, iteration + 1);
            }

            messages.add(assistantMsg);

            // Check if the LLM wants to call tools
            if (!assistantMsg.getToolCalls().isEmpty()) {
                for (ToolCall toolCall : assistantMsg.getToolCalls()) {
                    String toolName = toolCall.getFunction().getName();
                    Tool tool = toolRegistry.get(toolName);

                    String toolResult;
                    boolean success = false;

                    if (tool == null) {
                        toolResult = "Error: Unknown tool '" + toolName + "'";
                    } else {
                        try {
                            Map<String, Object> args = parseArguments(
                                    toolCall.getFunction().getArguments());
                            toolResult = tool.execute(args);
                            success = true;
                        } catch (Exception e) {
                            toolResult = "Error executing tool '" + toolName
                                    + "': " + e.getMessage();
                        }
                    }

                    // Record the step
                    steps.add(new AgentStep(
                            AgentStep.StepType.TOOL_CALL,
                            toolName,
                            toolCall.getFunction().getArguments(),
                            toolResult,
                            success));

                    // Add tool response to conversation
                    messages.add(ChatMessage.tool(toolCall.getId(), toolResult));
                }
            } else {
                // No tool calls — this is the final answer
                String answer = assistantMsg.getContent();
                steps.add(new AgentStep(
                        AgentStep.StepType.FINAL_ANSWER,
                        null, null, answer, true));
                return new AgentResult(
                        answer != null ? answer : "No content in response",
                        steps, iteration + 1);
            }
        }

        // Max iterations exceeded — ask LLM for final answer
        messages.add(ChatMessage.user(
                "Please provide your final answer based on the information gathered."));
        ChatResponse finalResponse = llmProvider.chat(ChatRequest.builder()
                .model(model != null ? model : llmProvider.getDefaultModel())
                .messages(messages)
                .temperature(temperature)
                .build());

        String finalAnswer = finalResponse.getFirstContent();
        return new AgentResult(
                finalAnswer != null ? finalAnswer : "Max iterations reached",
                steps, maxIterations);
    }

    /**
     * Parses JSON arguments string into a Map.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseArguments(String argumentsJson) {
        try {
            return OBJECT_MAPPER.readValue(argumentsJson,
                    new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.singletonMap("raw", argumentsJson);
        }
    }

    /** @return the execution steps from the last run */
    public List<AgentStep> getSteps() { return Collections.unmodifiableList(steps); }

    /**
     * Creates a new Builder.
     * @return a Builder
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for ReActAgent.
     */
    public static class Builder {
        private LLMProvider llmProvider;
        private ToolRegistry toolRegistry;
        private int maxIterations = 10;
        private String systemPrompt;
        private String model;
        private double temperature = 0.7;

        /** Sets the LLM provider (required). */
        public Builder llmProvider(LLMProvider llmProvider) {
            this.llmProvider = llmProvider; return this;
        }

        /** Sets the tool registry. */
        public Builder toolRegistry(ToolRegistry toolRegistry) {
            this.toolRegistry = toolRegistry; return this;
        }

        /** Sets the maximum iterations (default 10). */
        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations; return this;
        }

        /** Sets the system prompt. */
        public Builder systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt; return this;
        }

        /** Sets the model name. */
        public Builder model(String model) {
            this.model = model; return this;
        }

        /** Sets the sampling temperature. */
        public Builder temperature(double temperature) {
            this.temperature = temperature; return this;
        }

        /** Builds the ReAct agent. */
        public ReActAgent build() {
            if (llmProvider == null) {
                throw new IllegalArgumentException("LLM provider is required");
            }
            if (toolRegistry == null) {
                toolRegistry = new ToolRegistry();
            }
            return new ReActAgent(this);
        }
    }

    /**
     * Represents a single step in the agent's execution trace.
     */
    public static class AgentStep {
        public enum StepType { TOOL_CALL, FINAL_ANSWER }

        private final StepType type;
        private final String toolName;
        private final String arguments;
        private final String output;
        private final boolean success;

        AgentStep(StepType type, String toolName, String arguments,
                  String output, boolean success) {
            this.type = type;
            this.toolName = toolName;
            this.arguments = arguments;
            this.output = output;
            this.success = success;
        }

        /** @return the step type */
        public StepType getType() { return type; }

        /** @return the tool name (for TOOL_CALL steps) */
        public String getToolName() { return toolName; }

        /** @return the tool arguments JSON */
        public String getArguments() { return arguments; }

        /** @return the step output */
        public String getOutput() { return output; }

        /** @return whether this step succeeded */
        public boolean isSuccess() { return success; }

        @Override
        public String toString() {
            return "AgentStep{" + type + ", tool='" + toolName + "', success=" + success + "}";
        }
    }

    /**
     * Final result of a ReAct agent run.
     */
    public static class AgentResult {
        private final String answer;
        private final List<AgentStep> steps;
        private final int iterations;

        AgentResult(String answer, List<AgentStep> steps, int iterations) {
            this.answer = answer;
            this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
            this.iterations = iterations;
        }

        /** @return the final answer from the agent */
        public String getAnswer() { return answer; }

        /** @return the execution trace */
        public List<AgentStep> getSteps() { return steps; }

        /** @return number of iterations used */
        public int getIterations() { return iterations; }

        /** @return the number of tool calls made */
        public int getToolCallCount() {
            return (int) steps.stream()
                    .filter(s -> s.getType() == AgentStep.StepType.TOOL_CALL)
                    .count();
        }

        @Override
        public String toString() {
            return "AgentResult{answer='" + answer.substring(0, Math.min(answer.length(), 100))
                    + "', steps=" + steps.size() + ", iterations=" + iterations + "}";
        }
    }
}
