package tech.kayys.syirkah.asset.application.inspection;

import tech.kayys.syirkah.asset.domain.inspection.InspectionType;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CreateAssetInspectionCommand(
        String tenantId,
        UUID assetId,
        InspectionType type,
        Instant scheduledFor,
        String inspectorId,
        String notes,
        UUID workOrderId
) implements Command {

    public CreateAssetInspectionCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(assetId, "assetId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
    }
}
