package tech.kayys.syirkah.scheduling.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.scheduling.domain.ScheduleId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleCreated(
        UUID eventId,
        Instant occurredAt,
        ScheduleId scheduleId,
        TenantId tenantId,
        String name,
        LocalDate startDate,
        LocalDate endDate
) implements DomainEvent {
    public ScheduleCreated(ScheduleId scheduleId, TenantId tenantId, String name, LocalDate startDate, LocalDate endDate) {
        this(UUID.randomUUID(), Instant.now(), scheduleId, tenantId, name, startDate, endDate);
    }
    @Override
    public String eventType() {
        return "scheduling.schedule.created";
    }
}
