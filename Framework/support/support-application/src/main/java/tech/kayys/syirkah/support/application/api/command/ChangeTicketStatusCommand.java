package tech.kayys.syirkah.support.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.support.domain.ticket.TicketId;
import tech.kayys.syirkah.support.domain.ticket.TicketStatus;
import tech.kayys.syirkah.support.domain.ticket.WaitingReason;

import java.util.Objects;

public record ChangeTicketStatusCommand(
        TicketId ticketId,
        TicketStatus status,
        WaitingReason waitingReason
) implements Command {

    public ChangeTicketStatusCommand {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        if (status == TicketStatus.WAITING && waitingReason == null) {
            throw new IllegalArgumentException("waitingReason is required for WAITING status");
        }
    }
}
