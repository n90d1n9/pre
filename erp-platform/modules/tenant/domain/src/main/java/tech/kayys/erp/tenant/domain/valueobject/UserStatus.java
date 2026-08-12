package tech.kayys.erp.tenant.domain.valueobject;

/**
 * Status of a user.
 */
public enum UserStatus {
    ACTIVE("Active - user can login"),
    INACTIVE("Inactive - user cannot login"),
    PENDING("Pending - waiting for activation"),
    LOCKED("Locked - account locked"),
    SUSPENDED("Suspended - temporarily disabled"),
    DELETED("Deleted - account removed");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean canTransitionTo(UserStatus target) {
        return switch (this) {
            case PENDING -> target == ACTIVE || target == DELETED;
            case ACTIVE -> target == INACTIVE || target == LOCKED || target == SUSPENDED || target == DELETED;
            case INACTIVE -> target == ACTIVE || target == DELETED;
            case LOCKED -> target == ACTIVE || target == DELETED;
            case SUSPENDED -> target == ACTIVE || target == DELETED;
            case DELETED -> false;
        };
    }
}