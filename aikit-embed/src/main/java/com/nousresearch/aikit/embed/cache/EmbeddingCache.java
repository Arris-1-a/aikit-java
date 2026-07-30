package com.nousresearch.aikit.embed.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * LRU cache for embedding vectors, keyed by a hash of the input text.
 *
 * <p>Uses Caffeine for high-performance in-memory caching. Cache keys
 * are SHA-256 hashes of the input text to avoid storing raw text.</p>
 *
 * <p>This significantly reduces API costs and latency when the same
 * or similar texts are embedded repeatedly.</p>
 */
public class EmbeddingCache {

    private final Cache<String, float[]> cache;

    /**
     * Creates an embedding cache with the given size and TTL.
     *
     * @param maxSize maximum number of cached entries
     * @param ttl time-to-live for cached entries
     */
    public EmbeddingCache(int maxSize, Duration ttl) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl)
                .recordStats()
                .build();
    }

    /**
     * Creates a cache with default settings (1000 entries, 1 hour TTL).
     */
    public EmbeddingCache() {
        this(1000, Duration.ofHours(1));
    }

    /**
     * Gets a cached embedding for the given text.
     *
     * @param text the input text
     * @return the cached embedding, or null if not cached
     */
    public float[] get(String text) {
        return cache.getIfPresent(hash(text));
    }

    /**
     * Stores an embedding in the cache.
     *
     * @param text the input text
     * @param embedding the embedding vector
     */
    public void put(String text, float[] embedding) {
        cache.put(hash(text), embedding.clone());
    }

    /**
     * Checks if the cache contains an embedding for the given text.
     *
     * @param text the input text
     * @return true if cached
     */
    public boolean contains(String text) {
        return cache.getIfPresent(hash(text)) != null;
    }

    /** Clears all cached embeddings. */
    public void clear() {
        cache.invalidateAll();
    }

    /** @return the current cache size */
    public long size() {
        return cache.estimatedSize();
    }

    /** @return cache hit rate (0.0–1.0) */
    public double hitRate() {
        return cache.stats().hitRate();
    }

    /** @return cache hit count */
    public long hitCount() {
        return cache.stats().hitCount();
    }

    /** @return cache miss count */
    public long missCount() {
        return cache.stats().missCount();
    }

    /**
     * Computes a SHA-256 hash of the input text.
     */
    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
