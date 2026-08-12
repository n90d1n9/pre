package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Status of a vendor.
 */
public enum VendorStatus {
    ACTIVE("Active - approved vendor"),
    INACTIVE("Inactive - currently not used"),
    BLACKLISTED("Blacklisted - not allowed to transact"),
    PENDING_APPROVAL("Pending Approval - awaiting approval"),
    UNDER_REVIEW("Under Review - being evaluated");

    private final String description;

    VendorStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransact() {
        return this == ACTIVE;
    }
}