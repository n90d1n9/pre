package tech.kayys.syirkah.support.domain.ticket.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.support.domain.ticket.TicketId;
import tech.kayys.syirkah.support.domain.ticket.TicketStatus;

import java.time.Instant;
import java.util.UUID;

public record TicketStatusChanged(
        UUID eventId,
        Instant occurredAt,
        TicketId ticketId,
        TicketStatus previousStatus,
        TicketStatus newStatus
) implements DomainEvent {
    @Override
    public String eventType() {
        return "support.ticket-status-changed";
    }
}
