package tech.kayys.syirkah.support.application.api.query;

import tech.kayys.syirkah.foundation.application.query.Query;
import tech.kayys.syirkah.support.domain.ticket.TicketId;

import java.util.Objects;

public record GetPortalTicketQuery(TicketId ticketId) implements Query {
    public GetPortalTicketQuery {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
    }
}
