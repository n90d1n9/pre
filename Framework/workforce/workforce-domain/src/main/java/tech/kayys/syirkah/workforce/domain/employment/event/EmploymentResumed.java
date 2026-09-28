package tech.kayys.syirkah.workforce.domain.employment.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record EmploymentResumed(
        UUID eventId,
        Instant occurredAt,
        EmploymentId employmentId,
        WorkerId workerId
) implements DomainEvent {

    public EmploymentResumed(EmploymentId employmentId, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), employmentId, workerId);
    }

    @Override
    public String eventType() {
        return "workforce.employment.resumed";
    }
}
