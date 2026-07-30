package com.nousresearch.aikit.prompt.version;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a versioned prompt template.
 *
 * <p>Each version has a numeric version number, creation timestamp,
 * author, and optional change description for audit trails.</p>
 */
public class PromptVersion {

    private final int version;
    private final String template;
    private final Instant createdAt;
    private final String createdBy;
    private final String description;

    /**
     * Creates a new prompt version.
     */
    public PromptVersion(@JsonProperty("version") int version,
                         @JsonProperty("template") String template,
                         @JsonProperty("created_at") Instant createdAt,
                         @JsonProperty("created_by") String createdBy,
                         @JsonProperty("description") String description) {
        this.version = version;
        this.template = Objects.requireNonNull(template, "template must not be null");
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.createdBy = createdBy != null ? createdBy : "unknown";
        this.description = description;
    }

    /** @return version number */
    public int getVersion() { return version; }

    /** @return the template string */
    public String getTemplate() { return template; }

    /** @return creation timestamp */
    public Instant getCreatedAt() { return createdAt; }

    /** @return creator identifier */
    public String getCreatedBy() { return createdBy; }

    /** @return change description */
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromptVersion)) return false;
        PromptVersion that = (PromptVersion) o;
        return version == that.version;
    }

    @Override
    public int hashCode() { return Objects.hash(version); }

    @Override
    public String toString() {
        return "PromptVersion{v" + version + ", created=" + createdAt + "}";
    }
}
