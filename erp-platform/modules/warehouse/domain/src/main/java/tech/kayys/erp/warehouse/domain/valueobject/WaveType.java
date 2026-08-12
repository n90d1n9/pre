package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Types of waves.
 */
public enum WaveType {
    PICKING("Picking Wave"),
    PACKING("Packing Wave"),
    SHIPPING("Shipping Wave"),
    CROSS_DOCK("Cross-Docking Wave"),
    REPLENISHMENT("Replenishment Wave");

    private final String description;

    WaveType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}