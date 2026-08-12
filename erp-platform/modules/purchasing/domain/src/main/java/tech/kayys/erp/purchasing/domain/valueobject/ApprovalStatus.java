package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Purchase order approval status.
 */
public enum ApprovalStatus {
    PENDING("Pending - Awaiting approval"),
    APPROVED("Approved - PO approved"),
    REJECTED("Rejected - PO rejected"),
    CANCELLED("Cancelled - PO cancelled"),
    IN_REVIEW("In Review - Under review");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    public boolean canTransitionTo(ApprovalStatus target) {
        return switch (this) {
            case PENDING -> target == APPROVED || target == REJECTED || target == IN_REVIEW;
            case IN_REVIEW -> target == APPROVED || target == REJECTED;
            case APPROVED, REJECTED, CANCELLED -> false;
        };
    }
}