<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Kit Pengembangan AI untuk Java — klien LLM, penyimpanan vektor, embedding, manajemen prompt**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Inggris](#inggris) | [Cina](#cina)

---

## Inggris

**AiKit Java** adalah toolkit pengembangan AI yang komprehensif untuk Java 17+. Toolkit ini menyediakan semua yang Anda butuhkan untuk membangun aplikasi bertenaga AI — mulai dari klien chat LLM dan pencarian vektor hingga templating prompt dan kerangka kerja agen.

### 🚀 Fitur

- **Klien LLM Multi-Penyedia** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini dengan streaming (SSE), connection pooling, dan percobaan ulang dengan backoff eksponensial
- **Penyimpanan Vektor** — Indeks HNSW dalam memori dengan kemiripan kosinus/euclidean/dot-product dan pemfilteran metadata
- **Pembuatan Embedding** — Embedding multi-penyedia dengan pemrosesan batch dan caching berbasis Caffeine
- **Manajemen Prompt** — Mesin template mirip Jinja2 dengan variabel, kondisional, perulangan, dan kontrol versi
- **Kerangka Kerja Agen** — Agen ReAct (Reasoning + Acting) dengan registri alat dan eksekusi rantai alat
- **Integrasi Spring Boot** — Konfigurasi otomatis melalui `@ConfigurationProperties`

### 📦 Modul

| Modul | Deskripsi | Kelas Utama |
|--------|-------------|-------------|
| `aikit-core` | Antarmuka dan model inti | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | Klien LLM dengan HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Penyimpanan dan pencarian vektor | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Pembuatan embedding | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Template prompt | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Kerangka kerja agen | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autokonfigurasi Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Persyaratan

- Java 17 atau lebih baru
- Gradle 8.x (wrapper disertakan)

### 🔧 Memulai dengan Cepat

**1. Klon repositori**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Bangun proyek**

```bash
./gradlew build
```

**3. Tambahkan sebagai dependensi**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Contoh Penggunaan

#### Chat LLM

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

#### Pencarian Vektor

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

#### Templating Prompt

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

#### Agen dengan Alat

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

### 🧪 Menjalankan Pengujian

```bash
./gradlew test
```

### 📚 Dokumentasi

- Javadoc: `./gradlew javadoc`
- Dokumentasi API lengkap tersedia di Javadoc yang dihasilkan

### 🤝 Berkontribusi

Lihat [CONTRIBUTING.md](CONTRIBUTING.md) untuk panduan tentang gaya kode, PR, dan alur kerja pengembangan.

### 📄 Lisensi

Lisensi MIT — lihat [LICENSE](LICENSE) untuk detailnya.

---

## Cina

**AiKit Java** adalah toolkit pengembangan AI yang komprehensif untuk Java 17+, menyediakan semua yang Anda butuhkan mulai dari klien chat LLM dan pencarian vektor hingga template prompt dan kerangka kerja agen.

### 🚀 Fitur

- **Klien LLM Multi-Penyedia** — Mendukung OpenAI, Anthropic Claude, DeepSeek, Google Gemini, termasuk respons streaming (SSE), kumpulan koneksi, dan percobaan ulang dengan backoff eksponensial
- **Penyimpanan Vektor** — Indeks vektor dalam memori berbasis HNSW, mendukung kemiripan kosinus/euclidean/dot-product dan pemfilteran metadata
- **Pembuatan Embedding** — Embedding multi-penyedia, pemrosesan batch, caching berbasis Caffeine
- **Manajemen Prompt** — Mesin template mirip Jinja2, mendukung substitusi variabel, kondisional, perulangan, dan manajemen versi
- **Kerangka Kerja Agen** — Agen ReAct (reasoning + acting), dengan sistem pendaftaran alat dan eksekusi rantai alat
- **Integrasi Spring Boot** — Konfigurasi otomatis melalui `@ConfigurationProperties`

### 📦 Modul

| Modul | Deskripsi | Kelas Utama |
|--------|-------------|-------------|
| `aikit-core` | Antarmuka dan model inti | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | Klien LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Penyimpanan dan pencarian vektor | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Pembuatan embedding | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Template prompt | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Kerangka kerja agen | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autokonfigurasi Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Persyaratan

- Java 17 atau lebih baru
- Gradle 8.x (wrapper disertakan)

### 🔧 Memulai dengan Cepat

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Lisensi

Lisensi MIT — lihat [LICENSE](LICENSE) untuk detailnya.
