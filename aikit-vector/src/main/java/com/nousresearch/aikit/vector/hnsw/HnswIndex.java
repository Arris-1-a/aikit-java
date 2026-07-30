package com.nousresearch.aikit.vector.hnsw;

import com.nousresearch.aikit.vector.similarity.CosineSimilarity;
import com.nousresearch.aikit.vector.similarity.SimilarityFunction;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hierarchical Navigable Small World (HNSW) index for approximate nearest
 * neighbor search.
 *
 * <p>HNSW is a graph-based algorithm that builds a multi-layer structure
 * for efficient k-nearest-neighbor queries. This implementation follows
 * the original paper by Malkov & Yashunin (2016).</p>
 *
 * <p>Key parameters:</p>
 * <ul>
 *   <li><b>M</b> — maximum number of connections per node (default 16)</li>
 *   <li><b>efConstruction</b> — beam width during index building (default 200)</li>
 *   <li><b>efSearch</b> — beam width during search (default 100)</li>
 *   <li><b>mL</b> — normalization factor for level generation (default 1/ln(M))</li>
 * </ul>
 *
 * @param <T> the type of metadata associated with each vector
 */
public class HnswIndex<T> {

    private static final int DEFAULT_M = 16;
    private static final int DEFAULT_EF_CONSTRUCTION = 200;
    private static final int DEFAULT_EF_SEARCH = 100;

    private final int dimension;
    private final int m;
    private final int mMax;
    private final int mMax0;
    private final int efConstruction;
    private final double mL;
    private final SimilarityFunction similarityFunction;

    private final Map<String, HnswNode<T>> nodes;
    private String entryPointId;
    private int maxLevel;

    /**
     * Creates an HNSW index with default parameters (M=16, cosine similarity).
     *
     * @param dimension the dimensionality of vectors
     */
    public HnswIndex(int dimension) {
        this(dimension, DEFAULT_M, DEFAULT_EF_CONSTRUCTION, new CosineSimilarity());
    }

    /**
     * Creates an HNSW index with custom parameters.
     *
     * @param dimension the dimensionality of vectors
     * @param m maximum connections per node (4–64 recommended)
     * @param efConstruction beam width during construction
     * @param similarityFunction the similarity metric
     */
    public HnswIndex(int dimension, int m, int efConstruction, SimilarityFunction similarityFunction) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension must be positive");
        }
        if (m < 2) {
            throw new IllegalArgumentException("M must be at least 2");
        }
        this.dimension = dimension;
        this.m = m;
        this.mMax = m;
        this.mMax0 = m * 2;
        this.efConstruction = Math.max(efConstruction, m);
        this.mL = 1.0 / Math.log(m);
        this.similarityFunction = similarityFunction;
        this.nodes = new HashMap<>();
        this.entryPointId = null;
        this.maxLevel = -1;
    }

    /**
     * Inserts a vector into the index.
     *
     * @param id the unique identifier
     * @param vector the vector
     * @param metadata associated metadata
     */
    public void insert(String id, float[] vector, T metadata) {
        if (vector.length != dimension) {
            throw new IllegalArgumentException("Vector dimension " + vector.length
                    + " does not match index dimension " + dimension);
        }

        int level = randomLevel();
        HnswNode<T> newNode = new HnswNode<>(id, vector, metadata, level);

        synchronized (this) {
            nodes.put(id, newNode);

            if (entryPointId == null) {
                entryPointId = id;
                maxLevel = level;
                return;
            }

            HnswNode<T> currNode = nodes.get(entryPointId);
            String currId = entryPointId;

            // Search from top level down to level+1
            for (int lc = maxLevel; lc > level; lc--) {
                List<HnswNodeDistance<T>> candidates = searchLayer(vector, currId, 1, lc);
                if (!candidates.isEmpty()) {
                    currId = candidates.get(0).node.getId();
                }
            }

            // Insert at levels from min(level, maxLevel) down to 0
            for (int lc = Math.min(level, maxLevel); lc >= 0; lc--) {
                List<HnswNodeDistance<T>> candidates =
                        searchLayer(vector, currId, efConstruction, lc);
                List<HnswNodeDistance<T>> selectedNeighbors =
                        selectNeighbors(candidates, mMax);

                for (HnswNodeDistance<T> neighbor : selectedNeighbors) {
                    connectNodes(newNode, neighbor.node, lc);
                }
            }

            if (level > maxLevel) {
                entryPointId = id;
                maxLevel = level;
            }
        }
    }

    /**
     * Searches for k nearest neighbors using default ef.
     *
     * @param query the query vector
     * @param k number of results
     * @return sorted list of (id, score) pairs, highest score first
     */
    public List<Map.Entry<String, Float>> search(float[] query, int k) {
        return searchInternal(query, k, DEFAULT_EF_SEARCH);
    }

    /**
     * Searches with explicit ef parameter for recall/speed trade-off.
     *
     * @param query the query vector
     * @param k number of results
     * @param ef beam width during search
     * @return sorted list of (id, score) pairs
     */
    public List<Map.Entry<String, Float>> search(float[] query, int k, int ef) {
        return searchInternal(query, k, ef);
    }

    private List<Map.Entry<String, Float>> searchInternal(float[] query, int k, int ef) {
        if (entryPointId == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        String currId = entryPointId;

        // Traverse from top level to level 1
        for (int lc = maxLevel; lc > 0; lc--) {
            List<HnswNodeDistance<T>> candidates = searchLayer(query, currId, 1, lc);
            if (!candidates.isEmpty()) {
                currId = candidates.get(0).node.getId();
            }
        }

        // Search at level 0 with full ef
        List<HnswNodeDistance<T>> candidates =
                searchLayer(query, currId, Math.max(ef, k), 0);

        List<Map.Entry<String, Float>> results = new ArrayList<>();
        for (int i = 0; i < Math.min(k, candidates.size()); i++) {
            HnswNodeDistance<T> c = candidates.get(i);
            results.add(new AbstractMap.SimpleEntry<>(c.node.getId(), c.distance));
        }
        return results;
    }

    /**
     * Searches at a single layer, returning up to ef closest nodes.
     */
    private List<HnswNodeDistance<T>> searchLayer(
            float[] query, String entryId, int ef, int level) {
        Set<String> visited = new HashSet<>();
        PriorityQueue<HnswNodeDistance<T>> candidates = new PriorityQueue<>(
                Comparator.comparingDouble(HnswNodeDistance::getDistance).reversed());
        PriorityQueue<HnswNodeDistance<T>> results = new PriorityQueue<>(
                Comparator.comparingDouble(HnswNodeDistance::getDistance));

        HnswNode<T> entryNode = nodes.get(entryId);
        if (entryNode == null) {
            return Collections.emptyList();
        }

        float entryDist = similarityFunction.compute(query, entryNode.getVector());
        HnswNodeDistance<T> entryDistNode = new HnswNodeDistance<>(entryNode, entryDist);

        candidates.add(entryDistNode);
        results.add(entryDistNode);
        visited.add(entryId);

        while (!candidates.isEmpty()) {
            HnswNodeDistance<T> current = candidates.poll();
            HnswNodeDistance<T> worst = results.peek();

            if (current.distance < worst.distance) {
                break;
            }

            List<String> neighbors = current.node.getNeighbors(level);
            if (neighbors == null) {
                continue;
            }

            for (String neighborId : neighbors) {
                if (!visited.add(neighborId)) {
                    continue;
                }

                HnswNode<T> neighbor = nodes.get(neighborId);
                if (neighbor == null) {
                    continue;
                }

                float dist = similarityFunction.compute(query, neighbor.getVector());
                HnswNodeDistance<T> neighborDist =
                        new HnswNodeDistance<>(neighbor, dist);

                if (results.size() < ef || dist > results.peek().distance) {
                    candidates.add(neighborDist);
                    results.add(neighborDist);
                    if (results.size() > ef) {
                        results.poll();
                    }
                }
            }
        }

        List<HnswNodeDistance<T>> sortedResults = new ArrayList<>(results);
        sortedResults.sort(
                Comparator.comparingDouble(HnswNodeDistance::getDistance).reversed());
        return sortedResults;
    }

    /**
     * Selects at most maxM neighbors from candidates.
     */
    private List<HnswNodeDistance<T>> selectNeighbors(
            List<HnswNodeDistance<T>> candidates, int maxM) {
        if (candidates.size() <= maxM) {
            return new ArrayList<>(candidates);
        }

        List<HnswNodeDistance<T>> selected = new ArrayList<>();
        PriorityQueue<HnswNodeDistance<T>> remaining = new PriorityQueue<>(
                Comparator.comparingDouble(HnswNodeDistance::getDistance));

        remaining.addAll(candidates);

        while (!remaining.isEmpty() && selected.size() < maxM) {
            selected.add(remaining.poll());
        }

        return selected;
    }

    /**
     * Creates bidirectional connections between two nodes at a given level.
     */
    private void connectNodes(HnswNode<T> a, HnswNode<T> b, int level) {
        a.addNeighbor(level, b.getId(), mMax);
        b.addNeighbor(level, a.getId(), mMax);
    }

    /**
     * Generates a random level following exponential decay.
     */
    private int randomLevel() {
        double r = -Math.log(ThreadLocalRandom.current().nextDouble()) * mL;
        return (int) r;
    }

    /** @return the node for the given ID, or null */
    public HnswNode<T> getNode(String id) {
        return nodes.get(id);
    }

    /**
     * Removes a node from the index.
     * @param id the node ID to remove
     * @return true if the node was found and removed
     */
    public boolean remove(String id) {
        synchronized (this) {
            HnswNode<T> node = nodes.remove(id);
            if (node != null && id.equals(entryPointId)) {
                entryPointId = nodes.isEmpty() ? null : nodes.keySet().iterator().next();
                maxLevel = entryPointId != null
                        ? nodes.get(entryPointId).getMaxLevel() : -1;
            }
            return node != null;
        }
    }

    /** @return total number of vectors indexed */
    public int size() {
        return nodes.size();
    }

    /** @return the vector dimension */
    public int getDimension() {
        return dimension;
    }

    /** @return unmodifiable set of all node IDs */
    public Set<String> getNodeIds() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    /** Clears all nodes from the index. */
    public void clear() {
        synchronized (this) {
            nodes.clear();
            entryPointId = null;
            maxLevel = -1;
        }
    }

    /** @return similarity function name */
    public String getSimilarityName() {
        return similarityFunction.getName();
    }

    /** @return the maximum level in the hierarchy */
    public int getMaxLevel() {
        return maxLevel;
    }

    /** @return the entry point node ID */
    public String getEntryPointId() {
        return entryPointId;
    }
}
