package tech.kayys.erp.project.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Resource identifier.
 */
public final class ResourceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ResourceId(UUID value) {
        super(value);
    }

    public static ResourceId of(UUID value) {
        return new ResourceId(value);
    }

    public static ResourceId generate() {
        return new ResourceId(UUID.randomUUID());
    }

    public static ResourceId fromString(String value) {
        return new ResourceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ResourceId{" + value + "}";
    }
}