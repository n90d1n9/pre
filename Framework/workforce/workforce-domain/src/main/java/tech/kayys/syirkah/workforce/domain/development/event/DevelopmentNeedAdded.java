package tech.kayys.syirkah.workforce.domain.development.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentNeedId;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record DevelopmentNeedAdded(
        UUID eventId,
        Instant occurredAt,
        DevelopmentNeedId id,
        DevelopmentPlanId planId,
        WorkerId workerId
) implements DomainEvent {
    public DevelopmentNeedAdded(DevelopmentNeedId id, DevelopmentPlanId planId, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), id, planId, workerId);
    }

    @Override
    public String eventType() {
        return "workforce.development.need.added";
    }
}
