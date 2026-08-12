package tech.kayys.erp.employee.domain.valueobject;

/**
 * Types of leave.
 */
public enum LeaveType {
    ANNUAL("Annual Leave"),
    SICK("Sick Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    ADOPTION("Adoption Leave"),
    COMPASSIONATE("Compassionate Leave"),
    EMERGENCY("Emergency Leave"),
    STUDY("Study Leave"),
    UNSCHEDULED("Unscheduled Leave"),
    PUBLIC_HOLIDAY("Public Holiday"),
    UNPAID("Unpaid Leave");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPaid() {
        return this != UNSCHEDULED && this != UNPAID;
    }

    public boolean requiresApproval() {
        return this != PUBLIC_HOLIDAY;
    }
}