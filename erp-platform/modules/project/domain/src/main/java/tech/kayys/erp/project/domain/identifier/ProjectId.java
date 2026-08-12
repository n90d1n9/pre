package tech.kayys.erp.project.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Project identifier.
 */
public final class ProjectId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ProjectId(UUID value) {
        super(value);
    }

    public static ProjectId of(UUID value) {
        return new ProjectId(value);
    }

    public static ProjectId generate() {
        return new ProjectId(UUID.randomUUID());
    }

    public static ProjectId fromString(String value) {
        return new ProjectId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ProjectId{" + value + "}";
    }
}