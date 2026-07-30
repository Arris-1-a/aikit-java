package com.nousresearch.aikit.llm;

import com.nousresearch.aikit.core.LLMProvider;
import com.nousresearch.aikit.llm.client.AnthropicClient;
import com.nousresearch.aikit.llm.client.DeepSeekClient;
import com.nousresearch.aikit.llm.client.GeminiClient;
import com.nousresearch.aikit.llm.client.OpenAIClient;
import com.nousresearch.aikit.llm.config.LLMConfig;

import java.util.Locale;

/**
 * Factory for creating LLM provider instances from configuration.
 *
 * <p>Provides a single entry point for instantiating any supported
 * LLM client based on the provider type in the configuration.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * LLMConfig config = LLMConfig.builder()
 *     .provider("anthropic")
 *     .apiKey("sk-ant-...")
 *     .model("claude-3-5-sonnet-20241022")
 *     .build();
 *
 * LLMProvider llm = LLMFactory.create(config);
 * }</pre>
 */
public final class LLMFactory {

    private LLMFactory() {
        // Utility class
    }

    /**
     * Creates an LLM provider based on the configuration.
     *
     * @param config the LLM configuration
     * @return a configured LLM provider
     * @throws IllegalArgumentException if the provider type is unsupported
     */
    public static LLMProvider create(LLMConfig config) {
        String provider = config.getProvider().toLowerCase(Locale.ROOT);
        String apiKey = config.getApiKey();
        String model = config.getModel();
        String baseUrl = config.getBaseUrl();

        switch (provider) {
            case "openai":
                return buildOpenAI(apiKey, model, baseUrl);
            case "anthropic":
                return buildAnthropic(apiKey, model, baseUrl);
            case "deepseek":
                return buildDeepSeek(apiKey, model, baseUrl);
            case "gemini":
                return buildGemini(apiKey, model, baseUrl);
            default:
                throw new IllegalArgumentException(
                        "Unsupported LLM provider: " + provider
                        + ". Supported: openai, anthropic, deepseek, gemini");
        }
    }

    private static OpenAIClient buildOpenAI(String apiKey, String model, String baseUrl) {
        OpenAIClient.Builder builder = OpenAIClient.builder().apiKey(apiKey);
        if (model != null) builder.model(model);
        if (baseUrl != null) builder.baseUrl(baseUrl);
        return builder.build();
    }

    private static AnthropicClient buildAnthropic(String apiKey, String model, String baseUrl) {
        AnthropicClient.Builder builder = AnthropicClient.builder().apiKey(apiKey);
        if (model != null) builder.defaultModel(model);
        if (baseUrl != null) builder.baseUrl(baseUrl);
        return builder.build();
    }

    private static DeepSeekClient buildDeepSeek(String apiKey, String model, String baseUrl) {
        DeepSeekClient.Builder builder = DeepSeekClient.builder().apiKey(apiKey);
        if (model != null) builder.defaultModel(model);
        if (baseUrl != null) builder.baseUrl(baseUrl);
        return builder.build();
    }

    private static GeminiClient buildGemini(String apiKey, String model, String baseUrl) {
        GeminiClient.Builder builder = GeminiClient.builder().apiKey(apiKey);
        if (model != null) builder.defaultModel(model);
        if (baseUrl != null) builder.baseUrl(baseUrl);
        return builder.build();
    }
}
