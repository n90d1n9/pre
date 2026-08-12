package tech.kayys.erp.workforce.domain.valueobject;

/**
 * Types of overtime.
 */
public enum OvertimeType {
    WEEKDAY("Weekday Overtime - 1.5x rate"),
    WEEKEND("Weekend Overtime - 2x rate"),
    HOLIDAY("Holiday Overtime - 2x rate"),
    NIGHT("Night Overtime - 1.5x rate"),
    VOLUNTARY("Voluntary Overtime"),
    MANDATORY("Mandatory Overtime"),
    APPROVED("Approved Overtime");

    private final String description;

    OvertimeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public double getRateMultiplier() {
        return switch (this) {
            case WEEKDAY, NIGHT, VOLUNTARY -> 1.5;
            case WEEKEND, HOLIDAY, MANDATORY -> 2.0;
            case APPROVED -> 1.5;
        };
    }
}