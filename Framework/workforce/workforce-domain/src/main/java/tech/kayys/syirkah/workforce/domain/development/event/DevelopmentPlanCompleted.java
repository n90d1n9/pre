package tech.kayys.syirkah.workforce.domain.development.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record DevelopmentPlanCompleted(
        UUID eventId,
        Instant occurredAt,
        DevelopmentPlanId id,
        WorkerId workerId
) implements DomainEvent {
    public DevelopmentPlanCompleted(DevelopmentPlanId id, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId);
    }

    @Override
    public String eventType() {
        return "workforce.development.plan.completed";
    }
}
