package tech.kayys.syirkah.workforce.domain.compliance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceEvidenceId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record ComplianceEvidenceRecorded(
        UUID eventId,
        Instant occurredAt,
        ComplianceEvidenceId id,
        WorkerId workerId,
        ComplianceRequirementId requirementId
) implements DomainEvent {
    public ComplianceEvidenceRecorded(ComplianceEvidenceId id, WorkerId workerId, ComplianceRequirementId requirementId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, requirementId);
    }

    @Override
    public String eventType() {
        return "workforce.compliance.evidence.recorded";
    }
}
