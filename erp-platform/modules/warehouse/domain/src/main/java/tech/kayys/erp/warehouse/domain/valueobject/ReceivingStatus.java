package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of a receiving task.
 */
public enum ReceivingStatus {
    EXPECTED("Expected - shipment expected"),
    ARRIVED("Arrived - shipment arrived"),
    IN_QUALITY_CHECK("In Quality Check - being inspected"),
    PASSED_QUALITY("Passed Quality - quality check passed"),
    FAILED_QUALITY("Failed Quality - quality check failed"),
    PARTIALLY_RECEIVED("Partially Received - some items received"),
    COMPLETED("Completed - fully received"),
    CANCELLED("Cancelled - receiving cancelled"),
    ON_HOLD("On Hold - receiving paused");

    private final String description;

    ReceivingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == EXPECTED || this == ARRIVED || this == IN_QUALITY_CHECK || 
               this == PASSED_QUALITY || this == PARTIALLY_RECEIVED || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED_QUALITY;
    }

    public boolean canTransitionTo(ReceivingStatus target) {
        return switch (this) {
            case EXPECTED -> target == ARRIVED || target == CANCELLED;
            case ARRIVED -> target == IN_QUALITY_CHECK || target == PARTIALLY_RECEIVED || target == CANCELLED || target == ON_HOLD;
            case IN_QUALITY_CHECK -> target == PASSED_QUALITY || target == FAILED_QUALITY || target == CANCELLED || target == ON_HOLD;
            case PASSED_QUALITY -> target == PARTIALLY_RECEIVED || target == COMPLETED || target == CANCELLED;
            case PARTIALLY_RECEIVED -> target == COMPLETED || target == CANCELLED;
            case FAILED_QUALITY, ON_HOLD -> target == ARRIVED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}