package tech.kayys.erp.risk.domain.valueobject;

/**
 * Risk severity levels.
 */
public enum RiskLevel {
    CRITICAL(1, "Critical - immediate action required"),
    HIGH(2, "High - urgent attention needed"),
    MEDIUM(3, "Medium - requires management"),
    LOW(4, "Low - acceptable risk"),
    TRIVIAL(5, "Trivial - negligible impact");

    private final int priority;
    private final String description;

    RiskLevel(int priority, String description) {
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
        return this == CRITICAL || this == HIGH;
    }

    public static RiskLevel fromScore(double score) {
        if (score >= 20) return CRITICAL;
        if (score >= 15) return HIGH;
        if (score >= 10) return MEDIUM;
        if (score >= 5) return LOW;
        return TRIVIAL;
    }
}