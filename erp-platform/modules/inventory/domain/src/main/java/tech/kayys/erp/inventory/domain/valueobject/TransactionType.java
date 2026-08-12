package tech.kayys.erp.inventory.domain.valueobject;

/**
 * Types of inventory transactions.
 */
public enum TransactionType {
    RECEIPT("Receipt - stock added"),
    ISSUE("Issue - stock removed"),
    TRANSFER("Transfer - moved between warehouses"),
    ADJUSTMENT("Adjustment - correction"),
    RETURN("Return - returned to warehouse"),
    RESERVATION("Reservation - allocated for order"),
    RELEASE("Release - reservation released"),
    SCRAP("Scrap - discarded inventory"),
    COUNT("Count - physical inventory count");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAddition() {
        return this == RECEIPT || this == RETURN || this == ADJUSTMENT && this != SCRAP;
    }

    public boolean isRemoval() {
        return this == ISSUE || this == SCRAP || this == TRANSFER;
    }

    public boolean isReservation() {
        return this == RESERVATION || this == RELEASE;
    }
}