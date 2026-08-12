package tech.kayys.erp.subscription.domain.valueobject;

/**
 * Types of subscription plans.
 */
public enum PlanType {
    BASIC("Basic Plan"),
    PROFESSIONAL("Professional Plan"),
    ENTERPRISE("Enterprise Plan"),
    PREMIUM("Premium Plan"),
    CUSTOM("Custom Plan"),
    TRIAL("Trial Plan"),
    EDUCATIONAL("Educational Plan");

    private final String displayName;

    PlanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this plan includes premium features.
     */
    public boolean isPremium() {
        return this == PROFESSIONAL || this == ENTERPRISE || this == PREMIUM;
    }

    /**
     * Gets the priority level (higher = more features).
     */
    public int getPriority() {
        return switch (this) {
            case BASIC -> 1;
            case PROFESSIONAL, EDUCATIONAL -> 2;
            case PREMIUM -> 3;
            case ENTERPRISE -> 4;
            case CUSTOM, TRIAL -> 0;
        };
    }
}