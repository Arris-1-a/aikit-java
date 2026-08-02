<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**面向 Java 的 AI 开发工具包 — LLM 客户端、向量存储、嵌入、提示词管理**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[英语](#英语) | [中文](#中文)

---

## 英语

**AiKit Java** 是一个面向 Java 17+ 的综合 AI 开发工具包，提供了构建 AI 应用所需的一切——从 LLM 聊天客户端、向量搜索到提示词模板和 Agent 框架。

### 🚀 功能特性

- **多提供商 LLM 客户端** — 支持 OpenAI、Anthropic Claude、DeepSeek、Google Gemini，含流式响应（SSE）、连接池和指数退避重试
- **向量存储** — 基于 HNSW 的内存向量索引，支持余弦/欧几里得/点积相似度和元数据过滤
- **嵌入生成** — 多提供商嵌入，批量处理，基于 Caffeine 的缓存
- **提示词管理** — 类 Jinja2 模板引擎，支持变量替换、条件判断、循环和版本管理
- **Agent 框架** — ReAct（推理+行动）Agent，含工具注册系统和工具链执行
- **Spring Boot 集成** — 通过 `@ConfigurationProperties` 自动配置

### 📦 模块说明

| 模块 | 描述 | 核心类 |
|--------|-------------|-------------|
| `aikit-core` | 核心接口和模型 | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | 基于 HTTP/SSE 的 LLM 客户端 | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | 向量存储与搜索 | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | 嵌入生成 | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | 提示词模板 | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Agent 框架 | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot 自动配置 | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 环境要求

- Java 17 或更高版本
- Gradle 8.x（已包含 wrapper）

### 🔧 快速开始

**1. 克隆仓库**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. 构建项目**

```bash
./gradlew build
```

**3. 添加依赖**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 使用示例

#### LLM 聊天

```java
import com.nousresearch.aikit.llm.client.OpenAIClient;
import com.nousresearch.aikit.core.model.ChatRequest;
import com.nousresearch.aikit.core.model.ChatMessage;

var client = OpenAIClient.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .model("gpt-4o")
    .build();

String reply = client.chat("You are a helpful assistant.", "What is Java?");
System.out.println(reply);
```

#### 向量搜索

```java
import com.nousresearch.aikit.vector.InMemoryVectorStore;
import com.nousresearch.aikit.core.VectorStore;

var store = InMemoryVectorStore.<String>builder()
    .dimension(1536)
    .build();

store.add("doc1", embedding1, "Document about Java");
store.add("doc2", embedding2, "Document about Python");

var results = store.search(queryEmbedding, 5);
results.forEach(r -> System.out.println(r.getId() + ": " + r.getScore()));
```

#### 提示词模板

```java
import com.nousresearch.aikit.prompt.PromptManager;
import java.util.Map;

var manager = new PromptManager();
manager.register("greeting", "Hello {{ name }}! Welcome to {{ product }}.", "admin");

String result = manager.render("greeting", Map.of(
    "name", "Alice",
    "product", "AiKit"
));
// → "Hello Alice! Welcome to AiKit."
```

#### 带工具的 Agent

```java
import com.nousresearch.aikit.agent.react.ReActAgent;
import com.nousresearch.aikit.agent.tool.Tool;
import com.nousresearch.aikit.agent.tool.ToolRegistry;
import java.util.Map;

var registry = new ToolRegistry();
registry.register(Tool.builder()
    .name("calculator")
    .description("Performs arithmetic")
    .parameters(Map.of("type", "object",
        "properties", Map.of("expression", Map.of("type", "string"))))
    .executor(args -> String.valueOf(eval(args.get("expression").toString())))
    .build());

var agent = ReActAgent.builder()
    .llmProvider(openaiClient)
    .toolRegistry(registry)
    .maxIterations(5)
    .build();

var result = agent.run("What is 123 * 456?");
System.out.println(result.getAnswer());
```

#### Spring Boot

```yaml
# application.yml
aikit:
  llm:
    provider: openai
    api-key: ${OPENAI_API_KEY}
    model: gpt-4o
  vector:
    dimension: 1536
```

```java
@RestController
public class ChatController {
    private final LLMProvider llm;

    public ChatController(LLMProvider llm) { this.llm = llm; }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return llm.chat(null, message);
    }
}
```

### 🧪 运行测试

```bash
./gradlew test
```

### 📚 文档

- Javadoc：`./gradlew javadoc`
- 完整 API 文档见生成的 Javadoc

### 🤝 贡献

有关代码风格、PR 和开发流程的指南，请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。

### 📄 许可证

MIT License — 详见 [LICENSE](LICENSE)。

---

## 中文

**AiKit Java** 是一个面向 Java 17+ 的综合 AI 开发工具包，提供了从 LLM 聊天客户端、向量搜索到提示词模板和 Agent 框架的全套功能。

### 🚀 功能特性

- **多提供商 LLM 客户端** — 支持 OpenAI、Anthropic Claude、DeepSeek、Google Gemini，含流式响应（SSE）、连接池和指数退避重试
- **向量存储** — 基于 HNSW 的内存向量索引，支持余弦/欧几里得/点积相似度和元数据过滤
- **嵌入生成** — 多提供商嵌入，批量处理，基于 Caffeine 的缓存
- **提示词管理** — 类 Jinja2 模板引擎，支持变量替换、条件判断、循环和版本管理
- **Agent 框架** — ReAct（推理+行动）Agent，含工具注册系统和工具链执行
- **Spring Boot 集成** — 通过 `@ConfigurationProperties` 自动配置

### 📦 模块说明

| 模块 | 描述 | 核心类 |
|--------|-------------|-------------|
| `aikit-core` | 核心接口和模型 | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM 客户端 | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | 向量存储与搜索 | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | 嵌入生成 | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | 提示词模板 | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Agent 框架 | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot 自动配置 | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 环境要求

- Java 17 或更高版本
- Gradle 8.x（已包含 wrapper）

### 🔧 快速开始

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 许可证

MIT License — 详见 [LICENSE](LICENSE)
