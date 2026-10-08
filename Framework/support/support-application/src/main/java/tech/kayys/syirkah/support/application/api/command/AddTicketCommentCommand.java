package tech.kayys.syirkah.support.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.identity.domain.user.UserId;
import tech.kayys.syirkah.support.domain.ticket.TicketId;

import java.util.Objects;

public record AddTicketCommentCommand(
        TicketId ticketId,
        UserId authorId,
        String body,
        boolean internal
) implements Command {

    public AddTicketCommentCommand {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        Objects.requireNonNull(authorId, "authorId cannot be null");
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("body cannot be blank");
        }
    }
}