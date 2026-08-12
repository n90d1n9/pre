package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Warehouse operation types.
 */
public enum OperationType {
    RECEIVING("Receiving"),
    PUTAWAY("Putaway"),
    PICKING("Picking"),
    PACKING("Packing"),
    SHIPPING("Shipping"),
    TRANSFER("Transfer"),
    RETURNS("Returns"),
    DAMAGE("Damage"),
    REWORK("Rework"),
    REPLENISHMENT("Replenishment");

    private final String displayName;

    OperationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}