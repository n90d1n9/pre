package tech.kayys.erp.asset.domain.valueobject;

/**
 * Types of assets.
 */
public enum AssetType {
    BUILDING("Building"),
    LAND("Land"),
    VEHICLE("Vehicle"),
    MACHINERY("Machinery"),
    EQUIPMENT("Equipment"),
    FURNITURE("Furniture"),
    COMPUTER("Computer"),
    SOFTWARE("Software"),
    INTANGIBLE("Intangible Asset"),
    LEASEHOLD("Leasehold Improvement"),
    INFRASTRUCTURE("Infrastructure"),
    OTHER("Other");

    private final String displayName;

    AssetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isTangible() {
        return this != INTANGIBLE && this != SOFTWARE;
    }

    public boolean isRealEstate() {
        return this == BUILDING || this == LAND;
    }
}