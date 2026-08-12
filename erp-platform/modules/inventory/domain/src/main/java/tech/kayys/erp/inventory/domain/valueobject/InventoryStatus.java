package tech.kayys.erp.inventory.domain.valueobject;

/**
 * Status of inventory items.
 */
public enum InventoryStatus {
    AVAILABLE("Available - ready for sale"),
    RESERVED("Reserved - allocated to order"),
    BACKORDERED("Backordered - awaiting restock"),
    DISCONTINUED("Discontinued - no longer sold"),
    DAMAGED("Damaged - not sellable"),
    RECALLED("Recalled - withdrawn from sale"),
    OUT_OF_STOCK("Out of Stock - currently unavailable");

    private final String description;

    InventoryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return this == AVAILABLE || this == BACKORDERED;
    }

    public boolean isSellable() {
        return this == AVAILABLE || this == BACKORDERED || this == RESERVED;
    }
}