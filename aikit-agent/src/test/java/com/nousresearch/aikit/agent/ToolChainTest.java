package com.nousresearch.aikit.agent;

import com.nousresearch.aikit.agent.tool.Tool;
import com.nousresearch.aikit.agent.tool.ToolChain;
import com.nousresearch.aikit.agent.tool.ToolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class ToolChainTest {

    private ToolRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
        registry.register(Tool.builder()
                .name("get_city")
                .description("Returns the capital")
                .executor(args -> "Tokyo")
                .build());
        registry.register(Tool.builder()
                .name("get_weather")
                .description("Gets weather")
                .executor(args -> "Weather in " + args.get("city") + ": Sunny")
                .build());
    }

    @Test
    void shouldExecuteChainSequentially() throws Exception {
        ToolChain chain = new ToolChain(registry);
        chain.addStep("get_city", Map.of("country", "Japan"));
        chain.addStep("get_weather", Map.of("city", "{{ get_city.result }}"));

        List<ToolChain.ToolChainResult> results = chain.execute();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getOutput()).isEqualTo("Tokyo");
        assertThat(results.get(1).getOutput()).contains("Sunny");
    }
}
