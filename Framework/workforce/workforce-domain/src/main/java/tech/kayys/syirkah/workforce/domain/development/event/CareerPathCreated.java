package tech.kayys.syirkah.workforce.domain.development.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.development.CareerPathId;

import java.time.Instant;
import java.util.UUID;

public record CareerPathCreated(
        UUID eventId,
        Instant occurredAt,
        CareerPathId id,
        TenantId tenantId,
        String code
) implements DomainEvent {
    public CareerPathCreated(CareerPathId id, TenantId tenantId, String code) {
        this(UUID.randomUUID(), Instant.now(), id, tenantId, code);
    }

    @Override
    public String eventType() {
        return "workforce.development.career_path.created";
    }
}
