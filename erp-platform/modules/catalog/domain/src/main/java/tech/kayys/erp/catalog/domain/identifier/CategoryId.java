package tech.kayys.erp.catalog.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Category identifier.
 */
public final class CategoryId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CategoryId(UUID value) {
        super(value);
    }

    public static CategoryId of(UUID value) {
        return new CategoryId(value);
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId fromString(String value) {
        return new CategoryId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CategoryId{" + value + "}";
    }
}