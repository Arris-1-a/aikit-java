<div align="center">

**🌐 Language / 选择语言 / Idioma:**

[English](README.md) · [简体中文](README.zh-CN.md) · [हिन्दी](README.hi.md) · [Español](README.es.md) · [Français](README.fr.md) · [العربية](README.ar.md) · [বাংলা](README.bn.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [اردو](README.ur.md) · [Bahasa Indonesia](README.id.md) · [Deutsch](README.de.md) · [日本語](README.ja.md) · [मराठी](README.mr.md) · [తెలుగు](README.te.md) · [Türkçe](README.tr.md) · [தமிழ்](README.ta.md) · [Tiếng Việt](README.vi.md) · [한국어](README.ko.md) · [Italiano](README.it.md)

</div>

---

# AiKit Java

<div align="center">

**Java 向け AI 開発キット — LLM クライアント、ベクターストア、エンベディング、プロンプト管理**

[![CI](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nousresearch/aikit-java/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)](https://adoptium.net/)

</div>

---

[英語](#英語) | [中国語](#中国語)

---

## 英語

**AiKit Java** は、Java 17+ 向けの包括的な AI 開発ツールキットです。LLM チャットクライアントやベクター検索から、プロンプトテンプレートやエージェントフレームワークまで、AI を活用したアプリケーションの構築に必要なすべてを提供します。

### 🚀 機能

- **マルチプロバイダー LLM クライアント** — OpenAI、Anthropic Claude、DeepSeek、Google Gemini に対応。ストリーミング（SSE）、コネクションプーリング、指数バックオフによる再試行をサポート
- **ベクターストア** — コサイン/ユークリッド/ドット積の類似度とメタデータフィルタリングを備えたインメモリ HNSW インデックス
- **エンベディング生成** — バッチ処理と Caffeine ベースのキャッシュを備えたマルチプロバイダーエンベディング
- **プロンプト管理** — 変数、条件分岐、ループ、バージョン管理を備えた Jinja2 風テンプレートエンジン
- **エージェントフレームワーク** — ツールレジストリとツールチェーン実行を備えた ReAct（Reasoning + Acting）エージェント
- **Spring Boot 統合** — `@ConfigurationProperties` による自動設定

### 📦 モジュール

| モジュール | 説明 | 主要クラス |
|--------|-------------|-------------|
| `aikit-core` | コアのインターフェースとモデル | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage`, `ChatRequest` |
| `aikit-llm` | HTTP/SSE 対応の LLM クライアント | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient`, `RetryPolicy` |
| `aikit-vector` | ベクターの保存と検索 | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity`, `EuclideanDistance` |
| `aikit-embed` | エンベディング生成 | `OpenAIEmbeddingProvider`, `EmbeddingCache`, `AbstractEmbeddingProvider` |
| `aikit-prompt` | プロンプトテンプレート | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | エージェントフレームワーク | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot 自動設定 | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 要件

- Java 17 以降
- Gradle 8.x（wrapper 同梱）

### 🔧 クイックスタート

**1. リポジトリをクローン**

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
```

**2. プロジェクトをビルド**

```bash
./gradlew build
```

**3. 依存関係として追加**

```groovy
// build.gradle
dependencies {
    implementation 'com.nousresearch.aikit:aikit-llm:1.0.0-SNAPSHOT'
    implementation 'com.nousresearch.aikit:aikit-vector:1.0.0-SNAPSHOT'
}
```

### 💡 使用例

#### LLM チャット

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

#### ベクター検索

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

#### プロンプトテンプレート

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

#### ツール付きエージェント

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

### 🧪 テストの実行

```bash
./gradlew test
```

### 📚 ドキュメント

- Javadoc: `./gradlew javadoc`
- 完全な API ドキュメントは生成された Javadoc で参照できます

### 🤝 コントリビューション

コードスタイル、PR、開発ワークフローに関するガイドラインは [CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。

### 📄 ライセンス

MIT ライセンス — 詳細は [LICENSE](LICENSE) を参照してください。

---

## 中国語

**AiKit Java** は、Java 17+ 向けの包括的な AI 開発ツールキットで、LLM チャットクライアントやベクター検索からプロンプトテンプレートやエージェントフレームワークまで、あらゆる機能を提供します。

### 🚀 機能

- **マルチプロバイダー LLM クライアント** — OpenAI、Anthropic Claude、DeepSeek、Google Gemini に対応。ストリーミング応答（SSE）、コネクションプール、指数バックオフ再試行を含む
- **ベクターストア** — HNSW ベースのインメモリベクターインデックス。コサイン/ユークリッド/ドット積類似度とメタデータフィルタリングに対応
- **エンベディング生成** — マルチプロバイダーエンベディング、バッチ処理、Caffeine ベースのキャッシュ
- **プロンプト管理** — Jinja2 風テンプレートエンジン。変数の置換、条件分岐、ループ、バージョン管理に対応
- **エージェントフレームワーク** — ReAct（推論＋行動）エージェント。ツール登録システムとツールチェーン実行を備える
- **Spring Boot 統合** — `@ConfigurationProperties` による自動設定

### 📦 モジュール

| モジュール | 説明 | 主要クラス |
|--------|-------------|-------------|
| `aikit-core` | コアのインターフェースとモデル | `LLMProvider`, `EmbeddingProvider`, `VectorStore`, `ChatMessage` |
| `aikit-llm` | LLM クライアント | `OpenAIClient`, `AnthropicClient`, `DeepSeekClient`, `GeminiClient` |
| `aikit-vector` | ベクターの保存と検索 | `InMemoryVectorStore`, `HnswIndex`, `CosineSimilarity` |
| `aikit-embed` | エンベディング生成 | `OpenAIEmbeddingProvider`, `EmbeddingCache` |
| `aikit-prompt` | プロンプトテンプレート | `PromptTemplate`, `PromptManager`, `PromptVersion` |
| `aikit-agent` | エージェントフレームワーク | `ReActAgent`, `Tool`, `ToolRegistry`, `ToolChain` |
| `aikit-spring` | Spring Boot 自動設定 | `AiKitAutoConfiguration`, `AiKitProperties` |

### 📋 要件

- Java 17 以降
- Gradle 8.x（wrapper 同梱）

### 🔧 クイックスタート

```bash
git clone https://github.com/nousresearch/aikit-java.git
cd aikit-java
./gradlew build
```

### 📄 ライセンス

MIT ライセンス — 詳細は [LICENSE](LICENSE) を参照してください。
