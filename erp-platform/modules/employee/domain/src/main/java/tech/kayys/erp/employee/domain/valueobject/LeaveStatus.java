package tech.kayys.erp.employee.domain.valueobject;

/**
 * Status of a leave request.
 */
public enum LeaveStatus {
    PENDING("Pending - awaiting approval"),
    APPROVED("Approved - leave granted"),
    REJECTED("Rejected - leave denied"),
    CANCELLED("Cancelled - request withdrawn"),
    TAKEN("Taken - leave consumed");

    private final String description;

    LeaveStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED || this == TAKEN;
    }

    public boolean canTransitionTo(LeaveStatus target) {
        return switch (this) {
            case PENDING -> target == APPROVED || target == REJECTED || target == CANCELLED;
            case APPROVED -> target == TAKEN || target == CANCELLED;
            default -> false;
        };
    }
}