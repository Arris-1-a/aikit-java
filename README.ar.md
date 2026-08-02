<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**مجموعة تطوير الذكاء الاصطناعي لجافا — عميل LLM، مخزن المتجهات، التضمينات، إدارة البرومبتات**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[الإنجليزية](#الإنجليزية) | [الصينية](#الصينية)

---

## الإنجليزية

**AiKit Java** هي مجموعة أدوات شاملة لتطوير الذكاء الاصطناعي لجافا 17+. توفر كل ما تحتاجه لبناء تطبيقات مدعومة بالذكاء الاصطناعي — من عملاء محادثة LLM والبحث المتجهي إلى قوالب البرومبتات وأطر الوكلاء (Agents).

### 🚀 الميزات

- **عميل LLM متعدد المزودين** — OpenAI وAnthropic Claude وDeepSeek وGoogle Gemini مع البث (SSE) وتجميع الاتصالات وإعادة المحاولة مع التراجع الأسي
- **مخزن المتجهات** — فهرس HNSW في الذاكرة مع تشابه جيب التمام/الإقليدي/الضرب النقطي وتصفية البيانات الوصفية
- **توليد التضمينات** — تضمينات متعددة المزودين مع معالجة دفعات وتخزين مؤقت قائم على Caffeine
- **إدارة البرومبتات** — محرك قوالب شبيه بـ Jinja2 مع متغيرات وشروط وحلقات والتحكم في الإصدارات
- **إطار الوكلاء** — وكيل ReAct (الاستدلال + التنفيذ) مع سجل أدوات وتنفيذ سلاسل الأدوات
- **التكامل مع Spring Boot** — تهيئة تلقائية عبر `@ConfigurationProperties`

### 📦 الوحدات

| الوحدة | الوصف | الفئات الرئيسية |
|--------|-------------|-------------|
| `aikit-core` | الواجهات والنماذج الأساسية | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | عملاء LLM مع HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | تخزين المتجهات والبحث | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | توليد التضمينات | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | قوالب البرومبتات | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | إطار الوكلاء | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | التهيئة التلقائية لـ Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 المتطلبات

- جافا 17 أو أحدث
- Gradle 8.x (مضمن مع wrapper)

### 🔧 بدء الاستخدام السريع

**1. استنساخ المستودع**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. بناء المشروع**

```bash
./gradlew build
```

**3. الإضافة كاعتماد (dependency)**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 أمثلة الاستخدام

#### محادثة LLM

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

#### البحث المتجهي

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

#### قوالب البرومبتات

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

#### وكيل مع أدوات

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

### 🧪 تشغيل الاختبارات

```bash
./gradlew test
```

### 📚 التوثيق

- Javadoc: `./gradlew javadoc`
- توثيق API الكامل متاح في Javadoc المُنشأ

### 🤝 المساهمة

راجع [CONTRIBUTING.md](CONTRIBUTING.md) للحصول على إرشادات حول نمط الكود وطلبات السحب (PR) وسير عمل التطوير.

### 📄 الترخيص

رخصة MIT — راجع [LICENSE](LICENSE) للحصول على التفاصيل.

---

## الصينية

**AiKit Java** هي مجموعة أدوات شاملة لتطوير الذكاء الاصطناعي لجافا 17+، وتوفر كل ما تحتاجه من عملاء محادثة LLM والبحث المتجهي إلى قوالب البرومبتات وأطر الوكلاء.

### 🚀 الميزات

- **عميل LLM متعدد المزودين** — يدعم OpenAI وAnthropic Claude وDeepSeek وGoogle Gemini، مع استجابات البث (SSE) وتجميع الاتصالات وإعادة المحاولة مع التراجع الأسي
- **مخزن المتجهات** — فهرس متجهات في الذاكرة قائم على HNSW، يدعم تشابه جيب التمام/الإقليدي/الضرب النقطي وتصفية البيانات الوصفية
- **توليد التضمينات** — تضمينات متعددة المزودين، معالجة دفعات، تخزين مؤقت قائم على Caffeine
- **إدارة البرومبتات** — محرك قوالب شبيه بـ Jinja2، يدعم استبدال المتغيرات والشروط والحلقات وإدارة الإصدارات
- **إطار الوكلاء** — وكيل ReAct (الاستدلال + التنفيذ)، مع نظام تسجيل الأدوات وتنفيذ سلاسل الأدوات
- **التكامل مع Spring Boot** — تهيئة تلقائية عبر `@ConfigurationProperties`

### 📦 الوحدات

| الوحدة | الوصف | الفئات الرئيسية |
|--------|-------------|-------------|
| `aikit-core` | الواجهات والنماذج الأساسية | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | عملاء LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | تخزين المتجهات والبحث | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | توليد التضمينات | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | قوالب البرومبتات | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | إطار الوكلاء | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | التهيئة التلقائية لـ Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 المتطلبات

- جافا 17 أو أحدث
- Gradle 8.x (مضمن مع wrapper)

### 🔧 بدء الاستخدام السريع

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 الترخيص

رخصة MIT — راجع [LICENSE](LICENSE) للحصول على التفاصيل.
