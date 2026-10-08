package tech.kayys.syirkah.workforce.domain.development.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentActivityId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record DevelopmentActivityCompleted(
        UUID eventId,
        Instant occurredAt,
        DevelopmentActivityId id,
        WorkerId workerId
) implements DomainEvent {
    public DevelopmentActivityCompleted(DevelopmentActivityId id, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId);
    }

    @Override
    public String eventType() {
        return "workforce.development.activity.completed";
    }
}
