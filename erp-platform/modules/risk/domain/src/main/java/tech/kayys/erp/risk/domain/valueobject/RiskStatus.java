package tech.kayys.erp.risk.domain.valueobject;

/**
 * Status of a risk.
 */
public enum RiskStatus {
    IDENTIFIED("Identified - risk has been identified"),
    UNDER_REVIEW("Under Review - being assessed"),
    ASSESSED("Assessed - risk evaluated"),
    MITIGATING("Mitigating - mitigation in progress"),
    MITIGATED("Mitigated - risk controlled"),
    ACCEPTED("Accepted - risk accepted"),
    TRANSFERRED("Transferred - risk transferred"),
    RESOLVED("Resolved - risk closed"),
    REJECTED("Rejected - not a valid risk");

    private final String description;

    RiskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != RESOLVED && this != REJECTED && this != MITIGATED && this != ACCEPTED;
    }

    public boolean isTerminal() {
        return this == RESOLVED || this == REJECTED;
    }
}