package com.nousresearch.aikit.core;

import com.nousresearch.aikit.core.model.ChatMessage;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Core interface for Large Language Model (LLM) providers.
 *
 * <p>All LLM client implementations (OpenAI, Anthropic, DeepSeek, Gemini)
 * implement this interface, providing a uniform API for chat completions,
 * both synchronous and streaming.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * LLMProvider llm = OpenAIClient.builder()
 *     .apiKey("sk-...")
 *     .model("gpt-4")
 *     .build();
 *
 * ChatRequest request = ChatRequest.builder()
 *     .model("gpt-4")
 *     .messages(List.of(ChatMessage.user("Hello!")))
 *     .build();
 *
 * ChatResponse response = llm.chat(request);
 * System.out.println(response.getFirstContent());
 * }</pre>
 *
 * @see ChatRequest
 * @see ChatResponse
 */
public interface LLMProvider extends AutoCloseable {

    /**
     * Sends a chat completion request and blocks until the response is received.
     *
     * @param request the chat request with messages and parameters
     * @return the complete chat response
     * @throws com.nousresearch.aikit.core.exception.AiKitException on API errors
     */
    ChatResponse chat(ChatRequest request);

    /**
     * Sends a chat completion request asynchronously.
     *
     * @param request the chat request with messages and parameters
     * @return a future that resolves to the chat response
     */
    CompletableFuture<ChatResponse> chatAsync(ChatRequest request);

    /**
     * Sends a streaming chat completion request. Chunks are delivered
     * to the provided consumer as they arrive via SSE.
     *
     * @param request the chat request (must have stream=true)
     * @param onChunk consumer that receives each response chunk
     * @return the final aggregated ChatResponse
     */
    ChatResponse chatStream(ChatRequest request, Consumer<ChatResponse> onChunk);

    /**
     * Convenience method: sends a simple single-message prompt and returns
     * the text content of the first choice.
     *
     * @param systemPrompt optional system prompt (can be null)
     * @param userMessage the user message
     * @return the assistant's text response
     */
    default String chat(String systemPrompt, String userMessage) {
        ChatRequest.Builder builder = ChatRequest.builder()
                .model(getDefaultModel());
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            builder.messages(List.of(
                    ChatMessage.system(systemPrompt),
                    ChatMessage.user(userMessage)));
        } else {
            builder.messages(List.of(ChatMessage.user(userMessage)));
        }
        ChatResponse response = chat(builder.build());
        return response.getFirstContent();
    }

    /**
     * Returns the default model name for this provider.
     *
     * @return the default model name
     */
    String getDefaultModel();

    /**
     * Returns the provider type identifier.
     *
     * @return a string like "openai", "anthropic", "deepseek", "gemini"
     */
    String getProviderType();
}
