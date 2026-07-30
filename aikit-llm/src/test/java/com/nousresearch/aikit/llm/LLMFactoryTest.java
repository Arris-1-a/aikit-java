package com.nousresearch.aikit.llm;

import com.nousresearch.aikit.llm.config.LLMConfig;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LLMFactoryTest {

    @Test
    void shouldCreateOpenAIClient() {
        LLMConfig config = LLMConfig.builder()
                .provider("openai")
                .apiKey("sk-test")
                .model("gpt-4o")
                .build();

        var client = LLMFactory.create(config);
        assertThat(client.getProviderType()).isEqualTo("openai");
        assertThat(client.getDefaultModel()).isEqualTo("gpt-4o");
    }

    @Test
    void shouldCreateAnthropicClient() {
        LLMConfig config = LLMConfig.builder()
                .provider("anthropic")
                .apiKey("sk-ant-test")
                .model("claude-3-5-sonnet-20241022")
                .build();

        var client = LLMFactory.create(config);
        assertThat(client.getProviderType()).isEqualTo("anthropic");
    }

    @Test
    void shouldCreateDeepSeekClient() {
        LLMConfig config = LLMConfig.builder()
                .provider("deepseek")
                .apiKey("sk-test")
                .build();

        var client = LLMFactory.create(config);
        assertThat(client.getProviderType()).isEqualTo("deepseek");
        assertThat(client.getDefaultModel()).isEqualTo("deepseek-chat");
    }

    @Test
    void shouldCreateGeminiClient() {
        LLMConfig config = LLMConfig.builder()
                .provider("gemini")
                .apiKey("test-key")
                .build();

        var client = LLMFactory.create(config);
        assertThat(client.getProviderType()).isEqualTo("gemini");
    }

    @Test
    void shouldRejectUnknownProvider() {
        LLMConfig config = LLMConfig.builder()
                .provider("unknown")
                .apiKey("test")
                .build();

        assertThatThrownBy(() -> LLMFactory.create(config))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported");
    }
}
