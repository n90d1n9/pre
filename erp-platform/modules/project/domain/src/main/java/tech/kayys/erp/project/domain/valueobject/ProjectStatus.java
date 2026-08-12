package tech.kayys.erp.project.domain.valueobject;

/**
 * Status of a project.
 */
public enum ProjectStatus {
    PLANNING("Planning - project being defined"),
    APPROVED("Approved - project approved"),
    IN_PROGRESS("In Progress - project underway"),
    ON_HOLD("On Hold - temporarily paused"),
    REVIEW("Review - under review"),
    COMPLETED("Completed - project finished"),
    CANCELLED("Cancelled - project terminated"),
    ARCHIVED("Archived - no longer active");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PLANNING || this == APPROVED || this == IN_PROGRESS || this == REVIEW;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == ARCHIVED;
    }

    public boolean canTransitionTo(ProjectStatus target) {
        return switch (this) {
            case PLANNING -> target == APPROVED || target == CANCELLED;
            case APPROVED -> target == IN_PROGRESS || target == ON_HOLD || target == CANCELLED;
            case IN_PROGRESS -> target == REVIEW || target == ON_HOLD || target == CANCELLED;
            case ON_HOLD -> target == IN_PROGRESS || target == CANCELLED;
            case REVIEW -> target == IN_PROGRESS || target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED, ARCHIVED -> false;
        };
    }
}