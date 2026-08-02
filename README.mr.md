<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java साठी AI डेव्हलपमेंट किट — LLM क्लायंट, व्हेक्टर स्टोअर, एम्बेडिंग, प्रॉम्प्ट व्यवस्थापन**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[इंग्रजी](#इंग्रजी) | [चिनी](#चिनी)

---

## इंग्रजी

**AiKit Java** हे Java 17+ साठी एक सर्वसमावेशक AI डेव्हलपमेंट टूलकिट आहे. LLM चॅट क्लायंट आणि व्हेक्टर सर्चपासून प्रॉम्प्ट टेम्पलेटिंग आणि एजंट फ्रेमवर्कपर्यंत — AI-आधारित अॅप्लिकेशन्स तयार करण्यासाठी आवश्यक असलेली प्रत्येक गोष्ट ते प्रदान करते.

### 🚀 वैशिष्ट्ये

- **मल्टी-प्रोव्हायडर LLM क्लायंट** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini, स्ट्रीमिंग (SSE), कनेक्शन पूलिंग आणि एक्सपोनेन्शियल बॅकऑफ रिट्रायसह
- **व्हेक्टर स्टोअर** — कोसाइन/युक्लिडियन/डॉट-प्रॉडक्ट समानता आणि मेटाडेटा फिल्टरिंगसह इन-मेमरी HNSW इंडेक्स
- **एम्बेडिंग जनरेशन** — बॅच प्रोसेसिंग आणि Caffeine-आधारित कॅशिंगसह मल्टी-प्रोव्हायडर एम्बेडिंग
- **प्रॉम्प्ट व्यवस्थापन** — व्हेरिएबल्स, कंडिशनल्स, लूप्स आणि व्हर्जन कंट्रोलसह Jinja2-सारखे टेम्पलेट इंजिन
- **एजंट फ्रेमवर्क** — टूल रेजिस्ट्री आणि टूल चेन एक्झिक्युशनसह ReAct (रीझनिंग + अॅक्टिंग) एजंट
- **Spring Boot इंटिग्रेशन** — `@ConfigurationProperties` द्वारे ऑटो-कॉन्फिगरेशन

### 📦 मॉड्यूल्स

| मॉड्यूल | वर्णन | प्रमुख क्लासेस |
|--------|-------------|-------------|
| `aikit-core` | मुख्य इंटरफेस आणि मॉडेल्स | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE सह LLM क्लायंट | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | व्हेक्टर स्टोरेज आणि सर्च | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | एम्बेडिंग जनरेशन | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | प्रॉम्प्ट टेम्पलेट्स | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | एजंट फ्रेमवर्क | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ऑटोकॉन्फिग | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 आवश्यकता

- Java 17 किंवा त्याहून नवीन
- Gradle 8.x (wrapper समाविष्ट)

### 🔧 द्रुत प्रारंभ

**1. रिपॉझिटरी क्लोन करा**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. प्रोजेक्ट तयार करा**

```bash
./gradlew build
```

**3. डिपेंडन्सी म्हणून जोडा**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 वापराची उदाहरणे

#### LLM चॅट

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

#### व्हेक्टर सर्च

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

#### प्रॉम्प्ट टेम्पलेटिंग

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

#### टूल्ससह एजंट

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

### 🧪 चाचण्या चालवणे

```bash
./gradlew test
```

### 📚 दस्तऐवजीकरण

- Javadoc: `./gradlew javadoc`
- संपूर्ण API दस्तऐवज निर्माण झालेल्या Javadoc मध्ये उपलब्ध आहे

### 🤝 योगदान

कोड शैली, PR आणि डेव्हलपमेंट वर्कफ्लोच्या मार्गदर्शक तत्त्वांसाठी [CONTRIBUTING.md](CONTRIBUTING.md) पहा.

### 📄 परवाना

MIT परवाना — तपशीलांसाठी [LICENSE](LICENSE) पहा.

---

## चिनी

**AiKit Java** हे Java 17+ साठी एक सर्वसमावेशक AI डेव्हलपमेंट टूलकिट आहे, जे LLM चॅट क्लायंट आणि व्हेक्टर सर्चपासून प्रॉम्प्ट टेम्पलेट्स आणि एजंट फ्रेमवर्कपर्यंत सर्वकाही प्रदान करते.

### 🚀 वैशिष्ट्ये

- **मल्टी-प्रोव्हायडर LLM क्लायंट** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini चे समर्थन, स्ट्रीमिंग प्रतिसाद (SSE), कनेक्शन पूल आणि एक्सपोनेन्शियल बॅकऑफ रिट्रायसह
- **व्हेक्टर स्टोअर** — HNSW-आधारित इन-मेमरी व्हेक्टर इंडेक्स, कोसाइन/युक्लिडियन/डॉट-प्रॉडक्ट समानता आणि मेटाडेटा फिल्टरिंगचे समर्थन
- **एम्बेडिंग जनरेशन** — मल्टी-प्रोव्हायडर एम्बेडिंग, बॅच प्रोसेसिंग, Caffeine-आधारित कॅशिंग
- **प्रॉम्प्ट व्यवस्थापन** — Jinja2-सारखे टेम्पलेट इंजिन, व्हेरिएबल प्रतिस्थापन, कंडिशनल्स, लूप्स आणि व्हर्जन व्यवस्थापनाचे समर्थन
- **एजंट फ्रेमवर्क** — ReAct (रीझनिंग + अॅक्टिंग) एजंट, टूल रेजिस्ट्रेशन सिस्टम आणि टूल चेन एक्झिक्युशनसह
- **Spring Boot इंटिग्रेशन** — `@ConfigurationProperties` द्वारे ऑटो-कॉन्फिगरेशन

### 📦 मॉड्यूल्स

| मॉड्यूल | वर्णन | प्रमुख क्लासेस |
|--------|-------------|-------------|
| `aikit-core` | मुख्य इंटरफेस आणि मॉडेल्स | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM क्लायंट | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | व्हेक्टर स्टोरेज आणि सर्च | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | एम्बेडिंग जनरेशन | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | प्रॉम्प्ट टेम्पलेट्स | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | एजंट फ्रेमवर्क | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ऑटो-कॉन्फिगरेशन | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 आवश्यकता

- Java 17 किंवा त्याहून नवीन
- Gradle 8.x (wrapper समाविष्ट)

### 🔧 द्रुत प्रारंभ

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 परवाना

MIT परवाना — तपशीलांसाठी [LICENSE](LICENSE) पहा.
