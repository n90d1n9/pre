package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.crm.domain.identifier.TicketId;
import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.identity.domain.user.UserId;

public record AssignTicketCommand(
        TicketId ticketId,
        UserId assignedTo
) implements Command {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TicketId ticketId;
        private UserId assignedTo;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder assignedTo(UserId assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public AssignTicketCommand build() {
            return new AssignTicketCommand(ticketId, assignedTo);
        }
    }
}
