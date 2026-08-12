package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Status of a purchase order.
 */
public enum PurchaseOrderStatus {
    DRAFT("Draft - being created"),
    SUBMITTED("Submitted - sent to vendor"),
    ACKNOWLEDGED("Acknowledged - vendor accepted"),
    IN_TRANSIT("In Transit - items being shipped"),
    PARTIALLY_RECEIVED("Partially Received - some items received"),
    RECEIVED("Received - all items received"),
    COMPLETED("Completed - order closed"),
    CANCELLED("Cancelled - order voided"),
    REJECTED("Rejected - vendor rejected"),
    ON_HOLD("On Hold - pending approval");

    private final String description;

    PurchaseOrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(PurchaseOrderStatus target) {
        return switch (this) {
            case DRAFT -> target == SUBMITTED || target == CANCELLED || target == ON_HOLD;
            case ON_HOLD -> target == DRAFT || target == CANCELLED;
            case SUBMITTED -> target == ACKNOWLEDGED || target == REJECTED || target == CANCELLED;
            case ACKNOWLEDGED -> target == IN_TRANSIT || target == CANCELLED || target == REJECTED;
            case IN_TRANSIT -> target == PARTIALLY_RECEIVED || target == RECEIVED || target == CANCELLED;
            case PARTIALLY_RECEIVED -> target == RECEIVED || target == CANCELLED;
            case RECEIVED -> target == COMPLETED;
            case COMPLETED, CANCELLED, REJECTED -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED;
    }

    public boolean isActive() {
        return this != COMPLETED && this != CANCELLED && this != REJECTED;
    }

    public boolean isReceivable() {
        return this == ACKNOWLEDGED || this == IN_TRANSIT || this == PARTIALLY_RECEIVED;
    }
}