package tech.kayys.syirkah.workforce.domain.compensation.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.compensation.CompensationTermsId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CompensationTermsEnded(
        UUID eventId,
        Instant occurredAt,
        CompensationTermsId compensationTermsId,
        EmploymentId employmentId,
        LocalDate effectiveTo
) implements DomainEvent {
    public CompensationTermsEnded(
            CompensationTermsId id,
            EmploymentId employmentId,
            LocalDate effectiveTo
    ) {
        this(UUID.randomUUID(), Instant.now(), id, employmentId, effectiveTo);
    }

    @Override
    public String eventType() {
        return "workforce.compensation.terms-ended";
    }
}
