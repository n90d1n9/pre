package tech.kayys.erp.subscription.domain.valueobject;

/**
 * Billing cycle for subscriptions.
 */
public enum BillingCycle {
    MONTHLY(1, "Monthly"),
    QUARTERLY(3, "Quarterly"),
    SEMI_ANNUAL(6, "Semi-Annual"),
    ANNUAL(12, "Annual"),
    BIENNIAL(24, "Biennial"),
    TRIENNIAL(36, "Triennial");

    private final int months;
    private final String displayName;

    BillingCycle(int months, String displayName) {
        this.months = months;
        this.displayName = displayName;
    }

    public int getMonths() {
        return months;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the prorated discount for the billing cycle.
     * Annual and longer cycles typically have discounts.
     */
    public double getDiscountFactor() {
        return switch (this) {
            case MONTHLY -> 0.0;
            case QUARTERLY -> 0.05;
            case SEMI_ANNUAL -> 0.10;
            case ANNUAL -> 0.15;
            case BIENNIAL -> 0.20;
            case TRIENNIAL -> 0.25;
        };
    }

    /**
     * Gets the next billing date based on current date.
     */
    public java.time.LocalDate getNextBillingDate(java.time.LocalDate currentDate) {
        return currentDate.plusMonths(months);
    }
}