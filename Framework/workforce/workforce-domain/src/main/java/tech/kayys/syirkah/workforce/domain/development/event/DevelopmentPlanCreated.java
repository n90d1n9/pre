package tech.kayys.syirkah.workforce.domain.development.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record DevelopmentPlanCreated(
        UUID eventId,
        Instant occurredAt,
        DevelopmentPlanId id,
        WorkerId workerId,
        int cycleYear
) implements DomainEvent {
    public DevelopmentPlanCreated(DevelopmentPlanId id, WorkerId workerId, int cycleYear) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, cycleYear);
    }

    @Override
    public String eventType() {
        return "workforce.development.plan.created";
    }
}
