package tech.kayys.syirkah.support.application.api;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.support.domain.ticket.TicketId;

public interface TicketAccessPolicy {

    Uni<Boolean> canRead(TicketId ticketId, ActorContext actor);

    Uni<Boolean> canComment(TicketId ticketId, ActorContext actor);

    Uni<Boolean> canResolve(TicketId ticketId, ActorContext actor);
}
