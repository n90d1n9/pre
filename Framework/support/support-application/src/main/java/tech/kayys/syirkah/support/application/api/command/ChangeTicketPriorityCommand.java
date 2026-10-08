package tech.kayys.syirkah.support.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.support.domain.ticket.TicketId;
import tech.kayys.syirkah.support.domain.ticket.TicketPriority;

import java.util.Objects;

public record ChangeTicketPriorityCommand(
        TicketId ticketId,
        TicketPriority priority
) implements Command {

    public ChangeTicketPriorityCommand {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        Objects.requireNonNull(priority, "priority cannot be null");
    }
}