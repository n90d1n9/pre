package tech.kayys.erp.workforce.domain.valueobject;

/**
 * Status of attendance records.
 */
public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    EARLY_LEAVE("Early Leave"),
    ON_LEAVE("On Leave"),
    ON_BREAK("On Break"),
    TRAINING("Training"),
    BUSINESS_TRIP("Business Trip"),
    HOLIDAY("Holiday"),
    WEEKEND("Weekend"),
    HALF_DAY("Half Day"),
    OVERTIME("Overtime");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPresent() {
        return this == PRESENT || this == LATE || this == EARLY_LEAVE || this == OVERTIME;
    }

    public boolean isAbsent() {
        return this == ABSENT;
    }

    public boolean isWorking() {
        return this == PRESENT || this == LATE || this == EARLY_LEAVE || 
               this == OVERTIME || this == TRAINING || this == BUSINESS_TRIP;
    }
}