package tech.kayys.syirkah.asset.domain.event.warranty;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record WarrantyActivated(UUID eventId, Instant occurredAt, UUID warrantyId) implements DomainEvent {
    public WarrantyActivated { Objects.requireNonNull(eventId); Objects.requireNonNull(occurredAt); Objects.requireNonNull(warrantyId); }
    @Override public String eventType() { return "asset.warranty-activated"; }
}
