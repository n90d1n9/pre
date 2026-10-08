package tech.kayys.syirkah.workforce.domain.performance.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoalId;

import java.time.Instant;
import java.util.UUID;

public record PerformanceGoalAchieved(
        UUID eventId,
        Instant occurredAt,
        PerformanceGoalId id
) implements DomainEvent {
    public PerformanceGoalAchieved(PerformanceGoalId id) {
        this(UUID.randomUUID(), Instant.now(), id);
    }
    @Override public String eventType() { return "workforce.performance.goal.achieved"; }
}
