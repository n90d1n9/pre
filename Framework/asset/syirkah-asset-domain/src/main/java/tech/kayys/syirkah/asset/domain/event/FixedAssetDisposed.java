package tech.kayys.syirkah.asset.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Accounting → Asset: a fixed asset was disposed (ASSET-25 §22). Gain/loss stays Accounting-owned. */
public record FixedAssetDisposed(
        UUID eventId,
        Instant occurredAt,
        UUID accountingAssetId,
        UUID operationalAssetId,
        BigDecimal proceeds,
        String currency
) implements DomainEvent {

    public FixedAssetDisposed {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        Objects.requireNonNull(accountingAssetId, "accountingAssetId cannot be null");
        Objects.requireNonNull(operationalAssetId, "operationalAssetId cannot be null");
    }

    @Override
    public String eventType() {
        return "accounting.fixed-asset-disposed";
    }
}
