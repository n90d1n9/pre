package tech.kayys.syirkah.workforce.domain.benefit.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.benefit.BenefitId;
import tech.kayys.syirkah.workforce.domain.benefit.WorkerBenefitEnrollmentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WorkerBenefitEnded(
        UUID eventId,
        Instant occurredAt,
        WorkerBenefitEnrollmentId enrollmentId,
        WorkerId workerId,
        BenefitId benefitId,
        LocalDate effectiveTo
) implements DomainEvent {
    public WorkerBenefitEnded(
            WorkerBenefitEnrollmentId enrollmentId,
            WorkerId workerId,
            BenefitId benefitId,
            LocalDate effectiveTo
    ) {
        this(UUID.randomUUID(), Instant.now(), enrollmentId, workerId, benefitId, effectiveTo);
    }

    @Override
    public String eventType() { return "workforce.benefit.worker-ended"; }
}
