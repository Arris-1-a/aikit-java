package com.nousresearch.aikit.vector.hnsw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A single node in the HNSW graph.
 *
 * <p>Each node stores its vector, metadata, and per-layer neighbor lists.
 * The node belongs to layers 0 through {@code maxLevel}, with layer 0
 * containing the most connections.</p>
 *
 * @param <T> metadata type
 */
public class HnswNode<T> {

    private final String id;
    private final float[] vector;
    private final T metadata;
    private final int maxLevel;
    private final Map<Integer, List<String>> neighbors;

    /**
     * Creates a new HNSW node.
     *
     * @param id unique identifier
     * @param vector the embedding vector
     * @param metadata associated metadata
     * @param maxLevel the highest layer this node belongs to
     */
    public HnswNode(String id, float[] vector, T metadata, int maxLevel) {
        this.id = id;
        this.vector = vector.clone();
        this.metadata = metadata;
        this.maxLevel = maxLevel;
        this.neighbors = new HashMap<>();
        for (int i = 0; i <= maxLevel; i++) {
            neighbors.put(i, new ArrayList<>());
        }
    }

    /** @return the node identifier */
    public String getId() { return id; }

    /** @return the vector (defensive copy) */
    public float[] getVector() { return vector.clone(); }

    /** @return the raw vector array (no copy) */
    float[] getVectorRaw() { return vector; }

    /** @return the metadata */
    public T getMetadata() { return metadata; }

    /** @return the maximum layer level */
    public int getMaxLevel() { return maxLevel; }

    /**
     * Returns neighbors at a specific layer.
     * @param level the layer index
     * @return unmodifiable list of neighbor IDs
     */
    public List<String> getNeighbors(int level) {
        List<String> layerNeighbors = neighbors.get(level);
        if (layerNeighbors == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(layerNeighbors);
    }

    /**
     * Adds a neighbor at the given level, pruning if exceeds maxConnections.
     */
    public void addNeighbor(int level, String neighborId, int maxConnections) {
        List<String> layerNeighbors = neighbors.computeIfAbsent(
                level, k -> new ArrayList<>());

        if (!layerNeighbors.contains(neighborId)) {
            layerNeighbors.add(neighborId);
            if (layerNeighbors.size() > maxConnections) {
                // Simple pruning: remove oldest
                layerNeighbors.remove(0);
            }
        }
    }

    /**
     * Returns the number of neighbors across all layers.
     * @return total neighbor count
     */
    public int getTotalNeighborCount() {
        int count = 0;
        for (List<String> n : neighbors.values()) {
            count += n.size();
        }
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HnswNode)) return false;
        HnswNode<?> that = (HnswNode<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "HnswNode{id='" + id + "', level=" + maxLevel
                + ", dims=" + vector.length + "}";
    }
}
