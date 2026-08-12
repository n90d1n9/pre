package tech.kayys.erp.workflow.domain.valueobject;

/**
 * Status of a workflow instance.
 */
public enum WorkflowStatus {
    DRAFT("Draft - being created"),
    ACTIVE("Active - running"),
    PAUSED("Paused - temporarily stopped"),
    COMPLETED("Completed - finished successfully"),
    CANCELLED("Cancelled - terminated"),
    FAILED("Failed - error occurred"),
    ON_HOLD("On Hold - waiting for intervention");

    private final String description;

    WorkflowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PAUSED || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }

    public boolean canTransitionTo(WorkflowStatus target) {
        return switch (this) {
            case DRAFT -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == PAUSED || target == COMPLETED || target == CANCELLED || target == FAILED || target == ON_HOLD;
            case PAUSED -> target == ACTIVE || target == CANCELLED || target == COMPLETED;
            case ON_HOLD -> target == ACTIVE || target == CANCELLED;
            case COMPLETED, CANCELLED, FAILED -> false;
        };
    }
}