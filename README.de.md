<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**AI-Entwicklungskit für Java — LLM-Client, Vektor-Store, Embeddings, Prompt-Verwaltung**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Englisch](#englisch) | [Chinesisch](#chinesisch)

---

## Englisch

**AiKit Java** ist ein umfassendes KI-Entwicklungstoolkit für Java 17+. Es bietet alles, was Sie zum Erstellen KI-gestützter Anwendungen benötigen — von LLM-Chat-Clients und Vektorsuche bis hin zu Prompt-Templates und Agent-Frameworks.

### 🚀 Funktionen

- **Multi-Provider-LLM-Client** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini mit Streaming (SSE), Connection Pooling und Wiederholungsversuchen mit exponentiellem Backoff
- **Vektor-Store** — In-Memory-HNSW-Index mit Kosinus-/Euklid-/Skalarprodukt-Ähnlichkeit und Metadaten-Filterung
- **Embedding-Generierung** — Multi-Provider-Embeddings mit Batch-Verarbeitung und Caffeine-basiertem Caching
- **Prompt-Verwaltung** — Jinja2-ähnliche Template-Engine mit Variablen, Bedingungen, Schleifen und Versionskontrolle
- **Agent-Framework** — ReAct-Agent (Reasoning + Acting) mit Tool-Registry und Tool-Chain-Ausführung
- **Spring-Boot-Integration** — Automatische Konfiguration über `@ConfigurationProperties`

### 📦 Module

| Modul | Beschreibung | Wichtige Klassen |
|--------|-------------|-------------|
| `aikit-core` | Kern-Interfaces und -Modelle | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | LLM-Clients mit HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Vektor-Speicherung und -Suche | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Embedding-Generierung | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Prompt-Templates | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Agent-Framework | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring-Boot-Autokonfiguration | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Voraussetzungen

- Java 17 oder neuer
- Gradle 8.x (Wrapper enthalten)

### 🔧 Schnellstart

**1. Repository klonen**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Projekt bauen**

```bash
./gradlew build
```

**3. Als Abhängigkeit hinzufügen**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Anwendungsbeispiele

#### LLM-Chat

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

#### Vektorsuche

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

#### Prompt-Templating

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

#### Agent mit Tools

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

### 🧪 Tests ausführen

```bash
./gradlew test
```

### 📚 Dokumentation

- Javadoc: `./gradlew javadoc`
- Die vollständige API-Dokumentation ist im generierten Javadoc verfügbar

### 🤝 Mitwirken

Siehe [CONTRIBUTING.md](CONTRIBUTING.md) für Richtlinien zu Code-Stil, PRs und dem Entwicklungsworkflow.

### 📄 Lizenz

MIT-Lizenz — Details siehe [LICENSE](LICENSE).

---

## Chinesisch

**AiKit Java** ist ein umfassendes KI-Entwicklungstoolkit für Java 17+, das alles bietet, was Sie benötigen — von LLM-Chat-Clients und Vektorsuche bis hin zu Prompt-Templates und Agent-Frameworks.

### 🚀 Funktionen

- **Multi-Provider-LLM-Client** — Unterstützung für OpenAI, Anthropic Claude, DeepSeek, Google Gemini, einschließlich Streaming-Antworten (SSE), Connection Pooling und Wiederholungsversuchen mit exponentiellem Backoff
- **Vektor-Store** — In-Memory-Vektor-Index auf HNSW-Basis, Unterstützung für Kosinus-/Euklid-/Skalarprodukt-Ähnlichkeit und Metadaten-Filterung
- **Embedding-Generierung** — Multi-Provider-Embeddings, Batch-Verarbeitung, Caffeine-basiertes Caching
- **Prompt-Verwaltung** — Jinja2-ähnliche Template-Engine mit Unterstützung für Variablenersetzung, Bedingungen, Schleifen und Versionsverwaltung
- **Agent-Framework** — ReAct-Agent (Reasoning + Acting) mit Tool-Registrierungssystem und Tool-Chain-Ausführung
- **Spring-Boot-Integration** — Automatische Konfiguration über `@ConfigurationProperties`

### 📦 Module

| Modul | Beschreibung | Wichtige Klassen |
|--------|-------------|-------------|
| `aikit-core` | Kern-Interfaces und -Modelle | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM-Clients | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Vektor-Speicherung und -Suche | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Embedding-Generierung | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Prompt-Templates | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Agent-Framework | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring-Boot-Autokonfiguration | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Voraussetzungen

- Java 17 oder neuer
- Gradle 8.x (Wrapper enthalten)

### 🔧 Schnellstart

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Lizenz

MIT-Lizenz — Details siehe [LICENSE](LICENSE).
