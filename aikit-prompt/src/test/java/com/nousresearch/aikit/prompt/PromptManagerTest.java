package com.nousresearch.aikit.prompt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class PromptManagerTest {

    private PromptManager manager;

    @BeforeEach
    void setUp() {
        manager = new PromptManager();
    }

    @Test
    void shouldRegisterAndRenderPrompt() {
        manager.register("greeting", "Hello {{ name }}!", "alice");
        String result = manager.render("greeting", Map.of("name", "Bob"));
        assertThat(result).isEqualTo("Hello Bob!");
    }

    @Test
    void shouldSupportVersioning() {
        manager.register("greeting", "Hi {{ name }}!", "alice");
        manager.addVersion("greeting", "Hello {{ name }}!", "bob",
                "More formal greeting");

        assertThat(manager.getLatestVersion("greeting")).isEqualTo(2);

        String v1 = manager.renderVersion("greeting", 1, Map.of("name", "X"));
        String v2 = manager.renderVersion("greeting", 2, Map.of("name", "X"));

        assertThat(v1).isEqualTo("Hi X!");
        assertThat(v2).isEqualTo("Hello X!");
    }

    @Test
    void shouldListPromptNames() {
        manager.register("a", "{{ x }}", "u");
        manager.register("b", "{{ y }}", "u");
        assertThat(manager.getPromptNames()).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    void shouldRemovePrompt() {
        manager.register("test", "{{ x }}", "u");
        assertThat(manager.size()).isEqualTo(1);
        manager.remove("test");
        assertThat(manager.size()).isEqualTo(0);
    }
}
