package tech.kayys.erp.asset.domain.valueobject;

/**
 * Status of an asset.
 */
public enum AssetStatus {
    ACTIVE("Active - in use"),
    INACTIVE("Inactive - not in use"),
    MAINTENANCE("Maintenance - being repaired"),
    DEPRECIATED("Depreciated - fully depreciated"),
    DISPOSED("Disposed - removed"),
    LOST("Lost - missing"),
    STOLEN("Stolen"),
    DAMAGED("Damaged"),
    UNDER_REPAIR("Under Repair"),
    RESERVED("Reserved - allocated");

    private final String description;

    AssetStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }

    public boolean isActive() {
        return this == ACTIVE || this == RESERVED;
    }

    public boolean isTerminal() {
        return this == DISPOSED || this == LOST || this == STOLEN;
    }
}