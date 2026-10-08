package tech.kayys.syirkah.asset.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Raised when a document reference is detached from an asset (ASSET-24 §27). */
public record AssetDocumentDetached(
        UUID eventId,
        Instant occurredAt,
        UUID assetId,
        UUID documentId
) implements DomainEvent {

    public AssetDocumentDetached {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        Objects.requireNonNull(assetId, "assetId cannot be null");
        Objects.requireNonNull(documentId, "documentId cannot be null");
    }

    @Override
    public String eventType() {
        return "asset.document-detached";
    }
}
