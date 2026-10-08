package tech.kayys.syirkah.workforce.domain.talent.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionCandidateId;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record SuccessionCandidateNominated(
        UUID eventId,
        Instant occurredAt,
        SuccessionCandidateId id,
        SuccessionPlanId planId,
        WorkerId workerId
) implements DomainEvent {
    public SuccessionCandidateNominated(SuccessionCandidateId id, SuccessionPlanId planId, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), id, planId, workerId);
    }
    @Override public String eventType() { return "workforce.talent.succession_candidate.nominated"; }
}
