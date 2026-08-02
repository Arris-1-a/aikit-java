<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java کے لیے AI ڈیولپمنٹ کٹ — LLM کلائنٹ، ویکٹر اسٹور، ایمبیڈنگز، پرامپٹ مینجمنٹ**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[انگریزی](#انگریزی) | [چینی](#چینی)

---

## انگریزی

**AiKit Java** Java 17+ کے لیے ایک جامع AI ڈیولپمنٹ ٹول کٹ ہے۔ یہ AI سے چلنے والی ایپلیکیشنز بنانے کے لیے درکار ہر چیز فراہم کرتا ہے — LLM چیٹ کلائنٹس اور ویکٹر سرچ سے لے کر پرامپٹ ٹیمپلیٹنگ اور ایجنٹ فریم ورکس تک۔

### 🚀 خصوصیات

- **ملٹی-پرووائیڈر LLM کلائنٹ** — OpenAI، Anthropic Claude، DeepSeek، Google Gemini، اسٹریمنگ (SSE)، کنکشن پولنگ اور ایکسپونینشل بیک آف ریٹرائی کے ساتھ
- **ویکٹر اسٹور** — کوزائن/یوکلیڈین/ڈاٹ-پروڈکٹ سمیلیرٹی اور میٹاڈیٹا فلٹرنگ کے ساتھ ان-میموری HNSW انڈیکس
- **ایمبیڈنگ جنریشن** — بیچ پروسیسنگ اور Caffeine پر مبنی کیشنگ کے ساتھ ملٹی-پرووائیڈر ایمبیڈنگز
- **پرامپٹ مینجمنٹ** — متغیرات، کنڈیشنلز، لوپس اور ورژن کنٹرول والا Jinja2 جیسا ٹیمپلیٹ انجن
- **ایجنٹ فریم ورک** — ٹول رجسٹری اور ٹول چین ایگزیکیوشن کے ساتھ ReAct (ریزوننگ + ایکٹنگ) ایجنٹ
- **Spring Boot انٹیگریشن** — `@ConfigurationProperties` کے ذریعے آٹو-کنفیگریشن

### 📦 ماڈیولز

| ماڈیول | تفصیل | اہم کلاسیں |
|--------|-------------|-------------|
| `aikit-core` | بنیادی انٹرفیس اور ماڈلز | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE کے ساتھ LLM کلائنٹس | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | ویکٹر اسٹوریج اور سرچ | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | ایمبیڈنگ جنریشن | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | پرامپٹ ٹیمپلیٹس | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | ایجنٹ فریم ورک | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot آٹوکنفیگ | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 تقاضے

- Java 17 یا اس سے نیا
- Gradle 8.x (wrapper شامل ہے)

### 🔧 فوری آغاز

**1. ریپوزٹری کلون کریں**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. پروجیکٹ بنائیں**

```bash
./gradlew build
```

**3. ڈیپنڈنسی کے طور پر شامل کریں**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 استعمال کی مثالیں

#### LLM چیٹ

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

#### ویکٹر سرچ

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

#### پرامپٹ ٹیمپلیٹنگ

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

#### ٹولز کے ساتھ ایجنٹ

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

### 🧪 ٹیسٹ چلانا

```bash
./gradlew test
```

### 📚 دستاویزات

- Javadoc: `./gradlew javadoc`
- مکمل API دستاویزات تیار شدہ Javadoc میں دستیاب ہیں

### 🤝 تعاون

کوڈ اسٹائل، PR اور ڈیولپمنٹ ورک فلو کے رہنما اصولوں کے لیے [CONTRIBUTING.md](CONTRIBUTING.md) دیکھیں۔

### 📄 لائسنس

MIT لائسنس — تفصیلات کے لیے [LICENSE](LICENSE) دیکھیں۔

---

## چینی

**AiKit Java** Java 17+ کے لیے ایک جامع AI ڈیولپمنٹ ٹول کٹ ہے، جو LLM چیٹ کلائنٹس اور ویکٹر سرچ سے لے کر پرامپٹ ٹیمپلیٹس اور ایجنٹ فریم ورکس تک ہر چیز فراہم کرتی ہے۔

### 🚀 خصوصیات

- **ملٹی-پرووائیڈر LLM کلائنٹ** — OpenAI، Anthropic Claude، DeepSeek، Google Gemini کی سپورٹ، اسٹریمنگ رسپانس (SSE)، کنکشن پول اور ایکسپونینشل بیک آف ریٹرائی کے ساتھ
- **ویکٹر اسٹور** — HNSW پر مبنی ان-میموری ویکٹر انڈیکس، کوزائن/یوکلیڈین/ڈاٹ-پروڈکٹ سمیلیرٹی اور میٹاڈیٹا فلٹرنگ کی سپورٹ
- **ایمبیڈنگ جنریشن** — ملٹی-پرووائیڈر ایمبیڈنگز، بیچ پروسیسنگ، Caffeine پر مبنی کیشنگ
- **پرامپٹ مینجمنٹ** — Jinja2 جیسا ٹیمپلیٹ انجن، متغیرات کی تبدیلی، کنڈیشنلز، لوپس اور ورژن مینجمنٹ کی سپورٹ
- **ایجنٹ فریم ورک** — ReAct (ریزوننگ + ایکٹنگ) ایجنٹ، ٹول رجسٹریشن سسٹم اور ٹول چین ایگزیکیوشن کے ساتھ
- **Spring Boot انٹیگریشن** — `@ConfigurationProperties` کے ذریعے آٹو-کنفیگریشن

### 📦 ماڈیولز

| ماڈیول | تفصیل | اہم کلاسیں |
|--------|-------------|-------------|
| `aikit-core` | بنیادی انٹرفیس اور ماڈلز | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM کلائنٹس | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | ویکٹر اسٹوریج اور سرچ | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | ایمبیڈنگ جنریشن | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | پرامپٹ ٹیمپلیٹس | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | ایجنٹ فریم ورک | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot آٹو-کنفیگریشن | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 تقاضے

- Java 17 یا اس سے نیا
- Gradle 8.x (wrapper شامل ہے)

### 🔧 فوری آغاز

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 لائسنس

MIT لائسنس — تفصیلات کے لیے [LICENSE](LICENSE) دیکھیں۔
