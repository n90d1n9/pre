package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of a putaway task.
 */
public enum PutawayStatus {
    CREATED("Created - task generated"),
    ASSIGNED("Assigned - worker assigned"),
    IN_PROGRESS("In Progress - putaway underway"),
    PARTIALLY_COMPLETED("Partially Completed - some items put away"),
    COMPLETED("Completed - all items put away"),
    CANCELLED("Cancelled - task voided"),
    ON_HOLD("On Hold - temporarily paused"),
    FAILED("Failed - putaway failed");

    private final String description;

    PutawayStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == CREATED || this == ASSIGNED || this == IN_PROGRESS || 
               this == PARTIALLY_COMPLETED || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }

    public boolean canTransitionTo(PutawayStatus target) {
        return switch (this) {
            case CREATED -> target == ASSIGNED || target == CANCELLED;
            case ASSIGNED -> target == IN_PROGRESS || target == CANCELLED || target == ON_HOLD;
            case IN_PROGRESS -> target == PARTIALLY_COMPLETED || target == COMPLETED || target == CANCELLED || target == ON_HOLD || target == FAILED;
            case PARTIALLY_COMPLETED -> target == IN_PROGRESS || target == COMPLETED || target == CANCELLED;
            case ON_HOLD -> target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED, CANCELLED, FAILED -> false;
        };
    }
}