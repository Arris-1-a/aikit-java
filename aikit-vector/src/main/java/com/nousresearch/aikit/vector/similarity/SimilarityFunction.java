package com.nousresearch.aikit.vector.similarity;

/**
 * Computes similarity or distance between two float vectors.
 *
 * <p>Implementations provide different metric spaces for vector comparison.
 * Higher return values indicate greater similarity (for distance metrics,
 * the negative distance is returned).</p>
 */
public interface SimilarityFunction {

    /**
     * Computes the similarity between two vectors.
     *
     * @param a the first vector
     * @param b the second vector
     * @return similarity score (higher = more similar)
     * @throws IllegalArgumentException if vectors have different lengths
     */
    float compute(float[] a, float[] b);

    /**
     * Returns the name of this similarity function.
     * @return a human-readable name
     */
    String getName();
}
