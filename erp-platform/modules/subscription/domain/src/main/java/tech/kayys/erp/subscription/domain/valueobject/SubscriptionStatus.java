package tech.kayys.erp.subscription.domain.valueobject;

/**
 * Status of a subscription.
 */
public enum SubscriptionStatus {
    DRAFT("Draft - being created"),
    ACTIVE("Active - subscription is active"),
    PAUSED("Paused - temporarily suspended"),
    CANCELLED("Cancelled - terminated"),
    EXPIRED("Expired - ended naturally"),
    PAST_DUE("Past Due - payment overdue"),
    IN_GRACE_PERIOD("In Grace Period - still active but payment overdue");

    private final String description;

    SubscriptionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == IN_GRACE_PERIOD;
    }

    public boolean canTransitionTo(SubscriptionStatus target) {
        return switch (this) {
            case DRAFT -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == PAUSED || target == CANCELLED || 
                           target == EXPIRED || target == PAST_DUE;
            case PAUSED -> target == ACTIVE || target == CANCELLED;
            case PAST_DUE -> target == ACTIVE || target == IN_GRACE_PERIOD || 
                             target == CANCELLED || target == EXPIRED;
            case IN_GRACE_PERIOD -> target == ACTIVE || target == CANCELLED || 
                                    target == EXPIRED;
            case CANCELLED, EXPIRED -> false;
        };
    }

    public boolean isTerminal() {
        return this == CANCELLED || this == EXPIRED;
    }
}