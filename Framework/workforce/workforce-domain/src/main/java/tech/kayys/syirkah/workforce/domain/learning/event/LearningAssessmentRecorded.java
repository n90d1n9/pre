package tech.kayys.syirkah.workforce.domain.learning.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.learning.LearningAssessmentId;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;

import java.time.Instant;
import java.util.UUID;

public record LearningAssessmentRecorded(
        UUID eventId,
        Instant occurredAt,
        LearningAssessmentId id,
        LearningEnrollmentId enrollmentId,
        boolean passed
) implements DomainEvent {
    public LearningAssessmentRecorded(LearningAssessmentId id, LearningEnrollmentId enrollmentId, boolean passed) {
        this(UUID.randomUUID(), Instant.now(), id, enrollmentId, passed);
    }
    @Override public String eventType() { return "workforce.learning.assessment.recorded"; }
}
