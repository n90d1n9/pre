package tech.kayys.syirkah.workforce.domain.learning.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;
import tech.kayys.syirkah.workforce.domain.learning.LearningOfferingId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record LearningEnrolled(
        UUID eventId,
        Instant occurredAt,
        LearningEnrollmentId id,
        WorkerId workerId,
        LearningOfferingId offeringId
) implements DomainEvent {
    public LearningEnrolled(LearningEnrollmentId id, WorkerId workerId, LearningOfferingId offeringId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, offeringId);
    }
    @Override public String eventType() { return "workforce.learning.enrolled"; }
}
