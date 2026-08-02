<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java용 AI 개발 키트 — LLM 클라이언트, 벡터 스토어, 임베딩, 프롬프트 관리**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[영어](#영어) | [중국어](#중국어)

---

## 영어

**AiKit Java**는 Java 17+를 위한 포괄적인 AI 개발 툴킷입니다. LLM 채팅 클라이언트와 벡터 검색부터 프롬프트 템플릿과 에이전트 프레임워크까지, AI 기반 애플리케이션을 구축하는 데 필요한 모든 것을 제공합니다.

### 🚀 기능

- **멀티 프로바이더 LLM 클라이언트** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini 지원, 스트리밍(SSE), 커넥션 풀링, 지수 백오프 재시도 포함
- **벡터 스토어** — 코사인/유클리드/내적 유사도 및 메타데이터 필터링을 지원하는 인메모리 HNSW 인덱스
- **임베딩 생성** — 배치 처리와 Caffeine 기반 캐싱을 갖춘 멀티 프로바이더 임베딩
- **프롬프트 관리** — 변수, 조건문, 반복문, 버전 관리를 지원하는 Jinja2 스타일 템플릿 엔진
- **에이전트 프레임워크** — 도구 레지스트리와 도구 체인 실행을 갖춘 ReAct(추론 + 행동) 에이전트
- **Spring Boot 통합** — `@ConfigurationProperties`를 통한 자동 구성

### 📦 모듈

| 모듈 | 설명 | 핵심 클래스 |
|--------|-------------|-------------|
| `aikit-core` | 핵심 인터페이스 및 모델 | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE 기반 LLM 클라이언트 | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | 벡터 저장 및 검색 | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | 임베딩 생성 | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | 프롬프트 템플릿 | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | 에이전트 프레임워크 | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot 자동 구성 | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 요구 사항

- Java 17 이상
- Gradle 8.x (wrapper 포함)

### 🔧 빠른 시작

**1. 리포지토리 클론**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. 프로젝트 빌드**

```bash
./gradlew build
```

**3. 의존성으로 추가**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 사용 예제

#### LLM 채팅

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

#### 벡터 검색

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

#### 프롬프트 템플릿

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

#### 도구를 갖춘 에이전트

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

### 🧪 테스트 실행

```bash
./gradlew test
```

### 📚 문서

- Javadoc: `./gradlew javadoc`
- 전체 API 문서는 생성된 Javadoc에서 확인할 수 있습니다

### 🤝 기여

코드 스타일, PR 및 개발 워크플로에 대한 지침은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요.

### 📄 라이선스

MIT 라이선스 — 자세한 내용은 [LICENSE](LICENSE)를 참조하세요.

---

## 중국어

**AiKit Java**는 Java 17+를 위한 포괄적인 AI 개발 툴킷으로, LLM 채팅 클라이언트와 벡터 검색부터 프롬프트 템플릿과 에이전트 프레임워크까지 모든 것을 제공합니다.

### 🚀 기능

- **멀티 프로바이더 LLM 클라이언트** — OpenAI, Anthropic Claude, DeepSeek, Google Gemini 지원, 스트리밍 응답(SSE), 커넥션 풀, 지수 백오프 재시도 포함
- **벡터 스토어** — HNSW 기반 인메모리 벡터 인덱스, 코사인/유클리드/내적 유사도 및 메타데이터 필터링 지원
- **임베딩 생성** — 멀티 프로바이더 임베딩, 배치 처리, Caffeine 기반 캐싱
- **프롬프트 관리** — Jinja2 스타일 템플릿 엔진, 변수 치환, 조건문, 반복문 및 버전 관리 지원
- **에이전트 프레임워크** — ReAct(추론 + 행동) 에이전트, 도구 등록 시스템과 도구 체인 실행 포함
- **Spring Boot 통합** — `@ConfigurationProperties`를 통한 자동 구성

### 📦 모듈

| 모듈 | 설명 | 핵심 클래스 |
|--------|-------------|-------------|
| `aikit-core` | 핵심 인터페이스 및 모델 | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM 클라이언트 | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | 벡터 저장 및 검색 | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | 임베딩 생성 | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | 프롬프트 템플릿 | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | 에이전트 프레임워크 | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot 자동 구성 | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 요구 사항

- Java 17 이상
- Gradle 8.x (wrapper 포함)

### 🔧 빠른 시작

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 라이선스

MIT 라이선스 — 자세한 내용은 [LICENSE](LICENSE)를 참조하세요.
