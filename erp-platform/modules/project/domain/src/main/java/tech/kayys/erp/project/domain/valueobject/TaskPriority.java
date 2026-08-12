package tech.kayys.erp.project.domain.valueobject;

/**
 * Priority of a task.
 */
public enum TaskPriority {
    CRITICAL(1, "Critical - must be done immediately"),
    HIGH(2, "High - urgent"),
    MEDIUM(3, "Medium - normal priority"),
    LOW(4, "Low - can be delayed"),
    TRIVIAL(5, "Trivial - nice to have");

    private final int value;
    private final String description;

    TaskPriority(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCritical() {
        return this == CRITICAL || this == HIGH;
    }
}