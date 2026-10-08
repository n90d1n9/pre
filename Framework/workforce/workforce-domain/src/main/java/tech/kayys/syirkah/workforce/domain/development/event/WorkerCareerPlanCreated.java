package tech.kayys.syirkah.workforce.domain.development.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.development.WorkerCareerPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record WorkerCareerPlanCreated(
        UUID eventId,
        Instant occurredAt,
        WorkerCareerPlanId id,
        WorkerId workerId
) implements DomainEvent {
    public WorkerCareerPlanCreated(WorkerCareerPlanId id, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId);
    }

    @Override
    public String eventType() {
        return "workforce.development.career_plan.created";
    }
}
