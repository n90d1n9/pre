package tech.kayys.syirkah.workforce.domain.compensation.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.valueobject.Money;
import tech.kayys.syirkah.workforce.domain.compensation.CompensationTermsId;
import tech.kayys.syirkah.workforce.domain.compensation.PayFrequency;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CompensationTermsCreated(
        UUID eventId,
        Instant occurredAt,
        CompensationTermsId compensationTermsId,
        EmploymentId employmentId,
        Money basePay,
        PayFrequency payFrequency,
        LocalDate effectiveFrom
) implements DomainEvent {
    public CompensationTermsCreated(
            CompensationTermsId id,
            EmploymentId employmentId,
            Money basePay,
            PayFrequency payFrequency,
            LocalDate effectiveFrom
    ) {
        this(UUID.randomUUID(), Instant.now(), id, employmentId, basePay, payFrequency, effectiveFrom);
    }

    @Override
    public String eventType() {
        return "workforce.compensation.terms-created";
    }
}
