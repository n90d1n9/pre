package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of a wave.
 */
public enum WaveStatus {
    CREATED("Created - wave generated"),
    PLANNED("Planned - wave scheduled"),
    IN_PROGRESS("In Progress - wave processing"),
    PARTIALLY_COMPLETED("Partially Completed - some tasks done"),
    COMPLETED("Completed - wave finished"),
    CANCELLED("Cancelled - wave voided"),
    ON_HOLD("On Hold - temporarily paused");

    private final String description;

    WaveStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == CREATED || this == PLANNED || this == IN_PROGRESS || 
               this == PARTIALLY_COMPLETED || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(WaveStatus target) {
        return switch (this) {
            case CREATED -> target == PLANNED || target == CANCELLED;
            case PLANNED -> target == IN_PROGRESS || target == CANCELLED || target == ON_HOLD;
            case IN_PROGRESS -> target == PARTIALLY_COMPLETED || target == COMPLETED || target == CANCELLED || target == ON_HOLD;
            case PARTIALLY_COMPLETED -> target == IN_PROGRESS || target == COMPLETED || target == CANCELLED;
            case ON_HOLD -> target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}