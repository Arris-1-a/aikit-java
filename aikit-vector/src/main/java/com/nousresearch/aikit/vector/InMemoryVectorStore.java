package com.nousresearch.aikit.vector;

import com.nousresearch.aikit.core.VectorStore;
import com.nousresearch.aikit.vector.hnsw.HnswIndex;
import com.nousresearch.aikit.vector.hnsw.HnswNode;
import com.nousresearch.aikit.vector.persistence.VectorStorePersistence;
import com.nousresearch.aikit.vector.similarity.CosineSimilarity;
import com.nousresearch.aikit.vector.similarity.SimilarityFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory vector store backed by an HNSW index.
 *
 * <p>Provides fast approximate nearest neighbor search with metadata
 * filtering. Thread-safe for concurrent reads; inserts are synchronized.
 * Supports persistence via JSON or binary format.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * VectorStore<String> store = InMemoryVectorStore.<String>builder()
 *     .dimension(1536)
 *     .build();
 *
 * store.add("doc1", embedding, "Hello world");
 * List<VectorSearchResult<String>> results = store.search(query, 5);
 * }</pre>
 *
 * @param <T> metadata type
 */
public class InMemoryVectorStore<T> implements VectorStore<T> {

    private final HnswIndex<T> index;
    private final SimilarityFunction similarityFunction;
    private final ConcurrentHashMap<String, T> metadataStore;

    private InMemoryVectorStore(Builder<T> builder) {
        this.similarityFunction = builder.similarityFunction != null
                ? builder.similarityFunction : new CosineSimilarity();
        this.index = new HnswIndex<>(
                builder.dimension,
                builder.m,
                builder.efConstruction,
                this.similarityFunction);
        this.metadataStore = new ConcurrentHashMap<>();
    }

    // ---- VectorStore implementation ----

    @Override
    public void add(String id, float[] vector, T metadata) {
        index.insert(id, vector, metadata);
        if (metadata != null) {
            metadataStore.put(id, metadata);
        }
    }

    @Override
    public void addAll(List<VectorEntry<T>> entries) {
        for (VectorEntry<T> entry : entries) {
            add(entry.getId(), entry.getVector(), entry.getMetadata());
        }
    }

    @Override
    public List<VectorSearchResult<T>> search(float[] query, int k) {
        return search(query, k, null);
    }

    @Override
    public List<VectorSearchResult<T>> search(float[] query, int k, Map<String, Object> filter) {
        List<Map.Entry<String, Float>> rawResults = index.search(query, k);

        return rawResults.stream()
                .map(e -> {
                    T metadata = metadataStore.get(e.getKey());
                    return new VectorSearchResult<>(e.getKey(), e.getValue(), metadata);
                })
                .filter(result -> matchesFilter(result, filter))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) {
        metadataStore.remove(id);
        return index.remove(id);
    }

    @Override
    public VectorEntry<T> get(String id) {
        HnswNode<T> node = index.getNode(id);
        if (node == null) return null;
        return new VectorEntry<>(id, node.getVector(), node.getMetadata());
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public void clear() {
        index.clear();
        metadataStore.clear();
    }

    @Override
    public void save(String path) {
        VectorStorePersistence.save(this, path);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(String path) {
        VectorStorePersistence.load(this, path);
    }

    /**
     * Checks if a search result matches the given metadata filter.
     */
    private boolean matchesFilter(VectorSearchResult<T> result, Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }
        if (result.getMetadata() == null) {
            return false;
        }

        T metadata = result.getMetadata();
        if (metadata instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> metaMap = (Map<String, Object>) metadata;
            return filter.entrySet().stream()
                    .allMatch(e -> metaMap.containsKey(e.getKey())
                            && String.valueOf(metaMap.get(e.getKey()))
                                    .equals(String.valueOf(e.getValue())));
        }
        return true;
    }

    /** @return the index dimension */
    public int getDimension() { return index.getDimension(); }

    /** @return the similarity function name */
    public String getSimilarityType() { return similarityFunction.getName(); }

    /**
     * Creates a new builder.
     * @param <T> metadata type
     * @return a Builder
     */
    public static <T> Builder<T> builder() { return new Builder<>(); }

    /**
     * Builder for InMemoryVectorStore.
     * @param <T> metadata type
     */
    public static class Builder<T> {
        private int dimension = 1536;
        private int m = 16;
        private int efConstruction = 200;
        private SimilarityFunction similarityFunction;

        /** Sets the vector dimension (required). */
        public Builder<T> dimension(int dimension) {
            this.dimension = dimension; return this;
        }

        /** Sets HNSW M parameter (connections per node). */
        public Builder<T> m(int m) {
            this.m = m; return this;
        }

        /** Sets HNSW efConstruction parameter. */
        public Builder<T> efConstruction(int efConstruction) {
            this.efConstruction = efConstruction; return this;
        }

        /** Sets the similarity function. */
        public Builder<T> similarityFunction(SimilarityFunction fn) {
            this.similarityFunction = fn; return this;
        }

        /** Builds the vector store. */
        public InMemoryVectorStore<T> build() {
            return new InMemoryVectorStore<>(this);
        }
    }
}
