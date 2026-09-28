package tech.kayys.syirkah.workforce.domain.employment.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmploymentTerminated(
        UUID eventId,
        Instant occurredAt,
        EmploymentId employmentId,
        WorkerId workerId,
        LocalDate effectiveDate,
        String reason
) implements DomainEvent {

    public EmploymentTerminated(
            EmploymentId employmentId,
            WorkerId workerId,
            LocalDate effectiveDate,
            String reason
    ) {
        this(UUID.randomUUID(), Instant.now(), employmentId, workerId, effectiveDate, reason);
    }

    @Override
    public String eventType() {
        return "workforce.employment.terminated";
    }
}
