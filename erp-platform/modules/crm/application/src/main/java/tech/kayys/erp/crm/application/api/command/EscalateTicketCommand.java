package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.TicketId;

/**
 * Command to escalate a ticket.
 */
public record EscalateTicketCommand(
        TicketId ticketId,
        String escalatedTo,
        String reason
) implements Command<TicketId> {

    public EscalateTicketCommand {
        if (ticketId == null) {
            throw new IllegalArgumentException("Ticket ID cannot be null");
        }
        if (escalatedTo == null || escalatedTo.trim().isEmpty()) {
            throw new IllegalArgumentException("Escalated to cannot be empty");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Escalation reason cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TicketId ticketId;
        private String escalatedTo;
        private String reason;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder escalatedTo(String escalatedTo) {
            this.escalatedTo = escalatedTo;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public EscalateTicketCommand build() {
            return new EscalateTicketCommand(ticketId, escalatedTo, reason);
        }
    }
}