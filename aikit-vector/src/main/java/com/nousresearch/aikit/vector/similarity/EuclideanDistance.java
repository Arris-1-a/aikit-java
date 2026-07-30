package com.nousresearch.aikit.vector.similarity;

/**
 * Computes Euclidean (L2) distance between two vectors, negated so that
 * higher scores mean closer vectors.
 *
 * <p>Formula: score = -sqrt(Σ(Ai - Bi)²)</p>
 */
public class EuclideanDistance implements SimilarityFunction {

    @Override
    public float compute(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vector dimensions must match: " + a.length + " vs " + b.length);
        }

        float sumSquaredDiff = 0.0f;
        for (int i = 0; i < a.length; i++) {
            float diff = a[i] - b[i];
            sumSquaredDiff += diff * diff;
        }

        // Negate so higher scores = more similar
        return -(float) Math.sqrt(sumSquaredDiff);
    }

    @Override
    public String getName() { return "euclidean"; }
}
