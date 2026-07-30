package com.nousresearch.aikit.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot configuration properties for AiKit.
 *
 * <p>Binds to the {@code aikit.*} namespace in application.yml/properties.</p>
 *
 * <p>Example application.yml:</p>
 * <pre>{@code
 * aikit:
 *   llm:
 *     provider: openai
 *     api-key: ${OPENAI_API_KEY}
 *     model: gpt-4o
 *   vector:
 *     dimension: 1536
 * }</pre>
 */
@ConfigurationProperties(prefix = "aikit")
public class AiKitProperties {

    private boolean enabled = true;

    @NestedConfigurationProperty
    private LlmProperties llm = new LlmProperties();

    @NestedConfigurationProperty
    private VectorProperties vector = new VectorProperties();

    @NestedConfigurationProperty
    private EmbedProperties embed = new EmbedProperties();

    @NestedConfigurationProperty
    private AgentProperties agent = new AgentProperties();

    private final Map<String, ProviderProperties> providers = new HashMap<>();

    /** @return whether AiKit auto-configuration is enabled */
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LlmProperties getLlm() { return llm; }
    public void setLlm(LlmProperties llm) { this.llm = llm; }

    public VectorProperties getVector() { return vector; }
    public void setVector(VectorProperties vector) { this.vector = vector; }

    public EmbedProperties getEmbed() { return embed; }
    public void setEmbed(EmbedProperties embed) { this.embed = embed; }

    public AgentProperties getAgent() { return agent; }
    public void setAgent(AgentProperties agent) { this.agent = agent; }

    public Map<String, ProviderProperties> getProviders() { return providers; }

    /**
     * LLM configuration properties.
     */
    public static class LlmProperties {
        private String provider = "openai";
        private String apiKey;
        private String model;
        private String baseUrl;
        private Duration timeout = Duration.ofSeconds(60);
        private int maxRetries = 3;
        private int maxConnections = 20;

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

        public Duration getTimeout() { return timeout; }
        public void setTimeout(Duration timeout) { this.timeout = timeout; }

        public int getMaxRetries() { return maxRetries; }
        public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
    }

    /**
     * Vector store configuration properties.
     */
    public static class VectorProperties {
        private int dimension = 1536;
        private int m = 16;
        private int efConstruction = 200;
        private String similarity = "cosine";
        private String persistencePath;

        public int getDimension() { return dimension; }
        public void setDimension(int dimension) { this.dimension = dimension; }

        public int getM() { return m; }
        public void setM(int m) { this.m = m; }

        public int getEfConstruction() { return efConstruction; }
        public void setEfConstruction(int efConstruction) { this.efConstruction = efConstruction; }

        public String getSimilarity() { return similarity; }
        public void setSimilarity(String similarity) { this.similarity = similarity; }

        public String getPersistencePath() { return persistencePath; }
        public void setPersistencePath(String persistencePath) { this.persistencePath = persistencePath; }
    }

    /**
     * Embedding configuration properties.
     */
    public static class EmbedProperties {
        private String provider = "openai";
        private String apiKey;
        private String model = "text-embedding-3-small";
        private int dimension = 1536;
        private int cacheSize = 1000;
        private Duration cacheTtl = Duration.ofHours(1);

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public int getDimension() { return dimension; }
        public void setDimension(int dimension) { this.dimension = dimension; }

        public int getCacheSize() { return cacheSize; }
        public void setCacheSize(int cacheSize) { this.cacheSize = cacheSize; }

        public Duration getCacheTtl() { return cacheTtl; }
        public void setCacheTtl(Duration cacheTtl) { this.cacheTtl = cacheTtl; }
    }

    /**
     * Agent configuration properties.
     */
    public static class AgentProperties {
        private int maxIterations = 10;
        private String systemPrompt;
        private double temperature = 0.7;

        public int getMaxIterations() { return maxIterations; }
        public void setMaxIterations(int maxIterations) { this.maxIterations = maxIterations; }

        public String getSystemPrompt() { return systemPrompt; }
        public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
    }

    /**
     * Per-provider configuration.
     */
    public static class ProviderProperties {
        private String apiKey;
        private String baseUrl;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }
}
