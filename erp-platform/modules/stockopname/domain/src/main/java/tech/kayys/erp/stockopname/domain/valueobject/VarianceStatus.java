package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Status of counting variance.
 */
public enum VarianceStatus {
    NO_VARIANCE("No Variance"),
    APPROVED("Approved - variance accepted"),
    REJECTED("Rejected - variance rejected"),
    PENDING_REVIEW("Pending Review - under investigation"),
    UNDER_INVESTIGATION("Under Investigation"),
    ADJUSTED("Adjusted - variance corrected"),
    ESCALATED("Escalated - requiring management attention");

    private final String description;

    VarianceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isResolved() {
        return this == NO_VARIANCE || this == APPROVED || this == ADJUSTED;
    }
}