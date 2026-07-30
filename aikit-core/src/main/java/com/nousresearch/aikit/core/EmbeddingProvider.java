package com.nousresearch.aikit.core;

import com.nousresearch.aikit.core.model.EmbeddingRequest;
import com.nousresearch.aikit.core.model.EmbeddingResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Core interface for text embedding providers.
 *
 * <p>Embedding providers convert text into dense vector representations
 * that can be used for semantic search, clustering, and other NLP tasks.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * EmbeddingProvider embedder = OpenAIEmbeddingProvider.builder()
 *     .apiKey("sk-...")
 *     .model("text-embedding-3-small")
 *     .build();
 *
 * float[] vector = embedder.embed("Hello, world!");
 * }</pre>
 */
public interface EmbeddingProvider extends AutoCloseable {

    /**
     * Generates an embedding for a single text input.
     *
     * @param text the text to embed
     * @return the embedding vector as a float array
     */
    float[] embed(String text);

    /**
     * Generates embeddings for multiple text inputs in batch.
     *
     * @param texts list of texts to embed
     * @return 2D float array [text_index][dimension]
     */
    float[][] embedBatch(List<String> texts);

    /**
     * Sends a raw embedding request and returns the full response.
     *
     * @param request the embedding request
     * @return the embedding response with metadata
     */
    EmbeddingResponse createEmbeddings(EmbeddingRequest request);

    /**
     * Sends an embedding request asynchronously.
     *
     * @param request the embedding request
     * @return a future resolving to the embedding response
     */
    CompletableFuture<EmbeddingResponse> createEmbeddingsAsync(EmbeddingRequest request);

    /**
     * Returns the embedding dimension for the current model.
     *
     * @return the number of dimensions in the embedding vector
     */
    int getDimension();

    /**
     * Returns the default model name for this provider.
     *
     * @return the default embedding model name
     */
    String getDefaultModel();
}
