package tech.kayys.syirkah.support.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.identity.domain.user.UserId;
import tech.kayys.syirkah.support.domain.ticket.TicketId;

import java.util.Objects;

public record AssignTicketCommand(
        TicketId ticketId,
        UserId agentId
) implements Command {

    public AssignTicketCommand {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        Objects.requireNonNull(agentId, "agentId cannot be null");
    }
}