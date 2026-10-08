package tech.kayys.syirkah.asset.domain.event.warranty;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record WarrantyClaimRejected(UUID eventId, Instant occurredAt, UUID claimId) implements DomainEvent {
    public WarrantyClaimRejected { Objects.requireNonNull(eventId); Objects.requireNonNull(occurredAt); Objects.requireNonNull(claimId); }
    @Override public String eventType() { return "asset.warranty-claim-rejected"; }
}
