package com.nousresearch.aikit.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a request for text embeddings from an embedding provider.
 *
 * <p>Supports both single-string and batch-mode embedding generation,
 * along with model selection and encoding format options.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddingRequest {

    private final String model;
    private final List<String> input;
    private final String encodingFormat;
    private final String user;

    /**
     * Creates an embedding request for a single input.
     * @param model the embedding model name
     * @param input the text to embed
     */
    public EmbeddingRequest(String model, String input) {
        this(model, Collections.singletonList(input), "float", null);
    }

    /**
     * Creates an embedding request for multiple inputs.
     * @param model the embedding model name
     * @param input list of texts to embed
     */
    public EmbeddingRequest(String model, List<String> input) {
        this(model, input, "float", null);
    }

    public EmbeddingRequest(@JsonProperty("model") String model,
                            @JsonProperty("input") List<String> input,
                            @JsonProperty("encoding_format") String encodingFormat,
                            @JsonProperty("user") String user) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.input = Collections.unmodifiableList(
                Objects.requireNonNull(input, "input must not be null"));
        this.encodingFormat = encodingFormat != null ? encodingFormat : "float";
        this.user = user;
    }

    /** @return the embedding model name */
    public String getModel() { return model; }

    /** @return the input texts */
    public List<String> getInput() { return input; }

    /** @return encoding format ("float" or "base64") */
    public String getEncodingFormat() { return encodingFormat; }

    /** @return end-user identifier */
    public String getUser() { return user; }

    /**
     * Creates a request from an array of strings.
     * @param model the model name
     * @param inputs one or more input texts
     * @return a new EmbeddingRequest
     */
    public static EmbeddingRequest of(String model, String... inputs) {
        return new EmbeddingRequest(model, Arrays.asList(inputs));
    }

    @Override
    public String toString() {
        return "EmbeddingRequest{model='" + model + "', inputs=" + input.size() + "}";
    }
}
