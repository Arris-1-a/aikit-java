<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Kit de desarrollo de IA para Java — cliente LLM, almacén de vectores, embeddings, gestión de prompts**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Inglés](#inglés) | [Chino](#chino)

---

## Inglés

**AiKit Java** es un completo kit de herramientas de desarrollo de IA para Java 17+. Proporciona todo lo que necesitas para crear aplicaciones impulsadas por IA, desde clientes de chat LLM y búsqueda vectorial hasta plantillas de prompts y frameworks de agentes.

### 🚀 Características

- **Cliente LLM multiproveedor** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini con streaming (SSE), agrupación de conexiones y reintentos con retroceso exponencial
- **Almacén de vectores** — Índice HNSW en memoria con similitud coseno/euclidiana/producto punto y filtrado de metadatos
- **Generación de embeddings** — Embeddings multiproveedor con procesamiento por lotes y caché basada en Caffeine
- **Gestión de prompts** — Motor de plantillas similar a Jinja2 con variables, condicionales, bucles y control de versiones
- **Framework de agentes** — Agente ReAct (Razonamiento + Actuación) con registro de herramientas y ejecución de cadenas de herramientas
- **Integración con Spring Boot** — Configuración automática mediante `@ConfigurationProperties`

### 📦 Módulos

| Módulo | Descripción | Clases clave |
|--------|-------------|-------------|
| `aikit-core` | Interfaces y modelos principales | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | Clientes LLM con HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Almacenamiento y búsqueda de vectores | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Generación de embeddings | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Plantillas de prompts | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework de agentes | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autoconfiguración de Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Requisitos

- Java 17 o superior
- Gradle 8.x (wrapper incluido)

### 🔧 Inicio rápido

**1. Clona el repositorio**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Compila el proyecto**

```bash
./gradlew build
```

**3. Añade como dependencia**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Ejemplos de uso

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

#### Búsqueda vectorial

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

#### Plantillas de prompts

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

#### Agente con herramientas

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

### 🧪 Ejecutar pruebas

```bash
./gradlew test
```

### 📚 Documentación

- Javadoc: `./gradlew javadoc`
- La documentación completa de la API está disponible en el Javadoc generado

### 🤝 Contribuir

Consulta [CONTRIBUTING.md](CONTRIBUTING.md) para obtener pautas sobre estilo de código, PR y flujo de trabajo de desarrollo.

### 📄 Licencia

Licencia MIT — consulta [LICENSE](LICENSE) para más detalles.

---

## Chino

**AiKit Java** es un completo kit de herramientas de desarrollo de IA para Java 17+, que ofrece todo lo necesario desde clientes de chat LLM y búsqueda vectorial hasta plantillas de prompts y frameworks de agentes.

### 🚀 Características

- **Cliente LLM multiproveedor** — Compatible con OpenAI, Anthropic Claude, DeepSeek, Google Gemini, con respuestas en streaming (SSE), pool de conexiones y reintentos con retroceso exponencial
- **Almacén de vectores** — Índice vectorial en memoria basado en HNSW, con soporte de similitud coseno/euclidiana/producto punto y filtrado de metadatos
- **Generación de embeddings** — Embeddings multiproveedor, procesamiento por lotes, caché basada en Caffeine
- **Gestión de prompts** — Motor de plantillas tipo Jinja2, con soporte de sustitución de variables, condicionales, bucles y gestión de versiones
- **Framework de agentes** — Agente ReAct (razonamiento + actuación), con sistema de registro de herramientas y ejecución de cadenas de herramientas
- **Integración con Spring Boot** — Configuración automática mediante `@ConfigurationProperties`

### 📦 Módulos

| Módulo | Descripción | Clases clave |
|--------|-------------|-------------|
| `aikit-core` | Interfaces y modelos principales | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | Clientes LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Almacenamiento y búsqueda de vectores | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Generación de embeddings | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Plantillas de prompts | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework de agentes | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autoconfiguración de Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Requisitos

- Java 17 o superior
- Gradle 8.x (wrapper incluido)

### 🔧 Inicio rápido

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Licencia

Licencia MIT — consulta [LICENSE](LICENSE) para más detalles.
