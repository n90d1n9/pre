package tech.kayys.erp.catalog.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Product identifier in the Catalog bounded context.
 */
public final class ProductId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ProductId(UUID value) {
        super(value);
    }

    @Override
    public String toString() {
        return "ProductId{" + value + "}";
    }

    /**
     * Static factory for creating a ProductId.
     */
    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    /**
     * Generates a random ProductId.
     */
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    /**
     * Creates a ProductId from a string representation.
     */
    public static ProductId fromString(String value) {
        return new ProductId(UUID.fromString(value));
    }
}
