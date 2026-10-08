package tech.kayys.syirkah.support.application.email;

import tech.kayys.syirkah.support.domain.ticket.TicketId;

public record EmailProcessingResult(TicketId ticketId, Outcome outcome) {

    public enum Outcome {
        TICKET_CREATED,
        PUBLIC_REPLY_ADDED,
        DUPLICATE_IGNORED
    }
}
