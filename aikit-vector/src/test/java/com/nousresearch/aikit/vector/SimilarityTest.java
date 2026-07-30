package com.nousresearch.aikit.vector;

import com.nousresearch.aikit.vector.similarity.CosineSimilarity;
import com.nousresearch.aikit.vector.similarity.DotProductSimilarity;
import com.nousresearch.aikit.vector.similarity.EuclideanDistance;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class SimilarityTest {

    @Test
    void cosineShouldBeOneForIdentical() {
        CosineSimilarity cs = new CosineSimilarity();
        float[] v = {1.0f, 2.0f, 3.0f};
        assertThat(cs.compute(v, v)).isCloseTo(1.0f, within(0.001f));
    }

    @Test
    void cosineShouldBeZeroForOrthogonal() {
        CosineSimilarity cs = new CosineSimilarity();
        float[] a = {1.0f, 0.0f, 0.0f};
        float[] b = {0.0f, 1.0f, 0.0f};
        assertThat(cs.compute(a, b)).isCloseTo(0.0f, within(0.001f));
    }

    @Test
    void cosineShouldBeNegativeOneForOpposite() {
        CosineSimilarity cs = new CosineSimilarity();
        float[] a = {1.0f, 0.0f};
        float[] b = {-1.0f, 0.0f};
        assertThat(cs.compute(a, b)).isCloseTo(-1.0f, within(0.001f));
    }

    @Test
    void euclideanShouldBeZeroForIdentical() {
        EuclideanDistance ed = new EuclideanDistance();
        float[] v = {1.0f, 2.0f, 3.0f};
        assertThat(ed.compute(v, v)).isCloseTo(0.0f, within(0.001f));
    }

    @Test
    void euclideanShouldBeNegativeForDifferent() {
        EuclideanDistance ed = new EuclideanDistance();
        float[] a = {0.0f, 0.0f};
        float[] b = {3.0f, 4.0f};
        assertThat(ed.compute(a, b)).isCloseTo(-5.0f, within(0.001f));
    }

    @Test
    void dotProductShouldWork() {
        DotProductSimilarity dp = new DotProductSimilarity();
        float[] a = {1.0f, 2.0f, 3.0f};
        float[] b = {4.0f, 5.0f, 6.0f};
        assertThat(dp.compute(a, b)).isCloseTo(32.0f, within(0.001f));
    }

    @Test
    void allSimilarityShouldRejectDimensionMismatch() {
        CosineSimilarity cs = new CosineSimilarity();
        try {
            cs.compute(new float[]{1, 2}, new float[]{1, 2, 3});
            assertThat(false).as("Expected exception").isTrue();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("dimension");
        }
    }
}
