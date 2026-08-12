package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Putaway strategies.
 */
public enum PutawayStrategy {
    NEAREST("Nearest - closest bin"),
    EMPTY_BIN("Empty Bin - first available empty bin"),
    PARTIAL_BIN("Partial Bin - partially filled bin"),
    DEDICATED("Dedicated - dedicated bin per product"),
    RANDOM("Random - random bin assignment"),
    OPTIMIZED("Optimized - best fit based on product characteristics"),
    ZONE("Zone - zone-based putaway"),
    CROSS_DOCK("Cross Dock - immediate cross-docking");

    private final String description;

    PutawayStrategy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAutomated() {
        return this == OPTIMIZED || this == NEAREST || this == EMPTY_BIN;
    }

    public boolean requiresSorting() {
        return this == ZONE || this == DEDICATED;
    }
}