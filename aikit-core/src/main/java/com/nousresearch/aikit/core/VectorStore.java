package com.nousresearch.aikit.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Core interface for vector storage and similarity search.
 *
 * <p>A VectorStore indexes high-dimensional embedding vectors and supports
 * k-nearest-neighbor (kNN) search with metadata filtering.</p>
 *
 * <p>Implementations may be in-memory (HNSW-based), persistent, or
 * backed by external services (Pinecone, Milvus, etc.).</p>
 *
 * @param <T> the type of metadata object stored alongside vectors
 */
public interface VectorStore<T> {

    /**
     * Adds a single vector with associated metadata to the store.
     *
     * @param id unique identifier for this entry
     * @param vector the embedding vector
     * @param metadata optional metadata (can be null)
     */
    void add(String id, float[] vector, T metadata);

    /**
     * Adds multiple vectors in batch.
     *
     * @param entries list of vector entries to add
     */
    void addAll(List<VectorEntry<T>> entries);

    /**
     * Searches for the k nearest neighbors to the query vector.
     *
     * @param query the query vector
     * @param k number of results to return
     * @return list of search results ordered by descending similarity
     */
    List<VectorSearchResult<T>> search(float[] query, int k);

    /**
     * Searches with metadata filter.
     *
     * @param query the query vector
     * @param k number of results to return
     * @param filter metadata filter criteria
     * @return filtered and ranked search results
     */
    List<VectorSearchResult<T>> search(float[] query, int k, Map<String, Object> filter);

    /**
     * Deletes a vector by ID.
     *
     * @param id the ID of the vector to delete
     * @return true if the vector was found and deleted
     */
    boolean delete(String id);

    /**
     * Gets a vector by ID.
     *
     * @param id the vector ID
     * @return the vector entry, or null if not found
     */
    VectorEntry<T> get(String id);

    /**
     * Returns the total number of vectors in the store.
     *
     * @return the vector count
     */
    int size();

    /**
     * Removes all vectors from the store.
     */
    void clear();

    /**
     * Saves the vector store to persistent storage.
     *
     * @param path the file path to save to
     */
    void save(String path);

    /**
     * Loads the vector store from persistent storage.
     *
     * @param path the file path to load from
     */
    void load(String path);

    /**
     * Returns all entries in the store. Default returns empty list;
     * implementations should override to provide actual entries.
     *
     * @return all vector entries
     */
    default List<VectorEntry<T>> entries() {
        return Collections.emptyList();
    }

    /**
     * A single entry in the vector store.
     *
     * @param <T> metadata type
     */
    class VectorEntry<T> {
        private final String id;
        private final float[] vector;
        private final T metadata;

        /**
         * Creates a new vector entry.
         */
        public VectorEntry(String id, float[] vector, T metadata) {
            this.id = id;
            this.vector = vector;
            this.metadata = metadata;
        }

        /** @return the entry identifier */
        public String getId() { return id; }

        /** @return the embedding vector */
        public float[] getVector() { return vector; }

        /** @return the associated metadata */
        public T getMetadata() { return metadata; }
    }

    /**
     * A search result containing a matched entry and its similarity score.
     *
     * @param <T> metadata type
     */
    class VectorSearchResult<T> {
        private final String id;
        private final float score;
        private final T metadata;

        /**
         * Creates a new search result.
         */
        public VectorSearchResult(String id, float score, T metadata) {
            this.id = id;
            this.score = score;
            this.metadata = metadata;
        }

        /** @return the matched entry identifier */
        public String getId() { return id; }

        /**
         * Returns the similarity score (higher is more similar).
         * For cosine similarity, this ranges from -1 to 1.
         * For euclidean distance (negated), higher is closer.
         */
        public float getScore() { return score; }

        /** @return the matched entry metadata */
        public T getMetadata() { return metadata; }

        @Override
        public String toString() {
            return "VectorSearchResult{id='" + id + "', score=" + score + "}";
        }
    }
}
