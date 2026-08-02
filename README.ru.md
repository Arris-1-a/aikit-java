<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Набор средств разработки ИИ для Java — LLM-клиент, векторное хранилище, эмбеддинги, управление промптами**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Английский](#английский) | [Китайский](#китайский)

---

## Английский

**AiKit Java** — это комплексный набор средств разработки ИИ для Java 17+. Он предоставляет всё необходимое для создания приложений на базе ИИ — от LLM-клиентов для чата и векторного поиска до шаблонизации промптов и фреймворков агентов.

### 🚀 Возможности

- **Мультипровайдерный LLM-клиент** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini со стримингом (SSE), пулом соединений и повторными попытками с экспоненциальной задержкой
- **Векторное хранилище** — индекс HNSW в памяти с косинусной/евклидовой/скалярной мерой сходства и фильтрацией по метаданным
- **Генерация эмбеддингов** — мультипровайдерные эмбеддинги с пакетной обработкой и кэшированием на основе Caffeine
- **Управление промптами** — шаблонизатор в стиле Jinja2 с переменными, условиями, циклами и контролем версий
- **Фреймворк агентов** — агент ReAct (рассуждение + действие) с реестром инструментов и выполнением цепочек инструментов
- **Интеграция с Spring Boot** — автоматическая настройка через `@ConfigurationProperties`

### 📦 Модули

| Модуль | Описание | Ключевые классы |
|--------|-------------|-------------|
| `aikit-core` | Основные интерфейсы и модели | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | LLM-клиенты с HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Векторное хранение и поиск | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Генерация эмбеддингов | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Шаблоны промптов | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Фреймворк агентов | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Автоконфигурация Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Требования

- Java 17 или новее
- Gradle 8.x (wrapper включён)

### 🔧 Быстрый старт

**1. Клонируйте репозиторий**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Соберите проект**

```bash
./gradlew build
```

**3. Добавьте как зависимость**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Примеры использования

#### Чат с LLM

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

#### Векторный поиск

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

#### Шаблонизация промптов

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

#### Агент с инструментами

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

### 🧪 Запуск тестов

```bash
./gradlew test
```

### 📚 Документация

- Javadoc: `./gradlew javadoc`
- Полная документация API доступна в сгенерированном Javadoc

### 🤝 Участие в разработке

См. [CONTRIBUTING.md](CONTRIBUTING.md) с рекомендациями по стилю кода, pull request и рабочему процессу разработки.

### 📄 Лицензия

Лицензия MIT — подробности см. в [LICENSE](LICENSE).

---

## Китайский

**AiKit Java** — это комплексный набор средств разработки ИИ для Java 17+, предоставляющий всё необходимое: от LLM-клиентов для чата и векторного поиска до шаблонов промптов и фреймворков агентов.

### 🚀 Возможности

- **Мультипровайдерный LLM-клиент** — поддержка OpenAI, Anthropic Claude, DeepSeek, Google Gemini, включая стриминговые ответы (SSE), пул соединений и повторные попытки с экспоненциальной задержкой
- **Векторное хранилище** — векторный индекс в памяти на основе HNSW, поддержка косинусной/евклидовой/скалярной меры сходства и фильтрации по метаданным
- **Генерация эмбеддингов** — мультипровайдерные эмбеддинги, пакетная обработка, кэширование на основе Caffeine
- **Управление промптами** — шаблонизатор в стиле Jinja2 с поддержкой подстановки переменных, условий, циклов и управления версиями
- **Фреймворк агентов** — агент ReAct (рассуждение + действие) с системой регистрации инструментов и выполнением цепочек инструментов
- **Интеграция с Spring Boot** — автоматическая настройка через `@ConfigurationProperties`

### 📦 Модули

| Модуль | Описание | Ключевые классы |
|--------|-------------|-------------|
| `aikit-core` | Основные интерфейсы и модели | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM-клиенты | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Векторное хранение и поиск | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Генерация эмбеддингов | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Шаблоны промптов | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Фреймворк агентов | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Автоконфигурация Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Требования

- Java 17 или новее
- Gradle 8.x (wrapper включён)

### 🔧 Быстрый старт

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Лицензия

Лицензия MIT — подробности см. в [LICENSE](LICENSE).
