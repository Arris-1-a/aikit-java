package com.nousresearch.aikit.agent;

import com.nousresearch.aikit.agent.tool.Tool;
import com.nousresearch.aikit.agent.tool.ToolRegistry;
import com.nousresearch.aikit.core.model.ToolDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ToolRegistryTest {

    private ToolRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
    }

    @Test
    void shouldRegisterTool() {
        Tool tool = createEchoTool();
        registry.register(tool);
        assertThat(registry.size()).isEqualTo(1);
        assertThat(registry.get("echo")).isNotNull();
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        registry.register(createEchoTool());
        assertThatThrownBy(() -> registry.register(createEchoTool()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldAllowReplace() {
        registry.register(createEchoTool());
        Tool betterEcho = Tool.builder()
                .name("echo")
                .description("Better echo")
                .executor(args -> "Better: " + args.get("text"))
                .build();
        registry.registerOrReplace(betterEcho);
        assertThat(registry.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnToolDefinitions() {
        registry.register(createEchoTool());
        registry.register(createWeatherTool());

        List<ToolDefinition> defs = registry.getToolDefinitions();
        assertThat(defs).hasSize(2);
        assertThat(defs).extracting(d -> d.getFunction().getName())
                .containsExactlyInAnyOrder("echo", "get_weather");
    }

    @Test
    void shouldUnregisterTool() {
        registry.register(createEchoTool());
        registry.unregister("echo");
        assertThat(registry.size()).isEqualTo(0);
    }

    @Test
    void shouldClearAllTools() {
        registry.register(createEchoTool());
        registry.register(createWeatherTool());
        registry.clear();
        assertThat(registry.size()).isEqualTo(0);
    }

    @Test
    void shouldExecuteTool() throws Exception {
        Tool tool = createEchoTool();
        String result = tool.execute(Map.of("text", "hello"));
        assertThat(result).isEqualTo("Echo: hello");
    }

    @Test
    void shouldCreateToolDefinition() {
        Tool tool = createWeatherTool();
        ToolDefinition def = tool.toDefinition();
        assertThat(def.getFunction().getName()).isEqualTo("get_weather");
        assertThat(def.getFunction().getDescription()).contains("weather");
    }

    private Tool createEchoTool() {
        return Tool.builder()
                .name("echo")
                .description("Echoes the input text")
                .parameters(Map.of("type", "object",
                        "properties", Map.of("text",
                                Map.of("type", "string"))))
                .executor(args -> "Echo: " + args.get("text"))
                .build();
    }

    private Tool createWeatherTool() {
        return Tool.builder()
                .name("get_weather")
                .description("Gets weather for a city")
                .parameters(Map.of("type", "object",
                        "properties", Map.of("city",
                                Map.of("type", "string", "description", "City name"))))
                .executor(args -> "Sunny, 25°C in " + args.get("city"))
                .build();
    }
}
