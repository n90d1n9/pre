package tech.kayys.erp.catalog.domain.valueobject;

/**
 * Status of a product in the Catalog.
 */
public enum ProductStatus {
    DRAFT("Draft - not yet published"),
    ACTIVE("Active - available for sale"),
    INACTIVE("Inactive - temporarily unavailable"),
    DISCONTINUED("Discontinued - no longer sold");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canActivate() {
        return this == DRAFT || this == INACTIVE;
    }

    public boolean canDeactivate() {
        return this == ACTIVE;
    }
}
