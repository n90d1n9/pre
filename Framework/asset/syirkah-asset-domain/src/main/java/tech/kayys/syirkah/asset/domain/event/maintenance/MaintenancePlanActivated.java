package tech.kayys.syirkah.asset.domain.event.maintenance;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MaintenancePlanActivated(UUID eventId, Instant occurredAt, UUID planId) implements DomainEvent {
    public MaintenancePlanActivated { Objects.requireNonNull(eventId); Objects.requireNonNull(occurredAt); Objects.requireNonNull(planId); }
    @Override public String eventType() { return "maintenance.plan-activated"; }
}
