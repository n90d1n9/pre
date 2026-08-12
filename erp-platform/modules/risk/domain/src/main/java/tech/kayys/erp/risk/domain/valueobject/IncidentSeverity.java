package tech.kayys.erp.risk.domain.valueobject;

/**
 * Severity of incidents.
 */
public enum IncidentSeverity {
    CRITICAL(1, "Critical - severe impact"),
    MAJOR(2, "Major - significant impact"),
    MODERATE(3, "Moderate - manageable impact"),
    MINOR(4, "Minor - limited impact"),
    INSIGNIFICANT(5, "Insignificant - negligible impact");

    private final int priority;
    private final String description;

    IncidentSeverity(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCritical() {
        return this == CRITICAL || this == MAJOR;
    }
}