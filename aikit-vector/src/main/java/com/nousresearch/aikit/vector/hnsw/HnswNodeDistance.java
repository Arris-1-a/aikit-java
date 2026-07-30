package com.nousresearch.aikit.vector.hnsw;

/**
 * Wrapper pairing an HNSW node with its computed distance/similarity score.
 *
 * <p>Used internally during search to track candidates. Higher distance
 * values indicate greater similarity to the query vector.</p>
 *
 * @param <T> metadata type
 */
class HnswNodeDistance<T> {

    final HnswNode<T> node;
    final float distance;

    /**
     * Creates a new node-distance pair.
     * @param node the HNSW node
     * @param distance the similarity score (higher = more similar)
     */
    HnswNodeDistance(HnswNode<T> node, float distance) {
        this.node = node;
        this.distance = distance;
    }

    /** @return the similarity/distance score */
    float getDistance() { return distance; }

    @Override
    public String toString() {
        return "HnswNodeDist{id='" + node.getId() + "', dist=" + distance + "}";
    }
}
