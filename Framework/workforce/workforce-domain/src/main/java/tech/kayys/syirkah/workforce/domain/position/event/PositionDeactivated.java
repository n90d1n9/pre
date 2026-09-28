package tech.kayys.syirkah.workforce.domain.position.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.position.PositionId;

import java.time.Instant;
import java.util.UUID;

public record PositionDeactivated(
        UUID eventId,
        Instant occurredAt,
        PositionId positionId
) implements DomainEvent {

    public PositionDeactivated(PositionId positionId) {
        this(UUID.randomUUID(), Instant.now(), positionId);
    }

    @Override
    public String eventType() {
        return "workforce.position.deactivated";
    }
}
