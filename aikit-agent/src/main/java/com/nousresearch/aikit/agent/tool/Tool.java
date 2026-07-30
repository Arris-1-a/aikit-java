package com.nousresearch.aikit.agent.tool;

import com.nousresearch.aikit.core.model.ToolCall;
import com.nousresearch.aikit.core.model.ToolDefinition;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a callable tool/function that an agent can invoke.
 *
 * <p>Tools are registered with the agent framework and made available
 * to LLMs as function definitions. Each tool has a name, description,
 * parameter schema, and an execution method.</p>
 *
 * <p>Usage — implementing a tool:</p>
 * <pre>{@code
 * Tool weatherTool = Tool.builder()
 *     .name("get_weather")
 *     .description("Get current weather for a city")
 *     .parameters(Map.of(
 *         "type", "object",
 *         "properties", Map.of(
 *             "city", Map.of("type", "string", "description", "City name")
 *         ),
 *         "required", List.of("city")
 *     ))
 *     .executor(args -> {
 *         String city = (String) args.get("city");
 *         return "Weather in " + city + ": Sunny, 25°C";
 *     })
 *     .build();
 * }</pre>
 */
public class Tool {

    private final String name;
    private final String description;
    private final Map<String, Object> parameters;
    private final ToolExecutor executor;

    /**
     * Creates a new Tool.
     *
     * @param name the unique tool name
     * @param description what the tool does
     * @param parameters JSON Schema for parameters
     * @param executor the execution function
     */
    public Tool(String name, String description, Map<String, Object> parameters,
                ToolExecutor executor) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.executor = executor;
    }

    /** @return the tool name */
    public String getName() { return name; }

    /** @return the tool description */
    public String getDescription() { return description; }

    /** @return the parameter JSON Schema */
    public Map<String, Object> getParameters() { return parameters; }

    /**
     * Executes the tool with the given arguments.
     *
     * @param arguments the tool arguments as a map
     * @return the tool execution result
     * @throws Exception if execution fails
     */
    public String execute(Map<String, Object> arguments) throws Exception {
        return executor.execute(arguments);
    }

    /**
     * Executes the tool asynchronously.
     *
     * @param arguments the tool arguments
     * @return a future resolving to the result
     */
    public CompletableFuture<String> executeAsync(Map<String, Object> arguments) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executor.execute(arguments);
            } catch (Exception e) {
                throw new RuntimeException("Tool execution failed: " + name, e);
            }
        });
    }

    /**
     * Converts this tool to a ToolDefinition for LLM API calls.
     *
     * @return a ToolDefinition
     */
    public ToolDefinition toDefinition() {
        return ToolDefinition.of(name, description, parameters);
    }

    /**
     * Creates a new builder.
     * @return a Builder
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for Tool.
     */
    public static class Builder {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private ToolExecutor executor;

        /** Sets the tool name. */
        public Builder name(String name) { this.name = name; return this; }

        /** Sets the tool description. */
        public Builder description(String description) { this.description = description; return this; }

        /** Sets the JSON Schema parameters. */
        public Builder parameters(Map<String, Object> parameters) { this.parameters = parameters; return this; }

        /** Sets the executor function. */
        public Builder executor(ToolExecutor executor) { this.executor = executor; return this; }

        /** Builds the tool. */
        public Tool build() {
            if (name == null) throw new IllegalArgumentException("Tool name is required");
            if (executor == null) throw new IllegalArgumentException("Tool executor is required");
            return new Tool(name, description, parameters, executor);
        }
    }

    /**
     * Functional interface for tool execution.
     */
    @FunctionalInterface
    public interface ToolExecutor {
        /**
         * Executes the tool logic.
         *
         * @param arguments the parsed arguments
         * @return the execution result as a string
         * @throws Exception on failure
         */
        String execute(Map<String, Object> arguments) throws Exception;
    }
}
