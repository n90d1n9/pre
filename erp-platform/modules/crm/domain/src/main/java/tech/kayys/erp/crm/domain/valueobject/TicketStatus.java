package tech.kayys.erp.crm.domain.valueobject;

/**
 * Status of a support ticket.
 */
public enum TicketStatus {
    NEW("New - waiting to be assigned"),
    ASSIGNED("Assigned - assigned to agent"),
    IN_PROGRESS("In Progress - being worked on"),
    PENDING_CUSTOMER("Pending Customer - waiting for response"),
    RESOLVED("Resolved - solution provided"),
    CLOSED("Closed - ticket completed"),
    REOPENED("Reopened - previously resolved, now open"),
    ESCALATED("Escalated - requiring higher level"),
    ON_HOLD("On Hold - waiting for external input");

    private final String description;

    TicketStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != CLOSED && this != RESOLVED;
    }

    public boolean isTerminal() {
        return this == CLOSED;
    }

    public boolean canTransitionTo(TicketStatus target) {
        return switch (this) {
            case NEW -> target == ASSIGNED || target == REOPENED;
            case ASSIGNED -> target == IN_PROGRESS || target == ESCALATED;
            case IN_PROGRESS -> target == PENDING_CUSTOMER || target == RESOLVED || target == ESCALATED || target == ON_HOLD;
            case PENDING_CUSTOMER -> target == IN_PROGRESS || target == CLOSED || target == RESOLVED;
            case RESOLVED -> target == CLOSED || target == REOPENED;
            case REOPENED -> target == IN_PROGRESS || target == RESOLVED || target == CLOSED;
            case ESCALATED -> target == IN_PROGRESS || target == RESOLVED || target == CLOSED;
            case ON_HOLD -> target == IN_PROGRESS || target == RESOLVED || target == CLOSED;
            case CLOSED -> false;
        };
    }
}