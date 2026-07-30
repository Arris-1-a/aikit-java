package com.nousresearch.aikit.core;

import com.nousresearch.aikit.core.model.ChatMessage;
import com.nousresearch.aikit.core.model.ToolCall;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ChatMessage}.
 */
class ChatMessageTest {

    @Test
    void shouldCreateSystemMessage() {
        ChatMessage msg = ChatMessage.system("You are helpful.");
        assertThat(msg.getRole()).isEqualTo("system");
        assertThat(msg.getContent()).isEqualTo("You are helpful.");
        assertThat(msg.getToolCalls()).isEmpty();
    }

    @Test
    void shouldCreateUserMessage() {
        ChatMessage msg = ChatMessage.user("Hello!");
        assertThat(msg.getRole()).isEqualTo("user");
        assertThat(msg.getContent()).isEqualTo("Hello!");
    }

    @Test
    void shouldCreateAssistantMessage() {
        ChatMessage msg = ChatMessage.assistant("Hi there!");
        assertThat(msg.getRole()).isEqualTo("assistant");
        assertThat(msg.getContent()).isEqualTo("Hi there!");
    }

    @Test
    void shouldCreateToolMessage() {
        ChatMessage msg = ChatMessage.tool("call_123", "Result data");
        assertThat(msg.getRole()).isEqualTo("tool");
        assertThat(msg.getContent()).isEqualTo("Result data");
        assertThat(msg.getToolCallId()).isEqualTo("call_123");
    }

    @Test
    void shouldRequireRole() {
        assertThatThrownBy(() -> ChatMessage.builder().build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldEqualSameContent() {
        ChatMessage a = ChatMessage.user("Hello");
        ChatMessage b = ChatMessage.user("Hello");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void shouldHaveBuilderPattern() {
        ChatMessage msg = ChatMessage.builder()
                .role("user")
                .content("Test")
                .name("Alice")
                .build();
        assertThat(msg.getRole()).isEqualTo("user");
        assertThat(msg.getContent()).isEqualTo("Test");
        assertThat(msg.getName()).isEqualTo("Alice");
    }
}
