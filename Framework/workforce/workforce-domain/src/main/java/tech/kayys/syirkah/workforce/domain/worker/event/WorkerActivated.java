package tech.kayys.syirkah.workforce.domain.worker.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record WorkerActivated(
        UUID eventId,
        Instant occurredAt,
        WorkerId workerId
) implements DomainEvent {

    public WorkerActivated(WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), workerId);
    }

    @Override
    public String eventType() {
        return "workforce.worker.activated";
    }
}
