package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.TicketId;

/**
 * Command to resolve a ticket.
 */
public record ResolveTicketCommand(
        TicketId ticketId,
        String resolution
) implements Command<TicketId> {

    public ResolveTicketCommand {
        if (ticketId == null) {
            throw new IllegalArgumentException("Ticket ID cannot be null");
        }
        if (resolution == null || resolution.trim().isEmpty()) {
            throw new IllegalArgumentException("Resolution cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TicketId ticketId;
        private String resolution;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder resolution(String resolution) {
            this.resolution = resolution;
            return this;
        }

        public ResolveTicketCommand build() {
            return new ResolveTicketCommand(ticketId, resolution);
        }
    }
}