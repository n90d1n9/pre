package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.TicketId;

/**
 * Command to assign a ticket to an agent.
 */
public record AssignTicketCommand(
        TicketId ticketId,
        String assignedTo
) implements Command<TicketId> {

    public AssignTicketCommand {
        if (ticketId == null) {
            throw new IllegalArgumentException("Ticket ID cannot be null");
        }
        if (assignedTo == null || assignedTo.trim().isEmpty()) {
            throw new IllegalArgumentException("Assigned to cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TicketId ticketId;
        private String assignedTo;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder assignedTo(String assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public AssignTicketCommand build() {
            return new AssignTicketCommand(ticketId, assignedTo);
        }
    }
}