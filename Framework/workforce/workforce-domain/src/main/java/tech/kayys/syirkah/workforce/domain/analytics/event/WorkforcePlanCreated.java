package tech.kayys.syirkah.workforce.domain.analytics.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforcePlanId;

import java.time.Instant;
import java.util.UUID;

public record WorkforcePlanCreated(
        UUID eventId,
        Instant occurredAt,
        WorkforcePlanId id,
        TenantId tenantId,
        String code
) implements DomainEvent {
    public WorkforcePlanCreated(WorkforcePlanId id, TenantId tenantId, String code) {
        this(UUID.randomUUID(), Instant.now(), id, tenantId, code);
    }
    @Override public String eventType() { return "workforce.analytics.plan.created"; }
}
