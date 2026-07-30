package com.nousresearch.aikit.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Represents an embedding response from an embedding provider.
 *
 * <p>Contains the generated embedding vectors, one per input,
 * along with model and usage metadata.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddingResponse {

    private final String object;
    private final String model;
    private final List<EmbeddingData> data;
    private final ChatResponse.Usage usage;

    public EmbeddingResponse(@JsonProperty("object") String object,
                             @JsonProperty("model") String model,
                             @JsonProperty("data") List<EmbeddingData> data,
                             @JsonProperty("usage") ChatResponse.Usage usage) {
        this.object = object;
        this.model = model;
        this.data = data != null ? Collections.unmodifiableList(data) : Collections.emptyList();
        this.usage = usage;
    }

    /** @return object type (e.g., "list") */
    public String getObject() { return object; }

    /** @return the embedding model name */
    public String getModel() { return model; }

    /** @return the embedding data entries */
    public List<EmbeddingData> getData() { return data; }

    /** @return token usage statistics */
    public ChatResponse.Usage getUsage() { return usage; }

    /**
     * Returns the first embedding vector.
     * @return float array of the first embedding, or null
     */
    public float[] getFirstEmbedding() {
        if (data.isEmpty() || data.get(0).getEmbedding() == null) {
            return null;
        }
        List<Float> embedding = data.get(0).getEmbedding();
        float[] result = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            result[i] = embedding.get(i);
        }
        return result;
    }

    /**
     * Returns all embeddings as a float matrix.
     * @return 2D float array [input_index][dimension]
     */
    public float[][] getAllEmbeddings() {
        float[][] result = new float[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            List<Float> emb = data.get(i).getEmbedding();
            if (emb == null) {
                result[i] = new float[0];
                continue;
            }
            result[i] = new float[emb.size()];
            for (int j = 0; j < emb.size(); j++) {
                result[i][j] = emb.get(j);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "EmbeddingResponse{model='" + model + "', count=" + data.size() + "}";
    }

    /**
     * A single embedding data entry.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EmbeddingData {
        private final int index;
        private final String object;
        private final List<Float> embedding;

        public EmbeddingData(@JsonProperty("index") int index,
                             @JsonProperty("object") String object,
                             @JsonProperty("embedding") List<Float> embedding) {
            this.index = index;
            this.object = object;
            this.embedding = embedding != null ? Collections.unmodifiableList(embedding) : Collections.emptyList();
        }

        /** @return the index of this embedding in the batch */
        public int getIndex() { return index; }

        /** @return object type ("embedding") */
        public String getObject() { return object; }

        /** @return the embedding vector */
        public List<Float> getEmbedding() { return embedding; }

        @Override
        public String toString() {
            return "EmbeddingData{index=" + index + ", dims=" + embedding.size() + "}";
        }
    }
}
