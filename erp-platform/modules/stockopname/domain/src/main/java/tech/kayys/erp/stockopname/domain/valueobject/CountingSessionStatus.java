package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Status of a counting session.
 */
public enum CountingSessionStatus {
    PLANNED("Planned - session scheduled"),
    IN_PROGRESS("In Progress - counting underway"),
    PARTIALLY_COMPLETED("Partially Completed - some items counted"),
    COMPLETED("Completed - all items counted"),
    VERIFIED("Verified - counts verified"),
    ADJUSTED("Adjusted - inventory adjusted"),
    CANCELLED("Cancelled - session cancelled"),
    REOPENED("Reopened - session reopened for corrections");

    private final String description;

    CountingSessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PLANNED || this == IN_PROGRESS || this == PARTIALLY_COMPLETED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == VERIFIED || this == ADJUSTED || this == CANCELLED;
    }

    public boolean canTransitionTo(CountingSessionStatus target) {
        return switch (this) {
            case PLANNED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == PARTIALLY_COMPLETED || target == COMPLETED || target == CANCELLED;
            case PARTIALLY_COMPLETED -> target == COMPLETED || target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED -> target == VERIFIED || target == REOPENED || target == CANCELLED;
            case VERIFIED -> target == ADJUSTED || target == REOPENED || target == CANCELLED;
            case ADJUSTED, CANCELLED -> false;
            case REOPENED -> target == IN_PROGRESS || target == COMPLETED || target == CANCELLED;
        };
    }
}