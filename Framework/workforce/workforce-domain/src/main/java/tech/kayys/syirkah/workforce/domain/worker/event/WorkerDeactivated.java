package tech.kayys.syirkah.workforce.domain.worker.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record WorkerDeactivated(
        UUID eventId,
        Instant occurredAt,
        WorkerId workerId,
        String reason
) implements DomainEvent {

    public WorkerDeactivated(WorkerId workerId, String reason) {
        this(UUID.randomUUID(), Instant.now(), workerId, reason);
    }

    @Override
    public String eventType() {
        return "workforce.worker.deactivated";
    }
}
