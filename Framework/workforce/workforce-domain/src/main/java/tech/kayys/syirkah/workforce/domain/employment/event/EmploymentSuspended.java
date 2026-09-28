package tech.kayys.syirkah.workforce.domain.employment.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record EmploymentSuspended(
        UUID eventId,
        Instant occurredAt,
        EmploymentId employmentId,
        WorkerId workerId,
        String reason
) implements DomainEvent {

    public EmploymentSuspended(EmploymentId employmentId, WorkerId workerId, String reason) {
        this(UUID.randomUUID(), Instant.now(), employmentId, workerId, reason);
    }

    @Override
    public String eventType() {
        return "workforce.employment.suspended";
    }
}
