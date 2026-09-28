package tech.kayys.syirkah.crm.application.api.query;

import tech.kayys.syirkah.crm.domain.identifier.TicketId;
import tech.kayys.syirkah.foundation.application.query.Query;

public record GetTicketQuery(TicketId ticketId) implements Query {}
