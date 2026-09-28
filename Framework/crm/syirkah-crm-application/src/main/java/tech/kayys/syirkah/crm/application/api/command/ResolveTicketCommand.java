package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.crm.domain.identifier.TicketId;
import tech.kayys.syirkah.foundation.application.command.Command;

public record ResolveTicketCommand(
        TicketId ticketId,
        String resolution
) implements Command {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
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
