package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Result of quality check.
 */
public enum QualityCheckResult {
    PASSED("Passed - meets quality standards"),
    FAILED("Failed - does not meet quality standards"),
    PARTIAL("Partial - partially passed"),
    CONDITIONAL("Conditional - accepted with conditions"),
    REJECTED("Rejected - rejected for quality issues");

    private final String description;

    QualityCheckResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAccepted() {
        return this == PASSED || this == PARTIAL || this == CONDITIONAL;
    }

    public boolean isRejected() {
        return this == FAILED || this == REJECTED;
    }
}