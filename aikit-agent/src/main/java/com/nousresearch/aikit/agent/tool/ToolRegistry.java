package com.nousresearch.aikit.agent.tool;

import com.nousresearch.aikit.core.model.ToolDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry for agent tools.
 *
 * <p>Manages tool registration, lookup, and provides the list of
 * tool definitions for LLM function-calling APIs.</p>
 */
public class ToolRegistry {

    private final ConcurrentHashMap<String, Tool> tools;

    /**
     * Creates an empty tool registry.
     */
    public ToolRegistry() {
        this.tools = new ConcurrentHashMap<>();
    }

    /**
     * Registers a tool.
     *
     * @param tool the tool to register
     * @throws IllegalArgumentException if a tool with the same name already exists
     */
    public void register(Tool tool) {
        if (tools.containsKey(tool.getName())) {
            throw new IllegalArgumentException(
                    "Tool already registered: " + tool.getName());
        }
        tools.put(tool.getName(), tool);
    }

    /**
     * Registers a tool, replacing any existing tool with the same name.
     *
     * @param tool the tool to register
     */
    public void registerOrReplace(Tool tool) {
        tools.put(tool.getName(), tool);
    }

    /**
     * Unregisters a tool.
     *
     * @param name the tool name
     * @return the removed tool, or null
     */
    public Tool unregister(String name) {
        return tools.remove(name);
    }

    /**
     * Gets a tool by name.
     *
     * @param name the tool name
     * @return the tool, or null if not found
     */
    public Tool get(String name) {
        return tools.get(name);
    }

    /**
     * Returns all registered tool definitions for LLM API calls.
     *
     * @return list of ToolDefinitions
     */
    public List<ToolDefinition> getToolDefinitions() {
        List<ToolDefinition> definitions = new ArrayList<>();
        for (Tool tool : tools.values()) {
            definitions.add(tool.toDefinition());
        }
        return Collections.unmodifiableList(definitions);
    }

    /**
     * Returns all registered tools.
     *
     * @return unmodifiable list of tools
     */
    public List<Tool> getTools() {
        return Collections.unmodifiableList(new ArrayList<>(tools.values()));
    }

    /** @return the number of registered tools */
    public int size() { return tools.size(); }

    /** Removes all tools. */
    public void clear() { tools.clear(); }

    /** @return set of tool names */
    public java.util.Set<String> getToolNames() {
        return Collections.unmodifiableSet(tools.keySet());
    }
}
