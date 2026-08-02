<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java-এর জন্য AI ডেভেলপমেন্ট কিট — LLM ক্লায়েন্ট, ভেক্টর স্টোর, এমবেডিং, প্রম্পট ম্যানেজমেন্ট**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[ইংরেজি](#ইংরেজি) | [চীনা](#চীনা)

---

## ইংরেজি

**AiKit Java** হলো Java 17+-এর জন্য একটি ব্যাপক AI ডেভেলপমেন্ট টুলকিট। এটি AI-চালিত অ্যাপ্লিকেশন তৈরির জন্য প্রয়োজনীয় সবকিছু প্রদান করে — LLM চ্যাট ক্লায়েন্ট এবং ভেক্টর সার্চ থেকে শুরু করে প্রম্পট টেমপ্লেটিং এবং এজেন্ট ফ্রেমওয়ার্ক পর্যন্ত।

### 🚀 বৈশিষ্ট্য

- **মাল্টি-প্রোভাইডার LLM ক্লায়েন্ট** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini, স্ট্রিমিং (SSE), কানেকশন পুলিং এবং এক্সপোনেনশিয়াল ব্যাকঅফ রিট্রাই সহ
- **ভেক্টর স্টোর** — কোসাইন/ইউক্লিডিয়ান/ডট-প্রোডাক্ট সিমিলারিটি এবং মেটাডেটা ফিল্টারিং সহ ইন-মেমোরি HNSW ইনডেক্স
- **এমবেডিং জেনারেশন** — ব্যাচ প্রসেসিং এবং Caffeine-ভিত্তিক ক্যাশিং সহ মাল্টি-প্রোভাইডার এমবেডিং
- **প্রম্পট ম্যানেজমেন্ট** — ভেরিয়েবল, কন্ডিশনাল, লুপ এবং ভার্সন কন্ট্রোলসহ Jinja2-এর মতো টেমপ্লেট ইঞ্জিন
- **এজেন্ট ফ্রেমওয়ার্ক** — টুল রেজিস্ট্রি এবং টুল চেইন এক্সিকিউশনসহ ReAct (রিজনিং + অ্যাক্টিং) এজেন্ট
- **Spring Boot ইন্টিগ্রেশন** — `@ConfigurationProperties` এর মাধ্যমে অটো-কনফিগারেশন

### 📦 মডিউল

| মডিউল | বিবরণ | মূল ক্লাস |
|--------|-------------|-------------|
| `aikit-core` | মূল ইন্টারফেস এবং মডেল | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE সহ LLM ক্লায়েন্ট | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | ভেক্টর স্টোরেজ এবং সার্চ | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | এমবেডিং জেনারেশন | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | প্রম্পট টেমপ্লেট | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | এজেন্ট ফ্রেমওয়ার্ক | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot অটোকনফিগ | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 প্রয়োজনীয়তা

- Java 17 বা তার পরবর্তী সংস্করণ
- Gradle 8.x (wrapper অন্তর্ভুক্ত)

### 🔧 দ্রুত শুরু

**1. রিপোজিটরি ক্লোন করুন**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. প্রজেক্ট বিল্ড করুন**

```bash
./gradlew build
```

**3. ডিপেন্ডেন্সি হিসেবে যোগ করুন**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 ব্যবহারের উদাহরণ

#### LLM চ্যাট

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

#### ভেক্টর সার্চ

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

#### প্রম্পট টেমপ্লেটিং

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

#### টুলসহ এজেন্ট

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

### 🧪 টেস্ট চালানো

```bash
./gradlew test
```

### 📚 ডকুমেন্টেশন

- Javadoc: `./gradlew javadoc`
- সম্পূর্ণ API ডকুমেন্টেশন জেনারেট হওয়া Javadoc-এ উপলব্ধ

### 🤝 কন্ট্রিবিউশন

কোড স্টাইল, PR এবং ডেভেলপমেন্ট ওয়ার্কফ্লো সম্পর্কিত নির্দেশিকার জন্য [CONTRIBUTING.md](CONTRIBUTING.md) দেখুন।

### 📄 লাইসেন্স

MIT লাইসেন্স — বিস্তারিত জানতে [LICENSE](LICENSE) দেখুন।

---

## চীনা

**AiKit Java** হলো Java 17+-এর জন্য একটি ব্যাপক AI ডেভেলপমেন্ট টুলকিট, যা LLM চ্যাট ক্লায়েন্ট, ভেক্টর সার্চ থেকে প্রম্পট টেমপ্লেট এবং এজেন্ট ফ্রেমওয়ার্ক পর্যন্ত সবকিছু প্রদান করে।

### 🚀 বৈশিষ্ট্য

- **মাল্টি-প্রোভাইডার LLM ক্লায়েন্ট** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini সমর্থন করে, স্ট্রিমিং রেসপন্স (SSE), কানেকশন পুল এবং এক্সপোনেনশিয়াল ব্যাকঅফ রিট্রাই সহ
- **ভেক্টর স্টোর** — HNSW-ভিত্তিক ইন-মেমোরি ভেক্টর ইনডেক্স, কোসাইন/ইউক্লিডিয়ান/ডট-প্রোডাক্ট সিমিলারিটি এবং মেটাডেটা ফিল্টারিং সমর্থন করে
- **এমবেডিং জেনারেশন** — মাল্টি-প্রোভাইডার এমবেডিং, ব্যাচ প্রসেসিং, Caffeine-ভিত্তিক ক্যাশিং
- **প্রম্পট ম্যানেজমেন্ট** — Jinja2-এর মতো টেমপ্লেট ইঞ্জিন, ভেরিয়েবল প্রতিস্থাপন, কন্ডিশনাল, লুপ এবং ভার্সন ম্যানেজমেন্ট সমর্থন করে
- **এজেন্ট ফ্রেমওয়ার্ক** — ReAct (রিজনিং + অ্যাক্টিং) এজেন্ট, টুল রেজিস্ট্রেশন সিস্টেম এবং টুল চেইন এক্সিকিউশন সহ
- **Spring Boot ইন্টিগ্রেশন** — `@ConfigurationProperties` এর মাধ্যমে অটো-কনফিগারেশন

### 📦 মডিউল

| মডিউল | বিবরণ | মূল ক্লাস |
|--------|-------------|-------------|
| `aikit-core` | মূল ইন্টারফেস এবং মডেল | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM ক্লায়েন্ট | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | ভেক্টর স্টোরেজ এবং সার্চ | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | এমবেডিং জেনারেশন | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | প্রম্পট টেমপ্লেট | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | এজেন্ট ফ্রেমওয়ার্ক | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot অটো-কনফিগারেশন | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 প্রয়োজনীয়তা

- Java 17 বা তার পরবর্তী সংস্করণ
- Gradle 8.x (wrapper অন্তর্ভুক্ত)

### 🔧 দ্রুত শুরু

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 লাইসেন্স

MIT লাইসেন্স — বিস্তারিত জানতে [LICENSE](LICENSE) দেখুন।
