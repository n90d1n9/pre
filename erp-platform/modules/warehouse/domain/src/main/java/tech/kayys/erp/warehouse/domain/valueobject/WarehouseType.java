package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Types of warehouses.
 */
public enum WarehouseType {
    DISTRIBUTION_CENTER("Distribution Center"),
    FULFILLMENT_CENTER("Fulfillment Center"),
    RETURNS_CENTER("Returns Center"),
    RETAIL_STORE("Retail Store"),
    DROP_SHIP("Drop Ship"),
    CROSS_DOCK("Cross Dock"),
    MANUFACTURING("Manufacturing"),
    COLD_STORAGE("Cold Storage"),
    HAZMAT("Hazardous Materials");

    private final String displayName;

    WarehouseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}