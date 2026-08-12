package tech.kayys.erp.risk.domain.valueobject;

/**
 * Status of an incident.
 */
public enum IncidentStatus {
    REPORTED("Reported - incident reported"),
    UNDER_INVESTIGATION("Under Investigation - being reviewed"),
    ESCALATED("Escalated - requiring management attention"),
    MITIGATED("Mitigated - incident contained"),
    RESOLVED("Resolved - incident closed"),
    CLOSED("Closed - incident finalized");

    private final String description;

    IncidentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != RESOLVED && this != CLOSED;
    }
}