package com.nousresearch.aikit.vector.similarity;

/**
 * Computes cosine similarity between two vectors.
 *
 * <p>Cosine similarity measures the cosine of the angle between two vectors,
 * ranging from -1 (opposite) to 1 (identical direction). This is the most
 * common similarity metric for text embeddings.</p>
 *
 * <p>Formula: cos(θ) = (A·B) / (||A|| × ||B||)</p>
 */
public class CosineSimilarity implements SimilarityFunction {

    @Override
    public float compute(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vector dimensions must match: " + a.length + " vs " + b.length);
        }

        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0.0f || normB == 0.0f) {
            return 0.0f;
        }

        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public String getName() { return "cosine"; }
}
