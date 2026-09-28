package tech.kayys.syirkah.scheduling.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.scheduling.domain.WorkPatternId;
import tech.kayys.syirkah.scheduling.domain.WorkPatternType;

import java.time.Instant;
import java.util.UUID;

public record WorkPatternCreated(
        UUID eventId,
        Instant occurredAt,
        WorkPatternId workPatternId,
        TenantId tenantId,
        String name,
        WorkPatternType type
) implements DomainEvent {
    public WorkPatternCreated(WorkPatternId workPatternId, TenantId tenantId, String name, WorkPatternType type) {
        this(UUID.randomUUID(), Instant.now(), workPatternId, tenantId, name, type);
    }
    @Override
    public String eventType() {
        return "scheduling.work-pattern.created";
    }
}
