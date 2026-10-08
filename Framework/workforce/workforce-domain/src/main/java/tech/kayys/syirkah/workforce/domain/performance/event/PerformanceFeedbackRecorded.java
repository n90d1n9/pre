package tech.kayys.syirkah.workforce.domain.performance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.performance.FeedbackType;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceFeedbackId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PerformanceFeedbackRecorded(
        UUID eventId,
        Instant occurredAt,
        PerformanceFeedbackId id,
        WorkerId workerId,
        FeedbackType type
) implements DomainEvent {
    public PerformanceFeedbackRecorded(PerformanceFeedbackId id, WorkerId workerId, FeedbackType type) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, type);
    }
    @Override public String eventType() { return "workforce.performance.feedback.recorded"; }
}
