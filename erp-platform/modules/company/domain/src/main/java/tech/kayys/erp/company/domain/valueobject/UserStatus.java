package tech.kayys.erp.company.domain.valueobject;

/**
 * Status of a user.
 */
public enum UserStatus {
    ACTIVE("Active - can access system"),
    INACTIVE("Inactive - temporarily disabled"),
    LOCKED("Locked - account locked"),
    PENDING_VERIFICATION("Pending Verification - waiting for email verification"),
    SUSPENDED("Suspended - under review");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAccessible() {
        return this == ACTIVE;
    }

    public boolean canTransitionTo(UserStatus target) {
        return switch (this) {
            case ACTIVE -> target == INACTIVE || target == SUSPENDED;
            case INACTIVE -> target == ACTIVE || target == SUSPENDED;
            case LOCKED -> target == ACTIVE || target == SUSPENDED;
            case PENDING_VERIFICATION -> target == ACTIVE || target == SUSPENDED;
            case SUSPENDED -> target == ACTIVE || target == INACTIVE || target == LOCKED;
        };
    }
}