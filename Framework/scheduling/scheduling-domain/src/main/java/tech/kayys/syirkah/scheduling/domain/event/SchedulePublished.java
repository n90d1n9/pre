package tech.kayys.syirkah.scheduling.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.scheduling.domain.ScheduleId;

import java.time.Instant;
import java.util.UUID;

public record SchedulePublished(
        UUID eventId,
        Instant occurredAt,
        ScheduleId scheduleId,
        TenantId tenantId
) implements DomainEvent {
    public SchedulePublished(ScheduleId scheduleId, TenantId tenantId) {
        this(UUID.randomUUID(), Instant.now(), scheduleId, tenantId);
    }
    @Override
    public String eventType() {
        return "scheduling.schedule.published";
    }
}
