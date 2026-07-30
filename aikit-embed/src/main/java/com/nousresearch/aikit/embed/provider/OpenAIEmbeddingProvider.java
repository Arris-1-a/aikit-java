package com.nousresearch.aikit.embed.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nousresearch.aikit.core.exception.AiKitException;
import com.nousresearch.aikit.core.model.EmbeddingRequest;
import com.nousresearch.aikit.core.model.EmbeddingResponse;
import com.nousresearch.aikit.embed.AbstractEmbeddingProvider;
import com.nousresearch.aikit.embed.cache.EmbeddingCache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;

/**
 * OpenAI embedding provider.
 *
 * <p>Supports text-embedding-3-small (1536 dims), text-embedding-3-large
 * (3072 dims), and text-embedding-ada-002 (1536 dims).</p>
 */
public class OpenAIEmbeddingProvider extends AbstractEmbeddingProvider {

    private static final String DEFAULT_BASE_URL = "https://api.openai.com/v1";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Creates a new OpenAI embedding provider.
     */
    public OpenAIEmbeddingProvider(String apiKey, String model, int dimension,
                                   EmbeddingCache cache, OkHttpClient httpClient, String baseUrl) {
        super(apiKey, model, dimension, cache);
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_BASE_URL;
    }

    @Override
    public EmbeddingResponse createEmbeddings(EmbeddingRequest request) {
        try {
            String jsonBody = objectMapper.writeValueAsString(request);

            Request httpRequest = new Request.Builder()
                    .url(baseUrl + "/embeddings")
                    .post(RequestBody.create(jsonBody, JSON))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String body = response.body() != null ? response.body().string() : "";
                if (response.code() == 200) {
                    return objectMapper.readValue(body, EmbeddingResponse.class);
                }
                throw new AiKitException("Embedding API error (HTTP " + response.code() + "): " + body,
                        response.code());
            }
        } catch (IOException e) {
            throw new AiKitException("Failed to create embeddings", e);
        }
    }

    /**
     * Creates a builder.
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Builder for OpenAIEmbeddingProvider.
     */
    public static class Builder {
        private String apiKey;
        private String model = "text-embedding-3-small";
        private int dimension = 1536;
        private EmbeddingCache cache;
        private OkHttpClient httpClient;
        private String baseUrl;

        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder dimension(int dimension) { this.dimension = dimension; return this; }
        public Builder cache(EmbeddingCache cache) { this.cache = cache; return this; }
        public Builder httpClient(OkHttpClient client) { this.httpClient = client; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }

        public OpenAIEmbeddingProvider build() {
            if (httpClient == null) {
                httpClient = new OkHttpClient.Builder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .readTimeout(Duration.ofSeconds(60))
                        .build();
            }
            if (cache == null) {
                cache = new EmbeddingCache();
            }
            return new OpenAIEmbeddingProvider(apiKey, model, dimension, cache, httpClient, baseUrl);
        }
    }
}
