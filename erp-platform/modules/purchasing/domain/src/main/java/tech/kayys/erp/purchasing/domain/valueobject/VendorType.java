package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Types of vendors.
 */
public enum VendorType {
    SUPPLIER("Supplier"),
    MANUFACTURER("Manufacturer"),
    DISTRIBUTOR("Distributor"),
    SERVICE_PROVIDER("Service Provider"),
    CONSULTANT("Consultant"),
    CONTRACTOR("Contractor");

    private final String displayName;

    VendorType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}