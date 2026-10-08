package tech.kayys.syirkah.workforce.domain.performance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceReviewId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PerformanceReviewSubmitted(
        UUID eventId,
        Instant occurredAt,
        PerformanceReviewId id,
        WorkerId workerId
) implements DomainEvent {
    public PerformanceReviewSubmitted(PerformanceReviewId id, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId);
    }
    @Override public String eventType() { return "workforce.performance.review.submitted"; }
}
