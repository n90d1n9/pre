package tech.kayys.syirkah.support.domain.ticket.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.support.domain.ticket.TicketId;
import tech.kayys.syirkah.support.domain.ticket.TicketPriority;
import tech.kayys.syirkah.support.domain.ticket.TicketType;

import java.time.Instant;
import java.util.UUID;

public record TicketCreated(
        UUID eventId,
        Instant occurredAt,
        TicketId ticketId,
        TicketType type,
        TicketPriority priority
) implements DomainEvent {
    @Override
    public String eventType() {
        return "support.ticket-created";
    }
}
