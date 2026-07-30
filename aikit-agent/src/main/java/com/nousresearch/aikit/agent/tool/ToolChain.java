package com.nousresearch.aikit.agent.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Chains multiple tool calls sequentially, piping output from one tool
 * as input to the next.
 *
 * <p>Useful for complex multi-step operations where tools depend on
 * each other's results. Each step can reference previous results via
 * variable interpolation {@code {{ previous.tool_name }}}.</p>
 */
public class ToolChain {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final List<ToolChainStep> steps;
    private final ToolRegistry registry;

    /**
     * Creates a new tool chain.
     *
     * @param registry the tool registry for resolving tool names
     */
    public ToolChain(ToolRegistry registry) {
        this.registry = registry;
        this.steps = new ArrayList<>();
    }

    /**
     * Adds a step to the chain.
     *
     * @param toolName the tool to invoke
     * @param arguments the arguments for this step
     * @return this chain for fluent use
     */
    public ToolChain addStep(String toolName, Map<String, Object> arguments) {
        steps.add(new ToolChainStep(toolName, arguments));
        return this;
    }

    /**
     * Executes the entire tool chain sequentially.
     *
     * @return list of results from each step
     * @throws Exception if any step fails
     */
    public List<ToolChainResult> execute() throws Exception {
        List<ToolChainResult> results = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            ToolChainStep step = steps.get(i);

            // Resolve variable references in arguments
            Map<String, Object> resolvedArgs = resolveArguments(
                    step.arguments, results);

            Tool tool = registry.get(step.toolName);
            if (tool == null) {
                throw new IllegalArgumentException(
                        "Tool not found in chain: " + step.toolName);
            }

            String output = tool.execute(resolvedArgs);
            results.add(new ToolChainResult(step.toolName, resolvedArgs, output, true));
        }

        return results;
    }

    /**
     * Resolves {{ previous.tool_name.arg }} references in arguments.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveArguments(
            Map<String, Object> args, List<ToolChainResult> previousResults) {
        if (previousResults.isEmpty()) return args;

        // Deep copy and resolve
        try {
            String json = OBJECT_MAPPER.writeValueAsString(args);

            for (ToolChainResult prev : previousResults) {
                String placeholder = "{{ previous." + prev.toolName + " }}";
                if (json.contains(placeholder)) {
                    json = json.replace(placeholder, prev.output);
                }
                // Also support {{ previous.tool_name.result }}
                String resultPlaceholder = "{{ " + prev.toolName + ".result }}";
                if (json.contains(resultPlaceholder)) {
                    json = json.replace(resultPlaceholder, prev.output);
                }
            }

            return OBJECT_MAPPER.readValue(json,
                    new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return args; // Return unresolved if parsing fails
        }
    }

    /** @return number of steps */
    public int stepCount() { return steps.size(); }

    /**
     * A single step in a tool chain.
     */
    private static class ToolChainStep {
        final String toolName;
        final Map<String, Object> arguments;

        ToolChainStep(String toolName, Map<String, Object> arguments) {
            this.toolName = toolName;
            this.arguments = arguments;
        }
    }

    /**
     * Result of executing a single step in the chain.
     */
    public static class ToolChainResult {
        private final String toolName;
        private final Map<String, Object> arguments;
        private final String output;
        private final boolean success;

        /**
         * Creates a new result.
         */
        public ToolChainResult(String toolName, Map<String, Object> arguments,
                               String output, boolean success) {
            this.toolName = toolName;
            this.arguments = arguments;
            this.output = output;
            this.success = success;
        }

        /** @return the tool name */
        public String getToolName() { return toolName; }

        /** @return the resolved arguments */
        public Map<String, Object> getArguments() { return arguments; }

        /** @return the tool output */
        public String getOutput() { return output; }

        /** @return whether execution succeeded */
        public boolean isSuccess() { return success; }

        @Override
        public String toString() {
            String out = output != null ? output.substring(0, Math.min(output.length(), 80)) : "null";
            return "ToolChainResult{" + toolName + ": " + out + "}";
        }
    }
}
