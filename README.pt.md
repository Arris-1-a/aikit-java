<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Kit de desenvolvimento de IA para Java — cliente LLM, armazenamento vetorial, embeddings, gerenciamento de prompts**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Inglês](#inglês) | [Chinês](#chinês)

---

## Inglês

**AiKit Java** é um kit de ferramentas abrangente de desenvolvimento de IA para Java 17+. Ele fornece tudo o que você precisa para criar aplicações com IA — de clientes de chat LLM e busca vetorial a templates de prompts e frameworks de agentes.

### 🚀 Recursos

- **Cliente LLM multi-provedor** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini com streaming (SSE), pooling de conexões e nova tentativa com backoff exponencial
- **Armazenamento vetorial** — Índice HNSW em memória com similaridade por cosseno/euclidiana/produto escalar e filtragem de metadados
- **Geração de embeddings** — Embeddings multi-provedor com processamento em lote e cache baseado em Caffeine
- **Gerenciamento de prompts** — Motor de templates semelhante ao Jinja2 com variáveis, condicionais, loops e controle de versão
- **Framework de agentes** — Agente ReAct (Raciocínio + Ação) com registro de ferramentas e execução de cadeias de ferramentas
- **Integração com Spring Boot** — Autoconfiguração via `@ConfigurationProperties`

### 📦 Módulos

| Módulo | Descrição | Classes principais |
|--------|-------------|-------------|
| `aikit-core` | Interfaces e modelos principais | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | Clientes LLM com HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Armazenamento e busca vetorial | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Geração de embeddings | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Templates de prompts | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework de agentes | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autoconfiguração do Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Requisitos

- Java 17 ou superior
- Gradle 8.x (wrapper incluído)

### 🔧 Início rápido

**1. Clone o repositório**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Compile o projeto**

```bash
./gradlew build
```

**3. Adicione como dependência**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Exemplos de uso

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

#### Busca vetorial

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

#### Templates de prompts

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

#### Agente com ferramentas

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

### 🧪 Executando testes

```bash
./gradlew test
```

### 📚 Documentação

- Javadoc: `./gradlew javadoc`
- A documentação completa da API está disponível no Javadoc gerado

### 🤝 Contribuindo

Consulte [CONTRIBUTING.md](CONTRIBUTING.md) para diretrizes sobre estilo de código, PRs e fluxo de trabalho de desenvolvimento.

### 📄 Licença

Licença MIT — consulte [LICENSE](LICENSE) para detalhes.

---

## Chinês

**AiKit Java** é um kit de ferramentas abrangente de desenvolvimento de IA para Java 17+, fornecendo tudo o que você precisa, de clientes de chat LLM e busca vetorial a templates de prompts e frameworks de agentes.

### 🚀 Recursos

- **Cliente LLM multi-provedor** — Suporte a OpenAI, Anthropic Claude, DeepSeek, Google Gemini, com respostas em streaming (SSE), pool de conexões e nova tentativa com backoff exponencial
- **Armazenamento vetorial** — Índice vetorial em memória baseado em HNSW, com suporte a similaridade por cosseno/euclidiana/produto escalar e filtragem de metadados
- **Geração de embeddings** — Embeddings multi-provedor, processamento em lote, cache baseado em Caffeine
- **Gerenciamento de prompts** — Motor de templates semelhante ao Jinja2, com suporte a substituição de variáveis, condicionais, loops e gerenciamento de versões
- **Framework de agentes** — Agente ReAct (raciocínio + ação), com sistema de registro de ferramentas e execução de cadeias de ferramentas
- **Integração com Spring Boot** — Autoconfiguração via `@ConfigurationProperties`

### 📦 Módulos

| Módulo | Descrição | Classes principais |
|--------|-------------|-------------|
| `aikit-core` | Interfaces e modelos principais | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | Clientes LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Armazenamento e busca vetorial | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Geração de embeddings | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Templates de prompts | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework de agentes | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autoconfiguração do Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Requisitos

- Java 17 ou superior
- Gradle 8.x (wrapper incluído)

### 🔧 Início rápido

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Licença

Licença MIT — consulte [LICENSE](LICENSE) para detalhes.
