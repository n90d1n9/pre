package tech.kayys.erp.workforce.domain.valueobject;

/**
 * Types of shifts.
 */
public enum ShiftType {
    DAY("Day Shift - 6am to 2pm"),
    AFTERNOON("Afternoon Shift - 2pm to 10pm"),
    NIGHT("Night Shift - 10pm to 6am"),
    SPLIT("Split Shift - broken into two parts"),
    ON_CALL("On Call - as needed"),
    FLEXIBLE("Flexible - variable hours"),
    ROTATING("Rotating - changes periodically"),
    WEEKEND("Weekend Shift"),
    HOLIDAY("Holiday Shift");

    private final String description;

    ShiftType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public double getNightShiftDifferential() {
        return switch (this) {
            case NIGHT -> 0.15; // 15% differential
            case HOLIDAY -> 0.50; // 50% differential
            case WEEKEND -> 0.25; // 25% differential
            default -> 0.0;
        };
    }
}