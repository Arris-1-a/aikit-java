package com.nousresearch.aikit.core;

import com.nousresearch.aikit.core.model.ChatMessage;
import com.nousresearch.aikit.core.model.ChatRequest;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ChatRequestTest {

    @Test
    void shouldBuildMinimalRequest() {
        ChatRequest req = ChatRequest.builder()
                .model("gpt-4")
                .messages(List.of(ChatMessage.user("Hi")))
                .build();
        assertThat(req.getModel()).isEqualTo("gpt-4");
        assertThat(req.getMessages()).hasSize(1);
        assertThat(req.getTemperature()).isEqualTo(0.7);
        assertThat(req.isStream()).isFalse();
    }

    @Test
    void shouldBuildWithAllOptions() {
        ChatRequest req = ChatRequest.builder()
                .model("gpt-4")
                .messages(List.of(ChatMessage.user("Hi")))
                .temperature(0.5)
                .maxTokens(1000)
                .topP(0.9)
                .stream(true)
                .build();
        assertThat(req.getTemperature()).isEqualTo(0.5);
        assertThat(req.getMaxTokens()).isEqualTo(1000);
        assertThat(req.getTopP()).isEqualTo(0.9);
        assertThat(req.isStream()).isTrue();
    }
}
