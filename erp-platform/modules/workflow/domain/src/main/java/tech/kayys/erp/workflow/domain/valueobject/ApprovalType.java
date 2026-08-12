package tech.kayys.erp.workflow.domain.valueobject;

/**
 * Types of approval processes.
 */
public enum ApprovalType {
    SINGLE("Single - one approver"),
    SEQUENTIAL("Sequential - ordered approvers"),
    PARALLEL("Parallel - all approve simultaneously"),
    ANY("Any - first approval accepted"),
    MAJORITY("Majority - majority must approve"),
    UNANIMOUS("Unanimous - all must approve"),
    ESCALATION("Escalation - escalates if not approved");

    private final String description;

    ApprovalType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}