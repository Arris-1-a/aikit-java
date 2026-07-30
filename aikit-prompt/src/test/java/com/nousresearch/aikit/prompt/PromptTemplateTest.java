package com.nousresearch.aikit.prompt;

import com.nousresearch.aikit.prompt.template.PromptTemplate;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class PromptTemplateTest {

    @Test
    void shouldSubstituteVariables() {
        String result = PromptTemplate.render(
                "Hello {{ name }}!", Map.of("name", "World"));
        assertThat(result).isEqualTo("Hello World!");
    }

    @Test
    void shouldHandleMultipleVariables() {
        String result = PromptTemplate.render(
                "{{ greeting }} {{ name }}! Your score is {{ score }}.",
                Map.of("greeting", "Hello", "name", "Alice", "score", 95));
        assertThat(result).isEqualTo("Hello Alice! Your score is 95.");
    }

    @Test
    void shouldProcessIfBlocksTrue() {
        String tmpl = "{% if show_greeting %}Hello!{% endif %}World";
        String result = PromptTemplate.render(tmpl,
                Map.of("show_greeting", true));
        assertThat(result).isEqualTo("Hello!World");
    }

    @Test
    void shouldProcessIfBlocksFalse() {
        String tmpl = "{% if show_greeting %}Hello!{% endif %}World";
        String result = PromptTemplate.render(tmpl,
                Map.of("show_greeting", false));
        assertThat(result).isEqualTo("World");
    }

    @Test
    void shouldProcessForLoops() {
        String tmpl = "Items: {% for item in items %}{{ item }}, {% endfor %}";
        String result = PromptTemplate.compile(tmpl)
                .render(Map.of("items", List.of("a", "b", "c")));
        assertThat(result).isEqualTo("Items: a, b, c, ");
    }

    @Test
    void shouldRemoveComments() {
        String result = PromptTemplate.render(
                "Hello {# this is a comment #}World", Map.of());
        assertThat(result).isEqualTo("Hello World");
    }

    @Test
    void shouldHandleMissingVariableGracefully() {
        String result = PromptTemplate.render(
                "Hello {{ missing }}!", Map.of());
        assertThat(result).isEqualTo("Hello !");
    }

    @Test
    void shouldExtractVariables() {
        PromptTemplate tmpl = PromptTemplate.compile(
                "{{ name }} likes {{ food }} and {{ food }}");
        assertThat(tmpl.getVariables()).containsExactly("name", "food");
    }

    @Test
    void shouldHandleConditionalNegation() {
        String tmpl = "{% if not empty %}content{% endif %}";
        String result = PromptTemplate.render(tmpl,
                Map.of("empty", false));
        assertThat(result).isEqualTo("content");
    }

    @Test
    void shouldHandleEqualityCheck() {
        String tmpl = "{% if status == active %}Active{% endif %}";
        String result = PromptTemplate.compile(tmpl)
                .render(Map.of("status", "active"));
        assertThat(result).isEqualTo("Active");
    }
}
