package com.nousresearch.aikit.vector.similarity;

/**
 * Computes dot product similarity between two vectors.
 *
 * <p>Dot product is the sum of element-wise multiplications. Works best
 * with normalized vectors where it is equivalent to cosine similarity.</p>
 *
 * <p>Formula: A·B = Σ(Ai × Bi)</p>
 */
public class DotProductSimilarity implements SimilarityFunction {

    @Override
    public float compute(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vector dimensions must match: " + a.length + " vs " + b.length);
        }

        float dotProduct = 0.0f;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
        }

        return dotProduct;
    }

    @Override
    public String getName() { return "dot_product"; }
}
