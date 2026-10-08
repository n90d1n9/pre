package tech.kayys.syirkah.workforce.domain.learning.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.learning.LearningOfferingId;

import java.time.Instant;
import java.util.UUID;

public record LearningOfferingOpened(
        UUID eventId,
        Instant occurredAt,
        LearningOfferingId id
) implements DomainEvent {
    public LearningOfferingOpened(LearningOfferingId id) {
        this(UUID.randomUUID(), Instant.now(), id);
    }
    @Override public String eventType() { return "workforce.learning.offering.opened"; }
}
