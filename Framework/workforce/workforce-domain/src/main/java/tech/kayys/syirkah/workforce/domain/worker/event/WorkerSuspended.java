package tech.kayys.syirkah.workforce.domain.worker.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record WorkerSuspended(
        UUID eventId,
        Instant occurredAt,
        WorkerId workerId,
        String reason
) implements DomainEvent {

    public WorkerSuspended(WorkerId workerId, String reason) {
        this(UUID.randomUUID(), Instant.now(), workerId, reason);
    }

    @Override
    public String eventType() {
        return "workforce.worker.suspended";
    }
}
