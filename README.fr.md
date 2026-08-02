<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Kit de développement IA pour Java — client LLM, stockage vectoriel, embeddings, gestion des prompts**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[Anglais](#anglais) | [Chinois](#chinois)

---

## Anglais

**AiKit Java** est une boîte à outils complète de développement IA pour Java 17+. Elle fournit tout ce dont vous avez besoin pour créer des applications propulsées par l'IA — des clients de chat LLM et de la recherche vectorielle aux modèles de prompts et aux frameworks d'agents.

### 🚀 Fonctionnalités

- **Client LLM multi-fournisseurs** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini avec streaming (SSE), pooling de connexions et relance avec backoff exponentiel
- **Stockage vectoriel** — Index HNSW en mémoire avec similarité cosinus/euclidienne/produit scalaire et filtrage par métadonnées
- **Génération d'embeddings** — Embeddings multi-fournisseurs avec traitement par lots et cache basé sur Caffeine
- **Gestion des prompts** — Moteur de templates de type Jinja2 avec variables, conditionnelles, boucles et contrôle de version
- **Framework d'agents** — Agent ReAct (Raisonnement + Action) avec registre d'outils et exécution de chaînes d'outils
- **Intégration Spring Boot** — Auto-configuration via `@ConfigurationProperties`

### 📦 Modules

| Module | Description | Classes clés |
|--------|-------------|-------------|
| `aikit-core` | Interfaces et modèles principaux | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | Clients LLM avec HTTP/SSE | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | Stockage et recherche vectoriels | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | Génération d'embeddings | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | Templates de prompts | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework d'agents | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autoconfiguration Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Prérequis

- Java 17 ou version ultérieure
- Gradle 8.x (wrapper inclus)

### 🔧 Démarrage rapide

**1. Cloner le dépôt**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. Compiler le projet**

```bash
./gradlew build
```

**3. Ajouter comme dépendance**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 Exemples d'utilisation

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

#### Recherche vectorielle

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

#### Templating de prompts

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

#### Agent avec outils

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

### 🧪 Exécution des tests

```bash
./gradlew test
```

### 📚 Documentation

- Javadoc : `./gradlew javadoc`
- La documentation complète de l'API est disponible dans le Javadoc généré

### 🤝 Contribuer

Consultez [CONTRIBUTING.md](CONTRIBUTING.md) pour les directives sur le style de code, les PR et le workflow de développement.

### 📄 Licence

Licence MIT — voir [LICENSE](LICENSE) pour plus de détails.

---

## Chinois

**AiKit Java** est une boîte à outils complète de développement IA pour Java 17+, offrant tout ce dont vous avez besoin, des clients de chat LLM et de la recherche vectorielle aux templates de prompts et aux frameworks d'agents.

### 🚀 Fonctionnalités

- **Client LLM multi-fournisseurs** — Prise en charge d'OpenAI, Anthropic Claude, DeepSeek, Google Gemini, avec réponses en streaming (SSE), pool de connexions et relance avec backoff exponentiel
- **Stockage vectoriel** — Index vectoriel en mémoire basé sur HNSW, prise en charge de la similarité cosinus/euclidienne/produit scalaire et du filtrage par métadonnées
- **Génération d'embeddings** — Embeddings multi-fournisseurs, traitement par lots, cache basé sur Caffeine
- **Gestion des prompts** — Moteur de templates de type Jinja2, prise en charge de la substitution de variables, des conditionnelles, des boucles et de la gestion des versions
- **Framework d'agents** — Agent ReAct (raisonnement + action), avec système d'enregistrement d'outils et exécution de chaînes d'outils
- **Intégration Spring Boot** — Auto-configuration via `@ConfigurationProperties`

### 📦 Modules

| Module | Description | Classes clés |
|--------|-------------|-------------|
| `aikit-core` | Interfaces et modèles principaux | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | Clients LLM | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | Stockage et recherche vectoriels | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | Génération d'embeddings | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | Templates de prompts | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | Framework d'agents | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Autoconfiguration Spring Boot | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 Prérequis

- Java 17 ou version ultérieure
- Gradle 8.x (wrapper inclus)

### 🔧 Démarrage rapide

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 Licence

Licence MIT — voir [LICENSE](LICENSE) pour plus de détails.
