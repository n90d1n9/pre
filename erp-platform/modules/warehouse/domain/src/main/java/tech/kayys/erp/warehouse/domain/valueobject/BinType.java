package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Types of bin locations.
 */
public enum BinType {
    PALLET_RACK("Pallet Rack"),
    SHELF("Shelf"),
    BIN("Bin"),
    BULK("Bulk Storage"),
    FLOOR("Floor Storage"),
    CAGE("Cage"),
    REFRIGERATED("Refrigerated"),
    HAZMAT("Hazmat Storage"),
    PICK_FACE("Pick Face"),
    RESERVE("Reserve Storage"),
    RECEIVING("Receiving Area"),
    SHIPPING("Shipping Area"),
    DAMAGED("Damaged Goods"),
    RETURNS("Returns Area"),
    CONVEYOR("Conveyor"),
    MEZZANINE("Mezzanine");

    private final String displayName;

    BinType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isStorage() {
        return this != RECEIVING && this != SHIPPING && this != DAMAGED && this != RETURNS && this != CONVEYOR;
    }

    public boolean isRestricted() {
        return this == HAZMAT || this == REFRIGERATED;
    }
}