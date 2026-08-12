package tech.kayys.erp.project.domain.valueobject;

/**
 * Status of a task.
 */
public enum TaskStatus {
    TODO("To Do - not yet started"),
    IN_PROGRESS("In Progress - being worked on"),
    REVIEW("Review - awaiting review"),
    COMPLETED("Completed - task finished"),
    BLOCKED("Blocked - waiting for resolution"),
    CANCELLED("Cancelled - no longer needed"),
    DEFERRED("Deferred - postponed");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == TODO || this == IN_PROGRESS || this == REVIEW || this == BLOCKED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(TaskStatus target) {
        return switch (this) {
            case TODO -> target == IN_PROGRESS || target == BLOCKED || target == CANCELLED;
            case IN_PROGRESS -> target == REVIEW || target == BLOCKED || target == CANCELLED;
            case REVIEW -> target == COMPLETED || target == IN_PROGRESS || target == CANCELLED;
            case BLOCKED -> target == TODO || target == IN_PROGRESS || target == CANCELLED;
            case COMPLETED, CANCELLED, DEFERRED -> false;
        };
    }
}