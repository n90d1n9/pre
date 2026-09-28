package com.saas.product.spi;

/**
 * Marker + identity contract for all product domain extensions.
 *
 * Each domain (e-commerce, FnB, subscription, healthcare, etc.) implements
 * this interface to attach context-specific data to a ProductAggregate without
 * polluting the core model.
 *
 * Rules:
 *  - Must be immutable (return new instances on modification)
 *  - Must implement equals/hashCode based on logical identity
 *  - getContext() must return a stable, lowercase, hyphen-separated key
 *    e.g. "ecommerce", "fnb", "subscription", "pharmacy"
 */
public interface ProductExtension {

    /**
     * Unique stable key identifying this extension type.
     * Used as the map key in ProductAggregate.extensions.
     */
    String getContext();

    /**
     * Human-readable description for tooling, admin UI, and AI agents.
     */
    default String describe() {
        return "Extension[" + getContext() + "]";
    }
}
