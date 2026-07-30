package com.nousresearch.aikit.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Defines a tool/function that an LLM can call during chat completion.
 *
 * <p>Follows the OpenAI function-calling JSON schema format, with a name,
 * description, and JSON Schema parameters definition.</p>
 *
 * @see ToolCall
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolDefinition {

    private final String type;
    private final FunctionDefinition function;

    /**
     * Creates a new tool definition.
     * @param type the tool type (typically "function")
     * @param function the function definition
     */
    public ToolDefinition(@JsonProperty("type") String type,
                          @JsonProperty("function") FunctionDefinition function) {
        this.type = type != null ? type : "function";
        this.function = Objects.requireNonNull(function, "function must not be null");
    }

    /** @return the tool type */
    public String getType() { return type; }

    /** @return the function definition */
    public FunctionDefinition getFunction() { return function; }

    /**
     * Convenience factory method for creating a function tool definition.
     * @param name the function name
     * @param description what the function does
     * @param parameters JSON Schema for parameters
     * @return a new ToolDefinition
     */
    public static ToolDefinition of(String name, String description, Map<String, Object> parameters) {
        return new ToolDefinition("function", new FunctionDefinition(name, description, parameters));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolDefinition)) return false;
        ToolDefinition that = (ToolDefinition) o;
        return Objects.equals(type, that.type) && function.equals(that.function);
    }

    @Override
    public int hashCode() { return Objects.hash(type, function); }

    @Override
    public String toString() {
        return "ToolDefinition{type='" + type + "', function='" + function.getName() + "'}";
    }

    /**
     * Defines a specific function within a tool definition.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionDefinition {
        private final String name;
        private final String description;
        private final Map<String, Object> parameters;

        public FunctionDefinition(@JsonProperty("name") String name,
                                  @JsonProperty("description") String description,
                                  @JsonProperty("parameters") Map<String, Object> parameters) {
            this.name = Objects.requireNonNull(name, "name must not be null");
            this.description = description;
            this.parameters = parameters != null
                    ? Collections.unmodifiableMap(parameters) : Collections.emptyMap();
        }

        /** @return the function name */
        public String getName() { return name; }

        /** @return the function description */
        public String getDescription() { return description; }

        /** @return JSON Schema for function parameters */
        public Map<String, Object> getParameters() { return parameters; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FunctionDefinition)) return false;
            FunctionDefinition that = (FunctionDefinition) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() { return Objects.hash(name); }
    }
}
