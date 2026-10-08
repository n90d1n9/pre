package tech.kayys.syirkah.workforce.domain.performance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceCycleId;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceReviewId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PerformanceReviewCreated(
        UUID eventId,
        Instant occurredAt,
        PerformanceReviewId id,
        WorkerId workerId,
        PerformanceCycleId cycleId
) implements DomainEvent {
    public PerformanceReviewCreated(PerformanceReviewId id, WorkerId workerId, PerformanceCycleId cycleId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, cycleId);
    }
    @Override public String eventType() { return "workforce.performance.review.created"; }
}
