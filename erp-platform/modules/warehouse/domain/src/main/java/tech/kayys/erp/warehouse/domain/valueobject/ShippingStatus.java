package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of a shipping task.
 */
public enum ShippingStatus {
    CREATED("Created - task generated"),
    PACKING("Packing - items being packed"),
    READY_TO_SHIP("Ready to Ship - packed and labeled"),
    ASSIGNED("Assigned - carrier assigned"),
    IN_TRANSIT("In Transit - shipped"),
    DELIVERED("Delivered - confirmed delivered"),
    CANCELLED("Cancelled - shipping cancelled"),
    ON_HOLD("On Hold - temporarily paused"),
    PARTIALLY_SHIPPED("Partially Shipped - some items shipped");

    private final String description;

    ShippingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == CREATED || this == PACKING || this == READY_TO_SHIP || 
               this == ASSIGNED || this == ON_HOLD || this == PARTIALLY_SHIPPED;
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean isShipped() {
        return this == IN_TRANSIT || this == DELIVERED;
    }

    public boolean canTransitionTo(ShippingStatus target) {
        return switch (this) {
            case CREATED -> target == PACKING || target == CANCELLED;
            case PACKING -> target == READY_TO_SHIP || target == CANCELLED || target == ON_HOLD;
            case READY_TO_SHIP -> target == ASSIGNED || target == CANCELLED || target == ON_HOLD;
            case ASSIGNED -> target == IN_TRANSIT || target == CANCELLED || target == ON_HOLD;
            case IN_TRANSIT -> target == DELIVERED || target == CANCELLED || target == PARTIALLY_SHIPPED;
            case PARTIALLY_SHIPPED -> target == IN_TRANSIT || target == DELIVERED || target == CANCELLED;
            case ON_HOLD -> target == READY_TO_SHIP || target == CANCELLED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}