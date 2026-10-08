package tech.kayys.syirkah.workforce.domain.compliance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceAssessmentId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record ComplianceViolationDetected(
        UUID eventId,
        Instant occurredAt,
        ComplianceAssessmentId id,
        WorkerId workerId,
        ComplianceRequirementId requirementId,
        String notes
) implements DomainEvent {
    public ComplianceViolationDetected(ComplianceAssessmentId id, WorkerId workerId, ComplianceRequirementId requirementId, String notes) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, requirementId, notes);
    }

    @Override
    public String eventType() {
        return "workforce.compliance.violation.detected";
    }
}
