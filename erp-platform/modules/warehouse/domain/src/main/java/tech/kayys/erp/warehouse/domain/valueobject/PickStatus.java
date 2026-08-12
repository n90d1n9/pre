package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of a pick list.
 */
public enum PickStatus {
    CREATED("Created - pick list generated"),
    ASSIGNED("Assigned - picker assigned"),
    IN_PROGRESS("In Progress - picking underway"),
    PARTIALLY_PICKED("Partially Picked - some items picked"),
    COMPLETED("Completed - all items picked"),
    CANCELLED("Cancelled - pick list voided"),
    ON_HOLD("On Hold - temporarily paused");

    private final String description;

    PickStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == CREATED || this == ASSIGNED || this == IN_PROGRESS || 
               this == PARTIALLY_PICKED || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(PickStatus target) {
        return switch (this) {
            case CREATED -> target == ASSIGNED || target == CANCELLED;
            case ASSIGNED -> target == IN_PROGRESS || target == CANCELLED || target == ON_HOLD;
            case IN_PROGRESS -> target == PARTIALLY_PICKED || target == COMPLETED || target == CANCELLED || target == ON_HOLD;
            case PARTIALLY_PICKED -> target == IN_PROGRESS || target == COMPLETED || target == CANCELLED;
            case ON_HOLD -> target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}