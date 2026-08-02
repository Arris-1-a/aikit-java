<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java के लिए AI डेवलपमेंट किट — LLM क्लाइंट, वेक्टर स्टोर, एम्बेडिंग, प्रॉम्प्ट प्रबंधन**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[अंग्रेज़ी](#अंग्रेज़ी) | [चीनी](#चीनी)

---

## अंग्रेज़ी

**AiKit Java** Java 17+ के लिए एक व्यापक AI डेवलपमेंट टूलकिट है। यह AI-संचालित एप्लिकेशन बनाने के लिए आवश्यक सब कुछ प्रदान करता है — LLM चैट क्लाइंट और वेक्टर सर्च से लेकर प्रॉम्प्ट टेम्प्लेटिंग और एजेंट फ्रेमवर्क तक।

### 🚀 विशेषताएँ

- **मल्टी-प्रोवाइडर LLM क्लाइंट** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini, स्ट्रीमिंग (SSE), कनेक्शन पूलिंग और एक्सपोनेंशियल बैकऑफ़ रिट्राई के साथ
- **वेक्टर स्टोर** — कोसाइन/यूक्लिडियन/डॉट-प्रोडक्ट समानता और मेटाडेटा फ़िल्टरिंग के साथ इन-मेमोरी HNSW इंडेक्स
- **एम्बेडिंग जनरेशन** — बैच प्रोसेसिंग और Caffeine-आधारित कैशिंग के साथ मल्टी-प्रोवाइडर एम्बेडिंग
- **प्रॉम्प्ट प्रबंधन** — वेरिएबल्स, कंडीशनल्स, लूप्स और वर्जन कंट्रोल वाला Jinja2-जैसा टेम्प्लेट इंजन
- **एजेंट फ्रेमवर्क** — टूल रजिस्ट्री और टूल चेन निष्पादन के साथ ReAct (रीज़निंग + एक्टिंग) एजेंट
- **Spring Boot एकीकरण** — `@ConfigurationProperties` के माध्यम से ऑटो-कॉन्फ़िगरेशन

### 📦 मॉड्यूल

| मॉड्यूल | विवरण | मुख्य क्लासेस |
|--------|-------------|-------------|
| `aikit-core` | मुख्य इंटरफेस और मॉडल | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE के साथ LLM क्लाइंट | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | वेक्टर स्टोरेज और सर्च | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | एम्बेडिंग जनरेशन | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | प्रॉम्प्ट टेम्पलेट | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | एजेंट फ्रेमवर्क | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ऑटोकॉन्फ़िग | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 आवश्यकताएँ

- Java 17 या उसके बाद का संस्करण
- Gradle 8.x (wrapper शामिल है)

### 🔧 त्वरित शुरुआत

**1. रिपॉज़िटरी क्लोन करें**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. प्रोजेक्ट बनाएँ**

```bash
./gradlew build
```

**3. डिपेंडेंसी के रूप में जोड़ें**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 उपयोग उदाहरण

#### LLM चैट

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

#### वेक्टर सर्च

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

#### प्रॉम्प्ट टेम्प्लेटिंग

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

#### टूल्स के साथ एजेंट

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

### 🧪 टेस्ट चलाना

```bash
./gradlew test
```

### 📚 दस्तावेज़ीकरण

- Javadoc: `./gradlew javadoc`
- पूर्ण API दस्तावेज़ जनरेट हुए Javadoc में उपलब्ध हैं

### 🤝 योगदान

कोड शैली, PR और डेवलपमेंट वर्कफ़्लो के दिशानिर्देशों के लिए [CONTRIBUTING.md](CONTRIBUTING.md) देखें।

### 📄 लाइसेंस

MIT लाइसेंस — विवरण के लिए [LICENSE](LICENSE) देखें।

---

## चीनी

**AiKit Java** Java 17+ के लिए एक व्यापक AI डेवलपमेंट टूलकिट है, जो LLM चैट क्लाइंट, वेक्टर सर्च से लेकर प्रॉम्प्ट टेम्पलेट और एजेंट फ्रेमवर्क तक की पूरी सुविधाएँ प्रदान करता है।

### 🚀 विशेषताएँ

- **मल्टी-प्रोवाइडर LLM क्लाइंट** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini का समर्थन, स्ट्रीमिंग प्रतिक्रियाओं (SSE), कनेक्शन पूल और एक्सपोनेंशियल बैकऑफ़ रिट्राई सहित
- **वेक्टर स्टोर** — HNSW-आधारित इन-मेमोरी वेक्टर इंडेक्स, कोसाइन/यूक्लिडियन/डॉट-प्रोडक्ट समानता और मेटाडेटा फ़िल्टरिंग का समर्थन
- **एम्बेडिंग जनरेशन** — मल्टी-प्रोवाइडर एम्बेडिंग, बैच प्रोसेसिंग, Caffeine-आधारित कैशिंग
- **प्रॉम्प्ट प्रबंधन** — Jinja2-जैसा टेम्प्लेट इंजन, वेरिएबल प्रतिस्थापन, कंडीशनल, लूप और वर्जन प्रबंधन का समर्थन
- **एजेंट फ्रेमवर्क** — ReAct (रीज़निंग + एक्टिंग) एजेंट, टूल रजिस्ट्रेशन सिस्टम और टूल चेन निष्पादन सहित
- **Spring Boot एकीकरण** — `@ConfigurationProperties` के माध्यम से ऑटो-कॉन्फ़िगरेशन

### 📦 मॉड्यूल

| मॉड्यूल | विवरण | मुख्य क्लासेस |
|--------|-------------|-------------|
| `aikit-core` | मुख्य इंटरफेस और मॉडल | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM क्लाइंट | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | वेक्टर स्टोरेज और सर्च | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | एम्बेडिंग जनरेशन | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | प्रॉम्प्ट टेम्पलेट | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | एजेंट फ्रेमवर्क | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ऑटो-कॉन्फ़िगरेशन | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 आवश्यकताएँ

- Java 17 या उसके बाद का संस्करण
- Gradle 8.x (wrapper शामिल है)

### 🔧 त्वरित शुरुआत

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 लाइसेंस

MIT लाइसेंस — विवरण के लिए [LICENSE](LICENSE) देखें।
