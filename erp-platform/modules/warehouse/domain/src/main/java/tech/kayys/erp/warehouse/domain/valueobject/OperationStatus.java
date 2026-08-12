package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Status of warehouse operations.
 */
public enum OperationStatus {
    PENDING("Pending - waiting to start"),
    IN_PROGRESS("In Progress - currently being processed"),
    COMPLETED("Completed - operation finished"),
    ON_HOLD("On Hold - temporarily paused"),
    CANCELLED("Cancelled - operation voided"),
    REJECTED("Rejected - operation not accepted"),
    PARTIALLY_COMPLETED("Partially Completed - some items processed");

    private final String description;

    OperationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED;
    }
}