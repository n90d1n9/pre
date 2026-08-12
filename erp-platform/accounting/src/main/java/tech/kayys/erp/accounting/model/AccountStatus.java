package tech.kayys.erp.accounting.model;


/**
 * Status of an account in the chart of accounts.
 */
public enum AccountStatus {
    ACTIVE("Active - usable for transactions"),
    INACTIVE("Inactive - cannot be used"),
    CLOSED("Closed - no longer available"),
    ARCHIVED("Archived - historical only");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }
}