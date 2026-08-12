package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of inventory movement.
 */
public enum MovementStatus {
    CREATED("Created - movement created"),
    IN_TRANSIT("In Transit - inventory in transit"),
    COMPLETED("Completed - movement finished"),
    CANCELLED("Cancelled - movement cancelled"),
    ON_HOLD("On Hold - temporarily paused"),
    FAILED("Failed - movement failed");

    private final String description;

    MovementStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == CREATED || this == IN_TRANSIT || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }

    public boolean canTransitionTo(MovementStatus target) {
        return switch (this) {
            case CREATED -> target == IN_TRANSIT || target == CANCELLED;
            case IN_TRANSIT -> target == COMPLETED || target == CANCELLED || target == FAILED || target == ON_HOLD;
            case ON_HOLD -> target == IN_TRANSIT || target == CANCELLED;
            case COMPLETED, CANCELLED, FAILED -> false;
        };
    }
}