package tech.kayys.syirkah.asset.application.availability;

import tech.kayys.syirkah.asset.domain.availability.AssetUtilizationRecord;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Success payload for a recorded utilization observation. */
public record UtilizationResult(
        UUID utilizationId,
        UUID assetId,
        String type,
        BigDecimal quantity,
        String unit,
        Instant startsAt,
        Instant endsAt,
        long durationSeconds,
        boolean duplicate
) {

    public static UtilizationResult from(AssetUtilizationRecord record, boolean duplicate) {
        return new UtilizationResult(
                record.id().value(),
                record.assetId(),
                record.type().name(),
                record.quantity(),
                record.unit(),
                record.startsAt(),
                record.endsAt(),
                record.durationSeconds(),
                duplicate);
    }
}