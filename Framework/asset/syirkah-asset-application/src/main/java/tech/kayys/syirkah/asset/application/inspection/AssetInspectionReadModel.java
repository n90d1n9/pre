package tech.kayys.syirkah.asset.application.inspection;

import tech.kayys.syirkah.asset.domain.inspection.AssetCondition;
import tech.kayys.syirkah.asset.domain.inspection.AssetInspection;
import tech.kayys.syirkah.asset.domain.inspection.AssetInspectionStatus;
import tech.kayys.syirkah.asset.domain.inspection.InspectionResult;
import tech.kayys.syirkah.asset.domain.inspection.InspectionType;

import java.time.Instant;
import java.util.UUID;

/** Read model for an inspection (ASSET-20 section 20.22). Never exposes the aggregate. */
public record AssetInspectionReadModel(
        UUID id,
        String tenantId,
        UUID assetId,
        String inspectionNumber,
        InspectionType type,
        AssetInspectionStatus status,
        AssetCondition condition,
        InspectionResult result,
        String inspectorId,
        String notes,
        UUID workOrderId,
        Instant scheduledFor,
        Instant startedAt,
        Instant completedAt,
        Instant cancelledAt
) {

    public static AssetInspectionReadModel from(AssetInspection inspection) {
        return new AssetInspectionReadModel(
                inspection.id().value(), inspection.tenantId(), inspection.assetId(),
                inspection.inspectionNumber(), inspection.type(), inspection.status(),
                inspection.overallCondition(), inspection.result(), inspection.inspectorId(),
                inspection.notes(), inspection.workOrderId(), inspection.scheduledFor(),
                inspection.startedAt(), inspection.completedAt(), inspection.cancelledAt());
    }
}
