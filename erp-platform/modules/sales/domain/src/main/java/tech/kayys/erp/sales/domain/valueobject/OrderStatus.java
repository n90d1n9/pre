package tech.kayys.erp.sales.domain.valueobject;

/**
 * Status of an order in the Sales context.
 */
public enum OrderStatus {
    DRAFT("Draft - being created"),
    SUBMITTED("Submitted - awaiting confirmation"),
    CONFIRMED("Confirmed - approved for processing"),
    PROCESSING("Processing - being fulfilled"),
    SHIPPED("Shipped - in transit"),
    DELIVERED("Delivered - completed"),
    CANCELLED("Cancelled - order voided"),
    REFUNDED("Refunded - money returned");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case DRAFT -> target == SUBMITTED || target == CANCELLED;
            case SUBMITTED -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == SHIPPED || target == CANCELLED;
            case SHIPPED -> target == DELIVERED || target == REFUNDED;
            case DELIVERED, REFUNDED, CANCELLED -> false;
        };
    }

    public boolean isFinal() {
        return this == DELIVERED || this == REFUNDED || this == CANCELLED;
    }

    public boolean isActive() {
        return this != DELIVERED && this != REFUNDED && this != CANCELLED;
    }
}