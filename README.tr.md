<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java için AI Geliştirme Kiti — LLM istemcisi, vektör deposu, embedding'ler, prompt yönetimi**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[İngilizce](#i̇ngilizce) | [Çince](#çince)

---

## İngilizce

**AiKit Java**, Java 17+ için kapsamlı bir AI geliştirme araç setidir. LLM sohbet istemcileri ve vektör aramadan prompt şablonlamaya ve ajan çerçevelerine kadar AI destekli uygulamalar oluşturmak için ihtiyacınız olan her şeyi sağlar.

### 🚀 Özellikler

- **Çoklu Sağlayıcılı LLM İstemcisi** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini; akış (SSE), bağlantı havuzlama ve üstel geri çekilme ile yeniden deneme desteği
- **Vektör Deposu** — Kosinüs/Öklid/nokta çarpım benzerliği ve meta veri filtreleme ile bellek içi HNSW dizini
- **Embedding Üretimi** — Toplu işleme ve Caffeine tabanlı önbellekleme ile çoklu sağlayıcılı embedding'ler
- **Prompt Yönetimi** — Değişkenler, koşullar, döngüler ve sürüm kontrolü içeren Jinja2 benzeri şablon motoru
- **Ajan Çerçevesi** — Araç kayıt defteri ve araç zinciri yürütme ile ReAct (Akıl Yürütme + Eylem) ajanı
- **Spring Boot Entegrasyonu** — `@ConfigurationProperties` ile otomatik yapılandırma

### 📦 Modüller

| Modül | Açıklama | Ana Sınıflar |
|--------|-------------|-------------|
| `aikit-core` | Çekirdek arayüzler ve modeller | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE ile LLM istemcileri | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Vektör depolama ve arama | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Embedding üretimi | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Prompt şablonları | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Ajan çerçevesi | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot otomatik yapılandırması | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Gereksinimler

- Java 17 veya üzeri
- Gradle 8.x (wrapper dahildir)

### 🔧 Hızlı Başlangıç

**1. Depoyu klonlayın**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Projeyi derleyin**

```bash
./gradlew build
```

**3. Bağımlılık olarak ekleyin**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Kullanım Örnekleri

#### LLM Sohbeti

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

#### Vektör Araması

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

#### Prompt Şablonlama

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

#### Araçlarla Ajan

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

### 🧪 Testleri Çalıştırma

```bash
./gradlew test
```

### 📚 Dokümantasyon

- Javadoc: `./gradlew javadoc`
- Tam API dokümantasyonu oluşturulan Javadoc'ta mevcuttur

### 🤝 Katkıda Bulunma

Kod stili, PR'ler ve geliştirme iş akışı hakkındaki yönergeler için [CONTRIBUTING.md](CONTRIBUTING.md) dosyasına bakın.

### 📄 Lisans

MIT Lisansı — ayrıntılar için [LICENSE](LICENSE) dosyasına bakın.

---

## Çince

**AiKit Java**, Java 17+ için kapsamlı bir AI geliştirme araç setidir; LLM sohbet istemcileri ve vektör aramadan prompt şablonlarına ve ajan çerçevelerine kadar her şeyi sağlar.

### 🚀 Özellikler

- **Çoklu Sağlayıcılı LLM İstemcisi** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini desteği; akış yanıtları (SSE), bağlantı havuzu ve üstel geri çekilme ile yeniden deneme dahil
- **Vektör Deposu** — HNSW tabanlı bellek içi vektör dizini; kosinüs/Öklid/nokta çarpım benzerliği ve meta veri filtreleme desteği
- **Embedding Üretimi** — Çoklu sağlayıcılı embedding'ler, toplu işleme, Caffeine tabanlı önbellekleme
- **Prompt Yönetimi** — Jinja2 benzeri şablon motoru; değişken değiştirme, koşullar, döngüler ve sürüm yönetimi desteği
- **Ajan Çerçevesi** — ReAct (akıl yürütme + eylem) ajanı; araç kayıt sistemi ve araç zinciri yürütme ile
- **Spring Boot Entegrasyonu** — `@ConfigurationProperties` ile otomatik yapılandırma

### 📦 Modüller

| Modül | Açıklama | Ana Sınıflar |
|--------|-------------|-------------|
| `aikit-core` | Çekirdek arayüzler ve modeller | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM istemcileri | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Vektör depolama ve arama | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Embedding üretimi | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Prompt şablonları | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Ajan çerçevesi | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot otomatik yapılandırması | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Gereksinimler

- Java 17 veya üzeri
- Gradle 8.x (wrapper dahildir)

### 🔧 Hızlı Başlangıç

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Lisans

MIT Lisansı — ayrıntılar için [LICENSE](LICENSE) dosyasına bakın.
