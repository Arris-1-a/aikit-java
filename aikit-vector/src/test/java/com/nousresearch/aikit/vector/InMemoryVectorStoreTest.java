package com.nousresearch.aikit.vector;

import com.nousresearch.aikit.core.VectorStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class InMemoryVectorStoreTest {

    private InMemoryVectorStore<String> store;

    @BeforeEach
    void setUp() {
        store = InMemoryVectorStore.<String>builder()
                .dimension(4)
                .m(4)
                .efConstruction(20)
                .build();
    }

    @Test
    void shouldAddAndRetrieveVector() {
        store.add("doc1", new float[]{1.0f, 0.0f, 0.0f, 0.0f}, "metadata1");
        assertThat(store.size()).isEqualTo(1);

        VectorStore.VectorEntry<String> entry = store.get("doc1");
        assertThat(entry).isNotNull();
        assertThat(entry.getId()).isEqualTo("doc1");
        assertThat(entry.getMetadata()).isEqualTo("metadata1");
    }

    @Test
    void shouldSearchNearestNeighbors() {
        store.add("a", new float[]{1.0f, 0.0f, 0.0f, 0.0f}, "A");
        store.add("b", new float[]{0.9f, 0.0f, 0.0f, 0.0f}, "B");
        store.add("c", new float[]{0.0f, 0.0f, 1.0f, 0.0f}, "C");

        List<VectorStore.VectorSearchResult<String>> results =
                store.search(new float[]{1.0f, 0.0f, 0.0f, 0.0f}, 2);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo("a");
        assertThat(results.get(0).getScore()).isGreaterThan(0.9f);
    }

    @Test
    void shouldDeleteVector() {
        store.add("doc1", new float[]{1f, 0, 0, 0}, "meta");
        assertThat(store.size()).isEqualTo(1);

        boolean deleted = store.delete("doc1");
        assertThat(deleted).isTrue();
        assertThat(store.size()).isEqualTo(0);
    }

    @Test
    void shouldClearStore() {
        store.add("a", new float[]{1, 0, 0, 0}, "A");
        store.add("b", new float[]{0, 1, 0, 0}, "B");
        store.clear();
        assertThat(store.size()).isEqualTo(0);
    }

    @Test
    void shouldAddBatch() {
        List<VectorStore.VectorEntry<String>> entries = List.of(
                new VectorStore.VectorEntry<>("a", new float[]{1, 0, 0, 0}, "A"),
                new VectorStore.VectorEntry<>("b", new float[]{0, 1, 0, 0}, "B")
        );
        store.addAll(entries);
        assertThat(store.size()).isEqualTo(2);
    }
}
