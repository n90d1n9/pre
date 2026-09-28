package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.crm.domain.identifier.TicketId;
import tech.kayys.syirkah.foundation.application.command.Command;

public record CloseTicketCommand(
        TicketId ticketId,
        String closedBy
) implements Command {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TicketId ticketId;
        private String closedBy;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder closedBy(String closedBy) {
            this.closedBy = closedBy;
            return this;
        }

        public CloseTicketCommand build() {
            return new CloseTicketCommand(ticketId, closedBy);
        }
    }
}
