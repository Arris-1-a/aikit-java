<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Kit di sviluppo AI per Java — client LLM, archivio vettoriale, embedding, gestione dei prompt**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Inglese](#inglese) | [Cinese](#cinese)

---

## Inglese

**AiKit Java** è un kit completo di strumenti di sviluppo AI per Java 17+. Fornisce tutto ciò che serve per creare applicazioni basate sull'IA — dai client di chat LLM e la ricerca vettoriale al templating dei prompt e ai framework per agenti.

### 🚀 Funzionalità

- **Client LLM multi-fornitore** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini con streaming (SSE), pooling delle connessioni e nuovi tentativi con backoff esponenziale
- **Archivio vettoriale** — Indice HNSW in memoria con similarità coseno/euclidea/prodotto scalare e filtraggio dei metadati
- **Generazione di embedding** — Embedding multi-fornitore con elaborazione batch e cache basata su Caffeine
- **Gestione dei prompt** — Motore di template simile a Jinja2 con variabili, condizionali, cicli e controllo delle versioni
- **Framework per agenti** — Agente ReAct (Ragionamento + Azione) con registro degli strumenti ed esecuzione di catene di strumenti
- **Integrazione con Spring Boot** — Auto-configurazione tramite `@ConfigurationProperties`

### 📦 Moduli

| Modulo | Descrizione | Classi principali |
|--------|-------------|-------------|
| `aikit-core` | Interfacce e modelli principali | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | Client LLM con HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Archiviazione e ricerca vettoriale | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Generazione di embedding | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Template di prompt | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework per agenti | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Auto-configurazione Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Requisiti

- Java 17 o successivo
- Gradle 8.x (wrapper incluso)

### 🔧 Avvio rapido

**1. Clona il repository**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Compila il progetto**

```bash
./gradlew build
```

**3. Aggiungi come dipendenza**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Esempi di utilizzo

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

#### Ricerca vettoriale

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

#### Templating dei prompt

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

#### Agente con strumenti

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

### 🧪 Esecuzione dei test

```bash
./gradlew test
```

### 📚 Documentazione

- Javadoc: `./gradlew javadoc`
- La documentazione API completa è disponibile nel Javadoc generato

### 🤝 Contributi

Consulta [CONTRIBUTING.md](CONTRIBUTING.md) per le linee guida su stile del codice, PR e flusso di lavoro di sviluppo.

### 📄 Licenza

Licenza MIT — consulta [LICENSE](LICENSE) per i dettagli.

---

## Cinese

**AiKit Java** è un kit completo di strumenti di sviluppo AI per Java 17+, che fornisce tutto il necessario: dai client di chat LLM e la ricerca vettoriale ai template di prompt e ai framework per agenti.

### 🚀 Funzionalità

- **Client LLM multi-fornitore** — Supporto per OpenAI, Anthropic Claude, DeepSeek, Google Gemini, incluse risposte in streaming (SSE), pool di connessioni e nuovi tentativi con backoff esponenziale
- **Archivio vettoriale** — Indice vettoriale in memoria basato su HNSW, con supporto per similarità coseno/euclidea/prodotto scalare e filtraggio dei metadati
- **Generazione di embedding** — Embedding multi-fornitore, elaborazione batch, cache basata su Caffeine
- **Gestione dei prompt** — Motore di template simile a Jinja2, con supporto per sostituzione di variabili, condizionali, cicli e gestione delle versioni
- **Framework per agenti** — Agente ReAct (ragionamento + azione), con sistema di registrazione degli strumenti ed esecuzione di catene di strumenti
- **Integrazione con Spring Boot** — Auto-configurazione tramite `@ConfigurationProperties`

### 📦 Moduli

| Modulo | Descrizione | Classi principali |
|--------|-------------|-------------|
| `aikit-core` | Interfacce e modelli principali | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | Client LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Archiviazione e ricerca vettoriale | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Generazione di embedding | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Template di prompt | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework per agenti | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Auto-configurazione Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Requisiti

- Java 17 o successivo
- Gradle 8.x (wrapper incluso)

### 🔧 Avvio rapido

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Licenza

Licenza MIT — consulta [LICENSE](LICENSE) per i dettagli.
