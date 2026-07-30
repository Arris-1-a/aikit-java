package com.nousresearch.aikit.prompt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nousresearch.aikit.prompt.template.PromptTemplate;
import com.nousresearch.aikit.prompt.version.PromptVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages prompt templates with versioning support.
 *
 * <p>Stores named prompts with multiple versions. Supports loading
 * prompts from YAML files and programmatic registration. Thread-safe.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * PromptManager pm = new PromptManager();
 * pm.register("greeting", "Hello {{ name }}!", "alice");
 * 
 * String result = pm.render("greeting", Map.of("name", "World"));
 * }</pre>
 */
public class PromptManager {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private final ConcurrentHashMap<String, List<PromptVersion>> prompts;

    /**
     * Creates an empty prompt manager.
     */
    public PromptManager() {
        this.prompts = new ConcurrentHashMap<>();
    }

    /**
     * Registers a new prompt template, creating version 1.
     *
     * @param name the prompt name
     * @param template the template string
     * @param createdBy creator identifier
     * @return the created PromptVersion
     */
    public PromptVersion register(String name, String template, String createdBy) {
        PromptVersion version = new PromptVersion(1, template, Instant.now(), createdBy, "Initial version");
        prompts.computeIfAbsent(name, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(version);
        return version;
    }

    /**
     * Adds a new version of an existing prompt.
     *
     * @param name the prompt name
     * @param template the new template string
     * @param createdBy creator identifier
     * @param description change description
     * @return the new PromptVersion
     * @throws IllegalArgumentException if the prompt doesn't exist
     */
    public PromptVersion addVersion(String name, String template, String createdBy, String description) {
        List<PromptVersion> versions = prompts.get(name);
        if (versions == null) {
            throw new IllegalArgumentException("Prompt not found: " + name);
        }
        int nextVersion = versions.size() + 1;
        PromptVersion version = new PromptVersion(nextVersion, template, Instant.now(), createdBy, description);
        versions.add(version);
        return version;
    }

    /**
     * Renders a prompt with the latest version.
     *
     * @param name the prompt name
     * @param variables the variable bindings
     * @return the rendered string
     */
    public String render(String name, Map<String, Object> variables) {
        PromptTemplate template = getTemplate(name);
        if (template == null) {
            throw new IllegalArgumentException("Prompt not found: " + name);
        }
        return template.render(variables);
    }

    /**
     * Renders a specific version of a prompt.
     *
     * @param name the prompt name
     * @param version the version number
     * @param variables the variable bindings
     * @return the rendered string
     */
    public String renderVersion(String name, int version, Map<String, Object> variables) {
        PromptVersion pv = getVersion(name, version);
        if (pv == null) {
            throw new IllegalArgumentException(
                    "Prompt version not found: " + name + " v" + version);
        }
        return PromptTemplate.compile(pv.getTemplate()).render(variables);
    }

    /**
     * Gets the compiled template for the latest version.
     *
     * @param name the prompt name
     * @return the compiled template, or null
     */
    public PromptTemplate getTemplate(String name) {
        List<PromptVersion> versions = prompts.get(name);
        if (versions == null || versions.isEmpty()) return null;
        PromptVersion latest = versions.get(versions.size() - 1);
        return PromptTemplate.compile(latest.getTemplate());
    }

    /**
     * Gets a specific version.
     *
     * @param name the prompt name
     * @param version the version number (1-based)
     * @return the PromptVersion, or null
     */
    public PromptVersion getVersion(String name, int version) {
        List<PromptVersion> versions = prompts.get(name);
        if (versions == null || version < 1 || version > versions.size()) {
            return null;
        }
        return versions.get(version - 1);
    }

    /**
     * Returns all versions of a prompt.
     *
     * @param name the prompt name
     * @return list of versions, or empty list
     */
    public List<PromptVersion> getVersions(String name) {
        List<PromptVersion> versions = prompts.get(name);
        return versions != null ? Collections.unmodifiableList(versions) : Collections.emptyList();
    }

    /**
     * Returns the latest version number for a prompt.
     *
     * @param name the prompt name
     * @return the version number, or 0 if not found
     */
    public int getLatestVersion(String name) {
        List<PromptVersion> versions = prompts.get(name);
        return versions != null ? versions.size() : 0;
    }

    /**
     * Removes a prompt and all its versions.
     *
     * @param name the prompt name
     * @return true if removed
     */
    public boolean remove(String name) {
        return prompts.remove(name) != null;
    }

    /**
     * Returns all prompt names.
     *
     * @return unmodifiable set of prompt names
     */
    public java.util.Set<String> getPromptNames() {
        return Collections.unmodifiableSet(prompts.keySet());
    }

    /** @return total number of prompts */
    public int size() { return prompts.size(); }

    /**
     * Loads prompts from a YAML file.
     *
     * <p>Expected format:</p>
     * <pre>{@code
     * prompts:
     *   greeting:
     *     template: "Hello {{ name }}!"
     *     versions:
     *       - version: 1
     *         template: "Hi {{ name }}!"
     *         created_by: alice
     * }</pre>
     *
     * @param path the YAML file path
     * @throws IOException if the file cannot be read
     */
    @SuppressWarnings("unchecked")
    public void loadFromYaml(String path) throws IOException {
        String content = Files.readString(Paths.get(path));
        Map<String, Object> root = YAML_MAPPER.readValue(content,
                new TypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> promptList =
                (List<Map<String, Object>>) root.get("prompts");
        if (promptList == null) return;

        for (Map<String, Object> entry : promptList) {
            String name = (String) entry.get("name");
            String template = (String) entry.get("template");

            if (name != null && template != null) {
                List<Map<String, Object>> versions =
                        (List<Map<String, Object>>) entry.get("versions");
                if (versions != null) {
                    for (Map<String, Object> v : versions) {
                        Integer ver = (Integer) v.get("version");
                        String tpl = (String) v.get("template");
                        String by = (String) v.get("created_by");
                        String desc = (String) v.get("description");
                        if (ver != null && tpl != null) {
                            prompts.computeIfAbsent(name, k ->
                                    Collections.synchronizedList(new ArrayList<>()))
                                    .add(new PromptVersion(ver, tpl, null, by, desc));
                        }
                    }
                } else {
                    register(name, template, "yaml-import");
                }
            }
        }
    }

    /**
     * Exports all prompts to a map suitable for YAML serialization.
     */
    public Map<String, Object> toExportMap() {
        List<Map<String, Object>> promptList = new ArrayList<>();
        for (Map.Entry<String, List<PromptVersion>> entry : prompts.entrySet()) {
            Map<String, Object> promptMap = new LinkedHashMap<>();
            promptMap.put("name", entry.getKey());
            List<PromptVersion> versions = entry.getValue();
            if (!versions.isEmpty()) {
                promptMap.put("template", versions.get(versions.size() - 1).getTemplate());
            }
            List<Map<String, Object>> versionList = new ArrayList<>();
            for (PromptVersion v : versions) {
                Map<String, Object> vMap = new LinkedHashMap<>();
                vMap.put("version", v.getVersion());
                vMap.put("template", v.getTemplate());
                vMap.put("created_by", v.getCreatedBy());
                vMap.put("created_at", v.getCreatedAt().toString());
                if (v.getDescription() != null) {
                    vMap.put("description", v.getDescription());
                }
                versionList.add(vMap);
            }
            promptMap.put("versions", versionList);
            promptList.add(promptMap);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("prompts", promptList);
        return root;
    }
}
