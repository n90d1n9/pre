package tech.kayys.erp.crm.domain.valueobject;

/**
 * SLA status for support tickets.
 */
public enum SLAStatus {
    WITHIN_SLA("Within SLA - on track"),
    AT_RISK("At Risk - approaching deadline"),
    BREACHED("Breached - SLA violated");

    private final String description;

    SLAStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}