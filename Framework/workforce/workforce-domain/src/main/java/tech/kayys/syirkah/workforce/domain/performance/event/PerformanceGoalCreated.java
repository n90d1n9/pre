package tech.kayys.syirkah.workforce.domain.performance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceCycleId;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoalId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PerformanceGoalCreated(
        UUID eventId,
        Instant occurredAt,
        PerformanceGoalId id,
        WorkerId workerId,
        PerformanceCycleId cycleId
) implements DomainEvent {
    public PerformanceGoalCreated(PerformanceGoalId id, WorkerId workerId, PerformanceCycleId cycleId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, cycleId);
    }
    @Override public String eventType() { return "workforce.performance.goal.created"; }
}
