package tech.kayys.syirkah.workforce.domain.socialinsurance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.socialinsurance.SocialInsuranceSchemeId;
import tech.kayys.syirkah.workforce.domain.socialinsurance.WorkerSocialInsuranceEnrollmentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WorkerSocialInsuranceEnrolled(
        UUID eventId,
        Instant occurredAt,
        WorkerSocialInsuranceEnrollmentId enrollmentId,
        WorkerId workerId,
        EmploymentId employmentId,
        SocialInsuranceSchemeId schemeId,
        String membershipNumber,
        LocalDate effectiveFrom
) implements DomainEvent {
    public WorkerSocialInsuranceEnrolled(
            WorkerSocialInsuranceEnrollmentId enrollmentId,
            WorkerId workerId,
            EmploymentId employmentId,
            SocialInsuranceSchemeId schemeId,
            String membershipNumber,
            LocalDate effectiveFrom
    ) {
        this(UUID.randomUUID(), Instant.now(), enrollmentId, workerId, employmentId, schemeId, membershipNumber, effectiveFrom);
    }

    @Override
    public String eventType() { return "workforce.socialinsurance.worker-enrolled"; }
}
