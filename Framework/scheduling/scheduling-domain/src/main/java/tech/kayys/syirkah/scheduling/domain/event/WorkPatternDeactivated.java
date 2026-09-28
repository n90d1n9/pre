package tech.kayys.syirkah.scheduling.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.scheduling.domain.WorkPatternId;

import java.time.Instant;
import java.util.UUID;

public record WorkPatternDeactivated(
        UUID eventId,
        Instant occurredAt,
        WorkPatternId workPatternId
) implements DomainEvent {
    public WorkPatternDeactivated(WorkPatternId workPatternId) {
        this(UUID.randomUUID(), Instant.now(), workPatternId);
    }
    @Override
    public String eventType() {
        return "scheduling.work-pattern.deactivated";
    }
}
