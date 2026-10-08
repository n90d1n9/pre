package tech.kayys.syirkah.asset.application.availability;

import tech.kayys.syirkah.asset.domain.availability.AssetAvailabilityReason;
import tech.kayys.syirkah.asset.domain.valueobject.AssetStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Read model answering "can this asset be used, and until when?" (ASSET-26 §18).
 *
 * <p>Optimized for {@code GET /assets/{id}/availability} and dashboards. The
 * aggregate is never returned from this query.</p>
 */
public record AssetAvailabilityView(
        UUID assetId,
        String tenantId,
        String assetNumber,
        String assetName,
        AssetStatus assetStatus,
        boolean available,
        Instant availableFrom,
        Instant unavailableFrom,
        Instant unavailableUntil,
        AssetAvailabilityReason reason
) {
}