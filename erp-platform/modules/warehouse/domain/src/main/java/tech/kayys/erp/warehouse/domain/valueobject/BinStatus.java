package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of a bin location.
 */
public enum BinStatus {
    ACTIVE("Active - available for use"),
    FULL("Full - at capacity"),
    MAINTENANCE("Maintenance - temporarily unavailable"),
    RESERVED("Reserved - allocated for specific use"),
    INACTIVE("Inactive - permanently unavailable"),
    DAMAGED("Damaged - needs repair");

    private final String description;

    BinStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return this == ACTIVE;
    }

    public boolean canStore() {
        return this == ACTIVE && this != FULL && this != MAINTENANCE && this != INACTIVE && this != DAMAGED;
    }
}