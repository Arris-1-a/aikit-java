package com.nousresearch.aikit.core;

import com.nousresearch.aikit.core.model.ToolCall;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ToolCallTest {

    @Test
    void shouldCreateToolCall() {
        ToolCall.FunctionCall fn = new ToolCall.FunctionCall("get_weather", "{\"city\": \"Tokyo\"}");
        ToolCall tc = ToolCall.builder()
                .id("call_001")
                .function(fn)
                .build();
        assertThat(tc.getId()).isEqualTo("call_001");
        assertThat(tc.getFunction().getName()).isEqualTo("get_weather");
        assertThat(tc.getFunction().getArguments()).contains("Tokyo");
    }

    @Test
    void shouldDefaultTypeToFunction() {
        ToolCall tc = ToolCall.builder()
                .id("call_002")
                .function(new ToolCall.FunctionCall("test", "{}"))
                .build();
        assertThat(tc.getType()).isEqualTo("function");
    }
}
