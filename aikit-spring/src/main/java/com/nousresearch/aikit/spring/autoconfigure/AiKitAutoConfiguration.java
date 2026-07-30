package com.nousresearch.aikit.spring.autoconfigure;

import com.nousresearch.aikit.agent.react.ReActAgent;
import com.nousresearch.aikit.agent.tool.ToolRegistry;
import com.nousresearch.aikit.core.LLMProvider;
import com.nousresearch.aikit.embed.cache.EmbeddingCache;
import com.nousresearch.aikit.llm.LLMFactory;
import com.nousresearch.aikit.llm.config.LLMConfig;
import com.nousresearch.aikit.prompt.PromptManager;
import com.nousresearch.aikit.spring.properties.AiKitProperties;
import com.nousresearch.aikit.vector.InMemoryVectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

/**
 * Spring Boot auto-configuration for AiKit.
 *
 * <p>Automatically creates and wires AiKit beans based on
 * application properties. Beans are only created when the
 * corresponding properties are configured.</p>
 *
 * <p>Enabled by default; set {@code aikit.enabled=false} to disable.</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(AiKitProperties.class)
@ConditionalOnProperty(prefix = "aikit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiKitAutoConfiguration {

    private final AiKitProperties properties;

    /**
     * Creates the auto-configuration.
     * @param properties the AiKit properties
     */
    public AiKitAutoConfiguration(AiKitProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates the LLM provider bean if an API key is configured.
     *
     * @return the configured LLM provider
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aikit.llm", name = "api-key")
    public LLMProvider llmProvider() {
        AiKitProperties.LlmProperties llm = properties.getLlm();
        LLMConfig config = LLMConfig.builder()
                .provider(llm.getProvider())
                .apiKey(llm.getApiKey())
                .model(llm.getModel())
                .baseUrl(llm.getBaseUrl())
                .timeout(llm.getTimeout())
                .maxRetries(llm.getMaxRetries())
                .maxConnections(llm.getMaxConnections())
                .build();
        return LLMFactory.create(config);
    }

    /**
     * Creates the prompt manager bean.
     *
     * @return a PromptManager instance
     */
    @Bean
    @ConditionalOnMissingBean
    public PromptManager promptManager() {
        return new PromptManager();
    }

    /**
     * Creates the tool registry bean.
     *
     * @return a ToolRegistry instance
     */
    @Bean
    @ConditionalOnMissingBean
    public ToolRegistry toolRegistry() {
        return new ToolRegistry();
    }

    /**
     * Creates the ReAct agent bean if an LLM provider is available.
     *
     * @param llmProvider the LLM provider
     * @param toolRegistry the tool registry
     * @return a ReActAgent instance
     */
    @Bean
    @ConditionalOnMissingBean
    public ReActAgent reactAgent(LLMProvider llmProvider, ToolRegistry toolRegistry) {
        AiKitProperties.AgentProperties agent = properties.getAgent();
        ReActAgent.Builder builder = ReActAgent.builder()
                .llmProvider(llmProvider)
                .toolRegistry(toolRegistry)
                .maxIterations(agent.getMaxIterations())
                .temperature(agent.getTemperature());
        if (agent.getSystemPrompt() != null) {
            builder.systemPrompt(agent.getSystemPrompt());
        }
        return builder.build();
    }

    /**
     * Creates the embedding cache bean.
     *
     * @return an EmbeddingCache instance
     */
    @Bean
    @ConditionalOnMissingBean
    public EmbeddingCache embeddingCache() {
        AiKitProperties.EmbedProperties embed = properties.getEmbed();
        return new EmbeddingCache(embed.getCacheSize(), embed.getCacheTtl());
    }

    /**
     * Creates a default in-memory vector store.
     *
     * @return an InMemoryVectorStore with String metadata
     */
    @Bean
    @ConditionalOnMissingBean
    public InMemoryVectorStore<String> vectorStore() {
        AiKitProperties.VectorProperties vector = properties.getVector();
        return InMemoryVectorStore.<String>builder()
                .dimension(vector.getDimension())
                .m(vector.getM())
                .efConstruction(vector.getEfConstruction())
                .build();
    }
}
