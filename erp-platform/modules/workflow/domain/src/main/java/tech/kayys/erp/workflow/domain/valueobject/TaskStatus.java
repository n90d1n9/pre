package tech.kayys.erp.workflow.domain.valueobject;

/**
 * Status of a workflow task.
 */
public enum TaskStatus {
    PENDING("Pending - not yet started"),
    IN_PROGRESS("In Progress - being worked on"),
    COMPLETED("Completed - finished"),
    REJECTED("Rejected - not approved"),
    CANCELLED("Cancelled - no longer needed"),
    ON_HOLD("On Hold - waiting"),
    ESCALATED("Escalated - requiring attention"),
    ASSIGNED("Assigned - assigned to user");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == ON_HOLD || this == ESCALATED || this == ASSIGNED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED;
    }
}