package com.nousresearch.aikit.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.Objects;

/**
 * Represents a tool call requested by the LLM. A tool call contains
 * a unique identifier, the function name to invoke, and JSON-encoded arguments.
 *
 * <p>This maps to the standard function-calling / tool-use patterns in
 * OpenAI, Anthropic, and other LLM APIs.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ToolCall.Builder.class)
public class ToolCall {

    private final String id;
    private final String type;
    private final FunctionCall function;

    private ToolCall(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.type = builder.type != null ? builder.type : "function";
        this.function = Objects.requireNonNull(builder.function, "function must not be null");
    }

    /**
     * Returns the unique identifier for this tool call.
     * @return the tool call ID
     */
    public String getId() { return id; }

    /**
     * Returns the type of tool call (typically "function").
     * @return the tool call type
     */
    public String getType() { return type; }

    /**
     * Returns the function call details.
     * @return the function call
     */
    public FunctionCall getFunction() { return function; }

    public static Builder builder() { return new Builder(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolCall)) return false;
        ToolCall toolCall = (ToolCall) o;
        return id.equals(toolCall.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "ToolCall{id='" + id + "', function='" + function.getName() + "'}";
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String id;
        private String type;
        private FunctionCall function;

        @JsonProperty("id")
        public Builder id(String id) { this.id = id; return this; }

        @JsonProperty("type")
        public Builder type(String type) { this.type = type; return this; }

        @JsonProperty("function")
        public Builder function(FunctionCall function) { this.function = function; return this; }

        public ToolCall build() { return new ToolCall(this); }
    }

    /**
     * Represents a specific function invocation within a tool call.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionCall {
        private final String name;
        private final String arguments;

        public FunctionCall(@JsonProperty("name") String name,
                            @JsonProperty("arguments") String arguments) {
            this.name = Objects.requireNonNull(name, "name must not be null");
            this.arguments = arguments != null ? arguments : "{}";
        }

        /** @return the function name */
        public String getName() { return name; }

        /** @return JSON-encoded arguments string */
        public String getArguments() { return arguments; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FunctionCall)) return false;
            FunctionCall that = (FunctionCall) o;
            return name.equals(that.name) && arguments.equals(that.arguments);
        }

        @Override
        public int hashCode() { return Objects.hash(name, arguments); }

        @Override
        public String toString() {
            return "FunctionCall{name='" + name + "', arguments=" + arguments + "}";
        }
    }
}
