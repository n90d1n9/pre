package tech.kayys.syirkah.asset.application.maintenance.workorder;

import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenancePriority;
import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrderType;
import tech.kayys.syirkah.foundation.application.command.Command;
import java.util.Objects;
import java.util.UUID;

public record CreateMaintenanceWorkOrderCommand(
        String tenantId, UUID assetId, String workOrderNumber, String title,
        String description, MaintenanceWorkOrderType type, MaintenancePriority priority,
        String requestedBy) implements Command {
    public CreateMaintenanceWorkOrderCommand {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(assetId, "assetId");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(priority, "priority");
    }
}
