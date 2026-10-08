package tech.kayys.syirkah.workforce.domain.benefit.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.benefit.BenefitId;
import tech.kayys.syirkah.workforce.domain.benefit.WorkerBenefitEnrollmentId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WorkerBenefitEnrolled(
        UUID eventId,
        Instant occurredAt,
        WorkerBenefitEnrollmentId enrollmentId,
        WorkerId workerId,
        EmploymentId employmentId,
        BenefitId benefitId,
        LocalDate effectiveFrom
) implements DomainEvent {
    public WorkerBenefitEnrolled(
            WorkerBenefitEnrollmentId enrollmentId,
            WorkerId workerId,
            EmploymentId employmentId,
            BenefitId benefitId,
            LocalDate effectiveFrom
    ) {
        this(UUID.randomUUID(), Instant.now(), enrollmentId, workerId, employmentId, benefitId, effectiveFrom);
    }

    @Override
    public String eventType() { return "workforce.benefit.worker-enrolled"; }
}
