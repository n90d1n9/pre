package tech.kayys.syirkah.workforce.domain.worker.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.ref.PersonRef;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerType;

import java.time.Instant;
import java.util.UUID;

public record WorkerCreated(
        UUID eventId,
        Instant occurredAt,
        WorkerId workerId,
        TenantId tenantId,
        PersonRef person,
        WorkerType type
) implements DomainEvent {

    public WorkerCreated(WorkerId workerId, TenantId tenantId, PersonRef person, WorkerType type) {
        this(UUID.randomUUID(), Instant.now(), workerId, tenantId, person, type);
    }

    @Override
    public String eventType() {
        return "workforce.worker.created";
    }
}
