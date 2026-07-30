package com.nousresearch.aikit.embed;

import com.nousresearch.aikit.core.EmbeddingProvider;
import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.model.EmbeddingRequest;
import com.nousresearch.aikit.core.model.EmbeddingResponse;
import com.nousresearch.aikit.embed.cache.EmbeddingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base for embedding providers with caching support.
 *
 * <p>Handles cache lookups and batch processing, delegating actual
 * API calls to subclasses.</p>
 */
public abstract class AbstractEmbeddingProvider implements EmbeddingProvider {

    protected final String apiKey;
    protected final String model;
    protected final int dimension;
    protected final EmbeddingCache cache;

    /**
     * Creates a new embedding provider.
     *
     * @param apiKey the API key
     * @param model the embedding model name
     * @param dimension the embedding dimensionality
     * @param cache the embedding cache (can be null to disable)
     */
    protected AbstractEmbeddingProvider(String apiKey, String model, int dimension, EmbeddingCache cache) {
        this.apiKey = apiKey;
        this.model = model;
        this.dimension = dimension;
        this.cache = cache;
    }

    @Override
    public float[] embed(String text) {
        // Check cache first
        if (cache != null) {
            float[] cached = cache.get(text);
            if (cached != null) {
                return cached;
            }
        }

        EmbeddingRequest request = new EmbeddingRequest(model, text);
        EmbeddingResponse response = createEmbeddings(request);
        float[] embedding = response.getFirstEmbedding();

        if (embedding == null) {
            throw new AiKitException("No embedding returned for text");
        }

        // Cache the result
        if (cache != null) {
            cache.put(text, embedding);
        }

        return embedding;
    }

    @Override
    public float[][] embedBatch(List<String> texts) {
        List<String> uncached = new ArrayList<>();
        float[][] results = new float[texts.size()][];

        // Check cache for each text
        for (int i = 0; i < texts.size(); i++) {
            if (cache != null) {
                float[] cached = cache.get(texts.get(i));
                if (cached != null) {
                    results[i] = cached;
                } else {
                    uncached.add(texts.get(i));
                }
            } else {
                uncached.add(texts.get(i));
            }
        }

        if (uncached.isEmpty()) {
            return results;
        }

        // Batch embed uncached texts
        EmbeddingRequest request = new EmbeddingRequest(model, uncached);
        EmbeddingResponse response = createEmbeddings(request);
        float[][] embeddings = response.getAllEmbeddings();

        // Map back to original positions and cache
        int uncachedIdx = 0;
        for (int i = 0; i < texts.size(); i++) {
            if (results[i] == null) {
                if (uncachedIdx < embeddings.length) {
                    results[i] = embeddings[uncachedIdx];
                    if (cache != null) {
                        cache.put(texts.get(i), embeddings[uncachedIdx]);
                    }
                    uncachedIdx++;
                }
                // else: provider returned fewer embeddings than expected
            }
        }

        return results;
    }

    @Override
    public CompletableFuture<EmbeddingResponse> createEmbeddingsAsync(EmbeddingRequest request) {
        return CompletableFuture.supplyAsync(() -> createEmbeddings(request));
    }

    @Override
    public int getDimension() { return dimension; }

    @Override
    public String getDefaultModel() { return model; }

    @Override
    public void close() {
        if (cache != null) {
            cache.clear();
        }
    }
}
