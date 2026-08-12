package tech.kayys.erp.promotion.domain.valueobject;

/**
 * Status of a promotion.
 */
public enum PromotionStatus {
    DRAFT("Draft - being created"),
    SCHEDULED("Scheduled - waiting to start"),
    ACTIVE("Active - currently running"),
    PAUSED("Paused - temporarily inactive"),
    COMPLETED("Completed - ended naturally"),
    CANCELLED("Cancelled - ended early"),
    EXPIRED("Expired - passed end date");

    private final String description;

    PromotionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isSchedulable() {
        return this == DRAFT || this == SCHEDULED;
    }

    public boolean canTransitionTo(PromotionStatus target) {
        return switch (this) {
            case DRAFT -> target == SCHEDULED || target == CANCELLED;
            case SCHEDULED -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == PAUSED || target == COMPLETED || target == EXPIRED;
            case PAUSED -> target == ACTIVE || target == COMPLETED || target == EXPIRED || target == CANCELLED;
            case COMPLETED, EXPIRED, CANCELLED -> false;
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == EXPIRED || this == CANCELLED;
    }
}