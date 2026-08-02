<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java க்கான AI டெவலப்மென்ட் கிட் — LLM கிளையண்ட், வெக்டர் ஸ்டோர், எம்பெடிங்குகள், ப்ராம்ப்ட் மேனேஜ்மென்ட்**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[ஆங்கிலம்](#ஆங்கிலம்) | [சீனம்](#சீனம்)

---

## ஆங்கிலம்

**AiKit Java** என்பது Java 17+ க்கான ஒரு விரிவான AI டெவலப்மென்ட் டூல்கிட் ஆகும். LLM அரட்டை கிளையண்டுகள் மற்றும் வெக்டர் தேடலில் இருந்து ப்ராம்ப்ட் டெம்ப்ளேட்டிங் மற்றும் ஏஜென்ட் ஃபிரேம்வொர்க்குகள் வரை — AI-இயங்கும் பயன்பாடுகளை உருவாக்கத் தேவையான அனைத்தையும் இது வழங்குகிறது.

### 🚀 அம்சங்கள்

- **பல-வழங்குநர் LLM கிளையண்ட்** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini; ஸ்ட்ரீமிங் (SSE), இணைப்பு பூலிங் மற்றும் எக்ஸ்போனென்ஷியல் பேக்காஃப் ரீட்ரை உடன்
- **வெக்டர் ஸ்டோர்** — கோசைன்/யூக்ளிடியன்/டாட்-புராடக்ட் ஒற்றுமை மற்றும் மெட்டாடேட்டா வடிகட்டுதலுடன் இன்-மெமரி HNSW இன்டெக்ஸ்
- **எம்பெடிங் உருவாக்கம்** — பேட்ச் செயலாக்கம் மற்றும் Caffeine அடிப்படையிலான கேச்சிங்குடன் பல-வழங்குநர் எம்பெடிங்குகள்
- **ப்ராம்ப்ட் மேனேஜ்மென்ட்** — மாறிகள், நிபந்தனைகள், லூப்கள் மற்றும் பதிப்பு கட்டுப்பாட்டுடன் Jinja2 போன்ற டெம்ப்ளேட் இன்ஜின்
- **ஏஜென்ட் ஃபிரேம்வொர்க்** — கருவி பதிவேடு மற்றும் கருவி சங்கிலி செயலாக்கத்துடன் ReAct (ரீசனிங் + ஆக்டிங்) ஏஜென்ட்
- **Spring Boot ஒருங்கிணைப்பு** — `@ConfigurationProperties` வழியாக ஆட்டோ-கான்ஃபிகரேஷன்

### 📦 தொகுதிகள்

| தொகுதி | விளக்கம் | முக்கிய கிளாஸ்கள் |
|--------|-------------|-------------|
| `aikit-core` | முக்கிய இடைமுகங்கள் மற்றும் மாடல்கள் | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE உடன் LLM கிளையண்டுகள் | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | வெக்டர் சேமிப்பு மற்றும் தேடல் | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | எம்பெடிங் உருவாக்கம் | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | ப்ராம்ப்ட் டெம்ப்ளேட்டுகள் | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | ஏஜென்ட் ஃபிரேம்வொர்க் | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ஆட்டோகான்ஃபிக் | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 தேவைகள்

- Java 17 அல்லது அதற்குப் பிறகு
- Gradle 8.x (wrapper சேர்க்கப்பட்டுள்ளது)

### 🔧 விரைவான தொடக்கம்

**1. ரெப்போசிட்டரியை குளோன் செய்யுங்கள்**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. ப்ராஜெக்ட்டை பில்ட் செய்யுங்கள்**

```bash
./gradlew build
```

**3. சார்புநிலையாக (dependency) சேர்க்கவும்**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 பயன்பாட்டு எடுத்துக்காட்டுகள்

#### LLM அரட்டை

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

#### வெக்டர் தேடல்

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

#### ப்ராம்ப்ட் டெம்ப்ளேட்டிங்

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

#### கருவிகளுடன் ஏஜென்ட்

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

### 🧪 டெஸ்ட்களை இயக்குதல்

```bash
./gradlew test
```

### 📚 ஆவணங்கள்

- Javadoc: `./gradlew javadoc`
- முழு API ஆவணங்கள் உருவாக்கப்பட்ட Javadoc இல் கிடைக்கின்றன

### 🤝 பங்களிப்பு

குறியீட்டு பாணி, PRகள் மற்றும் டெவலப்மென்ட் வொர்க்ஃப்ளோவிற்கான வழிகாட்டுதல்களுக்கு [CONTRIBUTING.md](CONTRIBUTING.md) ஐப் பார்க்கவும்.

### 📄 உரிமம்

MIT உரிமம் — விவரங்களுக்கு [LICENSE](LICENSE) ஐப் பார்க்கவும்.

---

## சீனம்

**AiKit Java** என்பது Java 17+ க்கான ஒரு விரிவான AI டெவலப்மென்ட் டூல்கிட் ஆகும், இது LLM அரட்டை கிளையண்டுகள் மற்றும் வெக்டர் தேடலில் இருந்து ப்ராம்ப்ட் டெம்ப்ளேட்டுகள் மற்றும் ஏஜென்ட் ஃபிரேம்வொர்க்குகள் வரை அனைத்தையும் வழங்குகிறது.

### 🚀 அம்சங்கள்

- **பல-வழங்குநர் LLM கிளையண்ட்** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini ஆதரவு; ஸ்ட்ரீமிங் பதில்கள் (SSE), இணைப்பு பூல் மற்றும் எக்ஸ்போனென்ஷியல் பேக்காஃப் ரீட்ரை உட்பட
- **வெக்டர் ஸ்டோர்** — HNSW அடிப்படையிலான இன்-மெமரி வெக்டர் இன்டெக்ஸ்; கோசைன்/யூக்ளிடியன்/டாட்-புராடக்ட் ஒற்றுமை மற்றும் மெட்டாடேட்டா வடிகட்டுதலுக்கான ஆதரவு
- **எம்பெடிங் உருவாக்கம்** — பல-வழங்குநர் எம்பெடிங்குகள், பேட்ச் செயலாக்கம், Caffeine அடிப்படையிலான கேச்சிங்
- **ப்ராம்ப்ட் மேனேஜ்மென்ட்** — Jinja2 போன்ற டெம்ப்ளேட் இன்ஜின்; மாறி மாற்றீடு, நிபந்தனைகள், லூப்கள் மற்றும் பதிப்பு மேலாண்மைக்கான ஆதரவு
- **ஏஜென்ட் ஃபிரேம்வொர்க்** — ReAct (ரீசனிங் + ஆக்டிங்) ஏஜென்ட்; கருவி பதிவு அமைப்பு மற்றும் கருவி சங்கிலி செயலாக்கத்துடன்
- **Spring Boot ஒருங்கிணைப்பு** — `@ConfigurationProperties` வழியாக ஆட்டோ-கான்ஃபிகரேஷன்

### 📦 தொகுதிகள்

| தொகுதி | விளக்கம் | முக்கிய கிளாஸ்கள் |
|--------|-------------|-------------|
| `aikit-core` | முக்கிய இடைமுகங்கள் மற்றும் மாடல்கள் | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM கிளையண்டுகள் | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | வெக்டர் சேமிப்பு மற்றும் தேடல் | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | எம்பெடிங் உருவாக்கம் | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | ப்ராம்ப்ட் டெம்ப்ளேட்டுகள் | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | ஏஜென்ட் ஃபிரேம்வொர்க் | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot ஆட்டோ-கான்ஃபிகரேஷன் | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 தேவைகள்

- Java 17 அல்லது அதற்குப் பிறகு
- Gradle 8.x (wrapper சேர்க்கப்பட்டுள்ளது)

### 🔧 விரைவான தொடக்கம்

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 உரிமம்

MIT உரிமம் — விவரங்களுக்கு [LICENSE](LICENSE) ஐப் பார்க்கவும்.
