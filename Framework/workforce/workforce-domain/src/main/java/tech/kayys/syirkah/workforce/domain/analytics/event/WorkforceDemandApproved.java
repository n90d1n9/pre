package tech.kayys.syirkah.workforce.domain.analytics.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceDemandId;

import java.time.Instant;
import java.util.UUID;

public record WorkforceDemandApproved(
        UUID eventId,
        Instant occurredAt,
        WorkforceDemandId id,
        String approvedBy
) implements DomainEvent {
    public WorkforceDemandApproved(WorkforceDemandId id, String approvedBy) {
        this(UUID.randomUUID(), Instant.now(), id, approvedBy);
    }
    @Override public String eventType() { return "workforce.analytics.demand.approved"; }
}
