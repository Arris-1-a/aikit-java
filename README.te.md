<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java కోసం AI డెవలప్మెంట్ కిట్ — LLM క్లయింట్, వెక్టర్ స్టోర్, ఎంబెడ్డింగ్లు, ప్రాంప్ట్ మేనేజ్మెంట్**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[ఇంగ్లీష్](#ఇంగ్లీష్) | [చైనీస్](#చైనీస్)

---

## ఇంగ్లీష్

**AiKit Java** అనేది Java 17+ కోసం ఒక సమగ్ర AI డెవలప్మెంట్ టూల్కిట్. LLM చాట్ క్లయింట్లు మరియు వెక్టర్ సెర్చ్ నుండి ప్రాంప్ట్ టెంప్లేటింగ్ మరియు ఏజెంట్ ఫ్రేమ్వర్క్ల వరకు — AI-ఆధారిత అప్లికేషన్లను నిర్మించడానికి అవసరమైన ప్రతిదీ ఇది అందిస్తుంది.

### 🚀 ఫీచర్లు

- **మల్టీ-ప్రొవైడర్ LLM క్లయింట్** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini స్ట్రీమింగ్ (SSE), కనెక్షన్ పూలింగ్ మరియు ఎక్స్పోనెన్షియల్ బ్యాక్ఆఫ్ రీట్రైతో
- **వెక్టర్ స్టోర్** — కొసైన్/యూక్లిడియన్/డాట్-ప్రొడక్ట్ సిమిలారిటీ మరియు మెటాడేటా ఫిల్టరింగ్తో ఇన్-మెమరీ HNSW ఇండెక్స్
- **ఎంబెడ్డింగ్ జనరేషన్** — బ్యాచ్ ప్రాసెసింగ్ మరియు Caffeine-ఆధారిత కాషింగ్తో మల్టీ-ప్రొవైడర్ ఎంబెడ్డింగ్లు
- **ప్రాంప్ట్ మేనేజ్మెంట్** — వేరియబుల్స్, కండిషనల్స్, లూప్స్ మరియు వెర్షన్ కంట్రోల్తో Jinja2 లాంటి టెంప్లేట్ ఇంజిన్
- **ఏజెంట్ ఫ్రేమ్వర్క్** — టూల్ రిజిస్ట్రీ మరియు టూల్ చైన్ ఎగ్జిక్యూషన్తో ReAct (రీజనింగ్ + యాక్టింగ్) ఏజెంట్
- **Spring Boot ఇంటిగ్రేషన్** — `@ConfigurationProperties` ద్వారా ఆటో-కాన్ఫిగరేషన్

### 📦 మాడ్యూల్స్

| మాడ్యూల్ | వివరణ | కీ క్లాసులు |
|--------|-------------|-------------|
| `aikit-core` | కోర్ ఇంటర్ఫేస్లు మరియు మోడల్స్ | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSEతో LLM క్లయింట్లు | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | వెక్టర్ స్టోరేజ్ మరియు సెర్చ్ | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | ఎంబెడ్డింగ్ జనరేషన్ | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | ప్రాంప్ట్ టెంప్లేట్లు | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | ఏజెంట్ ఫ్రేమ్వర్క్ | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ఆటోకాన్ఫిగ్ | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 అవసరాలు

- Java 17 లేదా అంతకంటే కొత్తది
- Gradle 8.x (wrapper చేర్చబడింది)

### 🔧 శీఘ్ర ప్రారంభం

**1. రిపోజిటరీని క్లోన్ చేయండి**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. ప్రాజెక్ట్ను బిల్డ్ చేయండి**

```bash
./gradlew build
```

**3. డిపెండెన్సీగా జోడించండి**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 వినియోగ ఉదాహరణలు

#### LLM చాట్

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

#### వెక్టర్ సెర్చ్

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

#### ప్రాంప్ట్ టెంప్లేటింగ్

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

#### టూల్స్తో ఏజెంట్

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

### 🧪 టెస్ట్లను రన్ చేయడం

```bash
./gradlew test
```

### 📚 డాక్యుమెంటేషన్

- Javadoc: `./gradlew javadoc`
- పూర్తి API డాక్యుమెంటేషన్ జనరేట్ చేయబడిన Javadocలో అందుబాటులో ఉంది

### 🤝 సహకారం

కోడ్ శైలి, PRలు మరియు డెవలప్మెంట్ వర్క్ఫ్లోకు సంబంధించిన మార్గదర్శకాల కోసం [CONTRIBUTING.md](CONTRIBUTING.md) చూడండి.

### 📄 లైసెన్స్

MIT లైసెన్స్ — వివరాల కోసం [LICENSE](LICENSE) చూడండి.

---

## చైనీస్

**AiKit Java** అనేది Java 17+ కోసం ఒక సమగ్ర AI డెవలప్మెంట్ టూల్కిట్, ఇది LLM చాట్ క్లయింట్లు మరియు వెక్టర్ సెర్చ్ నుండి ప్రాంప్ట్ టెంప్లేట్లు మరియు ఏజెంట్ ఫ్రేమ్వర్క్ల వరకు ప్రతిదీ అందిస్తుంది.

### 🚀 ఫీచర్లు

- **మల్టీ-ప్రొవైడర్ LLM క్లయింట్** — OpenAI, Anthropic Claude, DeepSeek, Google Geminiకి మద్దతు, స్ట్రీమింగ్ రెస్పాన్స్లు (SSE), కనెక్షన్ పూల్ మరియు ఎక్స్పోనెన్షియల్ బ్యాక్ఆఫ్ రీట్రైతో
- **వెక్టర్ స్టోర్** — HNSW-ఆధారిత ఇన్-మెమరీ వెక్టర్ ఇండెక్స్, కొసైన్/యూక్లిడియన్/డాట్-ప్రొడక్ట్ సిమిలారిటీ మరియు మెటాడేటా ఫిల్టరింగ్కు మద్దతు
- **ఎంబెడ్డింగ్ జనరేషన్** — మల్టీ-ప్రొవైడర్ ఎంబెడ్డింగ్లు, బ్యాచ్ ప్రాసెసింగ్, Caffeine-ఆధారిత కాషింగ్
- **ప్రాంప్ట్ మేనేజ్మెంట్** — Jinja2 లాంటి టెంప్లేట్ ఇంజిన్, వేరియబుల్ ప్రత్యామ్నాయం, కండిషనల్స్, లూప్స్ మరియు వెర్షన్ మేనేజ్మెంట్కు మద్దతు
- **ఏజెంట్ ఫ్రేమ్వర్క్** — ReAct (రీజనింగ్ + యాక్టింగ్) ఏజెంట్, టూల్ రిజిస్ట్రేషన్ సిస్టమ్ మరియు టూల్ చైన్ ఎగ్జిక్యూషన్తో
- **Spring Boot ఇంటిగ్రేషన్** — `@ConfigurationProperties` ద్వారా ఆటో-కాన్ఫిగరేషన్

### 📦 మాడ్యూల్స్

| మాడ్యూల్ | వివరణ | కీ క్లాసులు |
|--------|-------------|-------------|
| `aikit-core` | కోర్ ఇంటర్ఫేస్లు మరియు మోడల్స్ | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM క్లయింట్లు | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | వెక్టర్ స్టోరేజ్ మరియు సెర్చ్ | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | ఎంబెడ్డింగ్ జనరేషన్ | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | ప్రాంప్ట్ టెంప్లేట్లు | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | ఏజెంట్ ఫ్రేమ్వర్క్ | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ఆటో-కాన్ఫిగరేషన్ | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 అవసరాలు

- Java 17 లేదా అంతకంటే కొత్తది
- Gradle 8.x (wrapper చేర్చబడింది)

### 🔧 శీఘ్ర ప్రారంభం

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 లైసెన్స్

MIT లైసెన్స్ — వివరాల కోసం [LICENSE](LICENSE) చూడండి.
