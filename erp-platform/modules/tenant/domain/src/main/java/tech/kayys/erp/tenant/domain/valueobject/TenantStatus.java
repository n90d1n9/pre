package tech.kayys.erp.tenant.domain.valueobject;

/**
 * Status of a tenant.
 */
public enum TenantStatus {
    ACTIVE("Active - tenant is operational"),
    INACTIVE("Inactive - tenant is disabled"),
    SUSPENDED("Suspended - tenant has been suspended"),
    PENDING("Pending - waiting for activation"),
    EXPIRED("Expired - subscription ended"),
    DELETED("Deleted - tenant marked for removal");

    private final String description;

    TenantStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PENDING;
    }

    public boolean canTransitionTo(TenantStatus target) {
        return switch (this) {
            case PENDING -> target == ACTIVE || target == SUSPENDED || target == DELETED;
            case ACTIVE -> target == INACTIVE || target == SUSPENDED || target == EXPIRED;
            case INACTIVE -> target == ACTIVE || target == DELETED;
            case SUSPENDED -> target == ACTIVE || target == EXPIRED || target == DELETED;
            case EXPIRED -> target == ACTIVE || target == DELETED;
            case DELETED -> false;
        };
    }
}