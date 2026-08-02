<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Bộ công cụ phát triển AI cho Java — máy khách LLM, kho lưu trữ vector, embedding, quản lý prompt**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Tiếng Anh](#tiếng-anh) | [Tiếng Trung](#tiếng-trung)

---

## Tiếng Anh

**AiKit Java** là bộ công cụ phát triển AI toàn diện cho Java 17+. Nó cung cấp mọi thứ bạn cần để xây dựng ứng dụng hỗ trợ AI — từ máy khách trò chuyện LLM và tìm kiếm vector đến tạo mẫu prompt và khung tác nhân (agent).

### 🚀 Tính năng

- **Máy khách LLM đa nhà cung cấp** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini với phát trực tuyến (SSE), nhóm kết nối và thử lại với backoff theo cấp số nhân
- **Kho lưu trữ vector** — Chỉ mục HNSW trong bộ nhớ với độ tương đồng cosine/euclidean/tích vô hướng và lọc theo siêu dữ liệu
- **Tạo embedding** — Embedding đa nhà cung cấp với xử lý theo lô và bộ nhớ đệm dựa trên Caffeine
- **Quản lý prompt** — Công cụ tạo mẫu giống Jinja2 với biến, câu điều kiện, vòng lặp và kiểm soát phiên bản
- **Khung tác nhân** — Tác nhân ReAct (Lý luận + Hành động) với đăng ký công cụ và thực thi chuỗi công cụ
- **Tích hợp Spring Boot** — Tự động cấu hình qua `@ConfigurationProperties`

### 📦 Mô-đun

| Mô-đun | Mô tả | Các lớp chính |
|--------|-------------|-------------|
| `aikit-core` | Giao diện và mô hình cốt lõi | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | Máy khách LLM với HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Lưu trữ và tìm kiếm vector | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Tạo embedding | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Mẫu prompt | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Khung tác nhân | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Tự động cấu hình Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Yêu cầu

- Java 17 trở lên
- Gradle 8.x (kèm wrapper)

### 🔧 Bắt đầu nhanh

**1. Sao chép kho lưu trữ**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Xây dựng dự án**

```bash
./gradlew build
```

**3. Thêm làm phụ thuộc**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Ví dụ sử dụng

#### Trò chuyện LLM

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

#### Tìm kiếm vector

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

#### Tạo mẫu prompt

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

#### Tác nhân với công cụ

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

### 🧪 Chạy kiểm thử

```bash
./gradlew test
```

### 📚 Tài liệu

- Javadoc: `./gradlew javadoc`
- Tài liệu API đầy đủ có trong Javadoc đã tạo

### 🤝 Đóng góp

Xem [CONTRIBUTING.md](CONTRIBUTING.md) để biết hướng dẫn về phong cách mã, PR và quy trình phát triển.

### 📄 Giấy phép

Giấy phép MIT — xem [LICENSE](LICENSE) để biết chi tiết.

---

## Tiếng Trung

**AiKit Java** là bộ công cụ phát triển AI toàn diện cho Java 17+, cung cấp mọi thứ bạn cần từ máy khách trò chuyện LLM và tìm kiếm vector đến mẫu prompt và khung tác nhân.

### 🚀 Tính năng

- **Máy khách LLM đa nhà cung cấp** — Hỗ trợ OpenAI, Anthropic Claude, DeepSeek, Google Gemini, bao gồm phản hồi phát trực tuyến (SSE), nhóm kết nối và thử lại với backoff theo cấp số nhân
- **Kho lưu trữ vector** — Chỉ mục vector trong bộ nhớ dựa trên HNSW, hỗ trợ độ tương đồng cosine/euclidean/tích vô hướng và lọc theo siêu dữ liệu
- **Tạo embedding** — Embedding đa nhà cung cấp, xử lý theo lô, bộ nhớ đệm dựa trên Caffeine
- **Quản lý prompt** — Công cụ tạo mẫu giống Jinja2, hỗ trợ thay thế biến, câu điều kiện, vòng lặp và quản lý phiên bản
- **Khung tác nhân** — Tác nhân ReAct (lý luận + hành động), kèm hệ thống đăng ký công cụ và thực thi chuỗi công cụ
- **Tích hợp Spring Boot** — Tự động cấu hình qua `@ConfigurationProperties`

### 📦 Mô-đun

| Mô-đun | Mô tả | Các lớp chính |
|--------|-------------|-------------|
| `aikit-core` | Giao diện và mô hình cốt lõi | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | Máy khách LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Lưu trữ và tìm kiếm vector | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Tạo embedding | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Mẫu prompt | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Khung tác nhân | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Tự động cấu hình Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Yêu cầu

- Java 17 trở lên
- Gradle 8.x (kèm wrapper)

### 🔧 Bắt đầu nhanh

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Giấy phép

Giấy phép MIT — xem [LICENSE](LICENSE) để biết chi tiết.
