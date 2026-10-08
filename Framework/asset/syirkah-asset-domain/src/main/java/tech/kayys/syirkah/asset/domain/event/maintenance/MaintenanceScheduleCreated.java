package tech.kayys.syirkah.asset.domain.event.maintenance;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MaintenanceScheduleCreated(UUID eventId, Instant occurredAt, UUID scheduleId, UUID assetId, UUID planId) implements DomainEvent {
    public MaintenanceScheduleCreated { Objects.requireNonNull(eventId); Objects.requireNonNull(occurredAt); Objects.requireNonNull(scheduleId); Objects.requireNonNull(assetId); Objects.requireNonNull(planId); }
    @Override public String eventType() { return "maintenance.schedule-created"; }
}
