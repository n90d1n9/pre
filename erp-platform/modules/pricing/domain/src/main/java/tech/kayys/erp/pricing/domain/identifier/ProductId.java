package tech.kayys.erp.pricing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Product identifier in the Pricing context.
 * Represents a product from Catalog context.
 */
public final class ProductId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ProductId(UUID value) {
        super(value);
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId fromString(String value) {
        return new ProductId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ProductId{" + value + "}";
    }
}